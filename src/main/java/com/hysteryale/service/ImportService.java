package com.hysteryale.service;

import com.hysteryale.model.Region;
import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.competitor.ForeCastValue;
import com.hysteryale.repository.CompetitorPricingRepository;
import com.hysteryale.utils.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImportService extends BasedService {

    @Resource
    CompetitorPricingRepository competitorPricingRepository;
    @Resource
    RegionService regionService;


    public void getOrderColumnsName(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) {
        for (int i = 0; i < 50; i++) {
            if (row.getCell(i) != null) {
                String columnName = row.getCell(i).getStringCellValue().trim();
                if (ORDER_COLUMNS_NAME.containsKey(columnName))
                    continue;
                ORDER_COLUMNS_NAME.put(columnName, i);
            }
        }
    }

    public List<String> getAllFilesInFolder(String folderPath, int state) {
        Pattern pattern;

        switch (state) {
            case 1:
                pattern = Pattern.compile(".*Final.*(.xlsx)$");
                break;
            case 2:
                pattern = Pattern.compile("^01.*(.xlsx)$");
                break;
            case 3:
                pattern = Pattern.compile("^Competitor.*(.xlsx)$");
                break;
            default:
                pattern = Pattern.compile(".*(.xlsx)$");
                break;
        }

        List<String> fileList = new ArrayList<>();
        Matcher matcher;
        try {
            DirectoryStream<Path> folder = Files.newDirectoryStream(Paths.get(folderPath));
            for (Path path : folder) {
                matcher = pattern.matcher(path.getFileName().toString());
                if (matcher.matches())
                    fileList.add(path.getFileName().toString());
                else
                    logError("Wrong formatted file's name: " + path.getFileName().toString());
            }
        } catch (Exception e) {
            logInfo(e.getMessage());

        }
        return fileList;
    }

    public List<CompetitorPricing> mapExcelDataIntoOrderObject(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) {
        List<CompetitorPricing> competitorPricingList = new ArrayList<>();

        Cell cellRegion = row.getCell(ORDER_COLUMNS_NAME.get("Region"));
        String region = cellRegion.getStringCellValue();

        Cell cellCompetitorName = row.getCell(ORDER_COLUMNS_NAME.get("Brand"));
        String competitorName = cellCompetitorName.getStringCellValue();

        boolean isChineseBrand = competitorName.contains("Heli") || competitorName.contains("HeLi") || competitorName.contains("Hangcha") || competitorName.contains("Hang Cha");
        Cell cellClass = row.getCell(ORDER_COLUMNS_NAME.get("Class"));
        String clazz = cellClass.getStringCellValue();

        Double leadTime = null;
        Cell cellLeadTime = row.getCell(ORDER_COLUMNS_NAME.get("Lead Time"));
        if (cellLeadTime != null && cellLeadTime.getCellType() == CellType.NUMERIC) {
            leadTime = cellLeadTime.getNumericCellValue();
        }

        double dealerStreetPricing = row.getCell(ORDER_COLUMNS_NAME.get("Price (USD)")).getNumericCellValue();

        // 2 fields below are hard-coded, will be modified later
        double percentageDealerPremium = 0.1;
        double dealerNet = 10000;

        double handlingCost = dealerStreetPricing - dealerNet * (1 + percentageDealerPremium);
        double dealerPricingPremium = dealerStreetPricing - (dealerNet + handlingCost);
        double dealerPricingPremiumPercentage = dealerPricingPremium / dealerStreetPricing;



        String category = row.getCell(ORDER_COLUMNS_NAME.get("Category")).getStringCellValue();
        String country = row.getCell(ORDER_COLUMNS_NAME.get("Country"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

        String seriesString;
        Cell cellSeries = row.getCell(ORDER_COLUMNS_NAME.get("HYG Series"));
        if (cellSeries != null && cellSeries.getCellType() == CellType.STRING) {
            seriesString = cellSeries.getStringCellValue();
            StringTokenizer stk = new StringTokenizer(seriesString, "/");
            while (stk.hasMoreTokens()) {

                CompetitorPricing cp = new CompetitorPricing(region, clazz, leadTime, stk.nextToken());
                cp.setCompetitorName(competitorName);
                cp.setDealerStreetPricing(dealerStreetPricing);
                cp.setDealerHandlingCost(handlingCost);
                cp.setDealerPremiumPercentage(percentageDealerPremium);

                cp.setDealerHandlingCost(handlingCost);
                cp.setDealerPricingPremium(dealerPricingPremium);
                cp.setDealerPricingPremiumPercentage(dealerPricingPremiumPercentage);

                cp.setCategory(category);
                cp.setCountry(country);
                competitorPricingList.add(cp);

            }
        } else {
            CompetitorPricing cp = new CompetitorPricing(region, clazz, leadTime);
            competitorPricingList.add(cp);
        }

        return competitorPricingList;
    }

    public void importCompetitorPricing() throws IOException {

        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.competitor-pricing");

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, 3);
        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }
            logInfo("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(pathFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            HashMap<String, Integer> COMPETITOR_COLUMNS_NAME = new HashMap<>();
            Sheet competitorSheet = workbook.getSheet("Competitor Pricing Database");

            List<ForeCastValue> foreCastValues = loadForecastForCompetitorPricingFromFile();
            List<CompetitorPricing> competitorPricingList = new ArrayList<>();

            for (Row row : competitorSheet) {
                if (row.getRowNum() == 0) getOrderColumnsName(row, COMPETITOR_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 0) {
                    List<CompetitorPricing> competitorPricings = mapExcelDataIntoOrderObject(row, COMPETITOR_COLUMNS_NAME);
                    for (CompetitorPricing competitorPricing : competitorPricings) {
                        // if it has series -> assign ForeCastValue
                        if (competitorPricing.getSeries() != null) {
                            String strRegion = competitorPricing.getRegion();
                            String metaSeries = competitorPricing.getSeries().substring(1); // extract metaSeries from series

                            ForeCastValue foreCast2022 = findForeCastValue(foreCastValues, strRegion, metaSeries, 2022);
                            ForeCastValue foreCast2023 = findForeCastValue(foreCastValues, strRegion, metaSeries, 2023);
                            ForeCastValue foreCast2024 = findForeCastValue(foreCastValues, strRegion, metaSeries, 2024);

                            competitorPricing.setActual(foreCast2022 == null ? 0 : foreCast2022.getQuantity());
                            competitorPricing.setAOPF(foreCast2023 == null ? 0 : foreCast2023.getQuantity());
                            competitorPricing.setLRFF(foreCast2024 == null ? 0 : foreCast2024.getQuantity());
                            competitorPricing.setPlant(foreCast2024 == null ? null : foreCast2024.getPlant());
                        }
                        competitorPricingList.add(competitorPricing);
                    }
                }
            }
            competitorPricingRepository.saveAll(competitorPricingList);
            updateStateImportFile(pathFile);
        }
    }

    /**
     * Find a forecast value by Region and Series, year is an option if year is empty then we get all years
     */
    private ForeCastValue findForeCastValue(List<ForeCastValue> foreCastValues, String strRegion, String metaSeries, int year){
        for(ForeCastValue foreCastValue : foreCastValues) {
            if(foreCastValue.getRegion().getRegion().equals(strRegion) && foreCastValue.getMetaSeries().equals(metaSeries) && foreCastValue.getYear() == year)
                return foreCastValue;
        }
       return null;
    }

    private List<ForeCastValue> loadForecastForCompetitorPricingFromFile() throws IOException {

        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.forecast-pricing");
        List<String> fileList = getAllFilesInFolder(folderPath, -1);

        List<ForeCastValue> foreCastValues = new ArrayList<>();

        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }
            logInfo("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(pathFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);


            int[] years = {2021, 2022, 2023, 2024, 2025, 2026, 2027};
            HashMap<String, Integer> FORECAST_ORDER_COLUMN = new HashMap<>();

            for(Sheet sheet : workbook) {
                Region region = getRegionBySheetName(sheet.getSheetName());
                for(Row row : sheet) {
                    if(row.getRowNum() == 1)
                        getOrderColumnsName(row, FORECAST_ORDER_COLUMN);
                    else if(!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() &&       // checking null
                            row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().length() == 3 &&    // checking cell is whether metaSeries or not
                            row.getRowNum() > 1) {

                        // get all quantity value from 2021 to 2027
                        for(int year: years) {
                            String metaSeries = row.getCell(FORECAST_ORDER_COLUMN.get("Series /Segments")).getStringCellValue();
                            String plant = row.getCell(FORECAST_ORDER_COLUMN.get("Plant"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                            int quantity = getQuantity(row, year);
                            // setting values
                            ForeCastValue foreCastValue = new ForeCastValue(region, year, metaSeries, quantity, plant);
                            foreCastValues.add(foreCastValue);
                        }
                    }
                }
            }
        }
        log.info("Number of ForeCastValue: " + foreCastValues.size());
        return foreCastValues;
    }

    /**
     * Get Quantity in Forecast Value based on year
     */
    private int getQuantity(Row row, int year) {
        int cellIndex = 5;
        switch (year) {
            case 2022:
                cellIndex = 6;
                break;
            case 2023:
                cellIndex = 7;
                break;
            case 2024:
                cellIndex = 8;
                break;
            case 2025:
                cellIndex = 9;
                break;
            case 2026:
                cellIndex = 10;
                break;
            case 2027:
                cellIndex = 11;
                break;
        }
        return (int) row.getCell(cellIndex).getNumericCellValue();
    }

    /**
     * Find the region based on sheetName in Forecast Value
     */
    private Region getRegionBySheetName(String sheetName) {
        String strRegion;
        switch (sheetName) {
            case "Asia_Fin":
                strRegion = "Asia";
                break;
            case "Pac_Fin":
                strRegion = "Pacific";
                break;
            default:
                strRegion = "India";
        }
        return regionService.getRegionByName(strRegion);
    }

    /**
     * Get CompetitorGroup based on country, class, category, series --> for assigning HYGLeadTime, DealerStreetPricing and calculating Variance %
     */
    private List<CompetitorPricing> getCompetitorGroup() {
        return competitorPricingRepository.getCompetitorGroup();
    }

    @Transactional
    private void assigningCompetitorValues() {

    }

}
