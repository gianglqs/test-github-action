package com.hysteryale.service;

import com.hysteryale.model.CompetitorPricing;
import com.hysteryale.model.ForeCastValue;
import com.hysteryale.model.Region;
import com.hysteryale.repository.CompetitorPricingRepository;
import com.hysteryale.repository.RegionRepository;
import com.hysteryale.utils.EnvironmentUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImportService extends BasedService {

    @Resource
    CompetitorPricingRepository competitorPricingRepository;

    @Resource
    RegionRepository regionRepository;


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

    public List<CompetitorPricing> mapExcelDataIntoOrderObject(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME, FormulaEvaluator evaluator) throws IllegalAccessException {
        List<CompetitorPricing> competitorPricingList = new ArrayList<>();

        Cell cellRegion = row.getCell(ORDER_COLUMNS_NAME.get("Region"));
        String region = cellRegion.getStringCellValue();

//        Cell cellPlant = row.getCell(ORDER_COLUMNS_NAME.get("Plant"));
//        competitorPricing.setPlant(cellRegion.getStringCellValue());

//        Cell cellCompetitorName = row.getCell(ORDER_COLUMNS_NAME.get("Competitor Name"));
//        String competitorName = cellCompetitorName.getStringCellValue();

        Cell cellClass = row.getCell(ORDER_COLUMNS_NAME.get("Class"));
        String clazz = cellClass.getStringCellValue();

        Double leadTime = null;
        Cell cellLeadTime = row.getCell(ORDER_COLUMNS_NAME.get("Lead Time"));
        if (cellLeadTime != null && cellLeadTime.getCellType() == CellType.NUMERIC) {
            leadTime = cellLeadTime.getNumericCellValue();
        }

        String seriesString = null;
        Cell cellSeries = row.getCell(ORDER_COLUMNS_NAME.get("HYG Series"));
        if (cellSeries != null && cellSeries.getCellType() == CellType.STRING) {
            seriesString = cellSeries.getStringCellValue();
            StringTokenizer stk = new StringTokenizer(seriesString, "/");
            while (stk.hasMoreTokens()) {
                CompetitorPricing cp = new CompetitorPricing(region, clazz, leadTime, stk.nextToken());
                competitorPricingList.add(cp);
            }
        } else {
            CompetitorPricing cp = new CompetitorPricing(region, clazz, leadTime);
            competitorPricingList.add(cp);
        }

        return competitorPricingList;
    }

    public void importCompetitorPricing() throws IOException, IllegalAccessException {

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
            List<CompetitorPricing> competitorPricingList = new ArrayList<>();

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (Row row : competitorSheet) {
                if (row.getRowNum() == 0) getOrderColumnsName(row, COMPETITOR_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 0) {
                    List<CompetitorPricing> competitorPricings = mapExcelDataIntoOrderObject(row, COMPETITOR_COLUMNS_NAME, evaluator);
                    for (CompetitorPricing competitorPricing : competitorPricings) {
                        // if it has series -> import Forecast
                        if (competitorPricing.getSeries() != null) {
                            competitorPricing = importForcastForCompetitorPricing(competitorPricing);
                        }
                        competitorPricingList.add(competitorPricing);
                    }
                    ;
                }
            }
            competitorPricingRepository.saveAll(competitorPricingList);
            updateStateImportFile(pathFile);
        }
    }

    /**
     * Find a forecast value by Region and Series, year is an option if year is empty then we get all years
     *
     * @param region
     * @param series
     * @param year
     * @return
     */
    private List<ForeCastValue> findForeCastValue(Region region, String series, int... year) {
        return null;
    }

    private List<ForeCastValue> loadForecastForCompetitorPricingFromFile() throws IOException, IllegalAccessException {
        List<ForeCastValue> result = new ArrayList<ForeCastValue>();
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.forecast-pricing");

        Map<String, Region> mapSheetNameWithRegion = new HashMap<>();

        mapSheetNameWithRegion.put("Asia_Fin", regionRepository.findByRegionId("A").get());
        mapSheetNameWithRegion.put("Pac_Fin", regionRepository.findByRegionId("P").get());
        mapSheetNameWithRegion.put("ISC_Fin", regionRepository.findByRegionId("I").get());


        List<String> fileList = getAllFilesInFolder(folderPath, 3);
        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }
            logInfo("{ Start loading file into memory: '" + fileName + "'");

            InputStream is = new FileInputStream(pathFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);

            //browser all sheet
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                ForeCastValue foreCastValue = new ForeCastValue();
                // get sheetName -> region
                String sheetName = workbook.getSheetName(i);
                Region region = mapSheetNameWithRegion.get(sheetName);
                HashMap<String, Integer> FORECAST_COLUMNS_NAME = new HashMap<>();
                Sheet regionSheet = workbook.getSheet(sheetName);
                List<ForeCastValue> listForecastValue = new ArrayList<>();
                for (Row row : regionSheet) {
                    if (row.getRowNum() == 1) getOrderColumnsName(row, FORECAST_COLUMNS_NAME);
                    else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 0) {
                        List<ForeCastValue> getListForeCastFromRow = mapExcelDataIntoForeCastValueObject(row, FORECAST_COLUMNS_NAME, region);

                    }
                }


            }


            List<CompetitorPricing> competitorPricingList = new ArrayList<>();

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();


            competitorPricingRepository.saveAll(competitorPricingList);
            updateStateImportFile(pathFile);
        }


        // read all workbook, add all ForeCastValue to the results
        // extract Region
        // extract Series
        // extract Years // read from column H -> L
        // extract Volume
        ForeCastValue foreCastValue = new ForeCastValue();
        foreCastValue.setRegion(new Region());
        foreCastValue.setSeries("");
        foreCastValue.setYear(2023);
        foreCastValue.setQuantity(10);

        result.add(foreCastValue);

        return result;
    }

    private CompetitorPricing importForcastForCompetitorPricing(CompetitorPricing competitorPricing) throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.forecast-pricing");


        String series = competitorPricing.getSeries();

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, -1);


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


            HashMap<String, Integer> FORECAST_COLUMNS_NAME = new HashMap<>();
            String sheetName = null;

            switch (competitorPricing.getRegion()) {
                case "Asia":
                    sheetName = "Asia_Fin";
                    break;
                case "Pacific":
                    sheetName = "Pac_Fin";
                    break;
                case "India":
                    sheetName = "ISC_Fin";
                    break;
            }

            XSSFSheet sheet = workbook.getSheet(sheetName);
            for (Row row : sheet) {
                if (row.getRowNum() == 1) getOrderColumnsName(row, FORECAST_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 1) {
                    Cell metaSeriesCell = row.getCell(FORECAST_COLUMNS_NAME.get("Series /Segments"));
                    String metaSeriesForecast = metaSeriesCell.getStringCellValue();
                    // if metaSeries of forecast # metaSeries of Competitor -> next
                    if (!metaSeriesForecast.equals(series.substring(1)))
                        continue;

                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

                    // Cell 2022
                    Cell cell2022 = row.getCell(6);

                    System.out.println(cell2022.getCellType());
                    //  evaluator.evaluateFormulaCell(cell2022);
                    double volume2022 = cell2022.getNumericCellValue();
                    competitorPricing.setActual(volume2022);
                    System.out.println(volume2022);

                    // Cell 2023
                    Cell cell2023 = row.getCell(7);
                    //  evaluator.evaluate(cell2023);
                    double volume2023 = cell2023.getNumericCellValue();
                    competitorPricing.setAOPF(volume2023);

                    // Cell 2024
                    Cell cell2024 = row.getCell(8);
                    //   evaluator.evaluate(cell2024);
                    double volume2024 = cell2024.getNumericCellValue();
                    competitorPricing.setLRFF(volume2024);
                    return competitorPricing;
                }
            }
        }

        return competitorPricing;
    }

    public List<ForeCastValue> mapExcelDataIntoForeCastValueObject(Row row, HashMap<String, Integer> FORECAST_COLUMNS_NAME, Region region) throws IllegalAccessException {
        List<ForeCastValue> result = new ArrayList<>();
        //get series
        Cell metaSeriesCell = row.getCell(FORECAST_COLUMNS_NAME.get("Series /Segments"));
        String metaSeries = metaSeriesCell.getStringCellValue();
        // if length of metaSeries > 4 -> row is Total
        if (metaSeries.length() > 4)
            return null;

        int startCellQuantity = 5;
        int yearStart = 2021;

        for (; startCellQuantity < 12; startCellQuantity++) {
            //get quantity
            Cell quantityCell = row.getCell(startCellQuantity);
            int quantity = (int) quantityCell.getNumericCellValue();
            //Forecast
            ForeCastValue foreCast = new ForeCastValue(region, yearStart, metaSeries, quantity);
            result.add(foreCast);
            yearStart++;
        }
        return result;

    }

}
