package com.hysteryale.service;

import com.hysteryale.model.CompetitorPricing;
import com.hysteryale.repository.CompetitorPricingRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImportService extends BasedService {

    @Resource
    private CompetitorPricingRepository competitorPricingRepository;

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

//        Cell cellActual = row.getCell(ORDER_COLUMNS_NAME.get("Actual"));
//        evaluator.evaluate(cellActual);
//        Double actual = null;
//        if (cellActual != null && cellActual.getCellType() == CellType.FORMULA) {
//            actual = cellActual.getNumericCellValue();
//        }
//
//        Double AOPF = null;
//        Cell cellAOPF = row.getCell(ORDER_COLUMNS_NAME.get("AOPF"));
//        evaluator.evaluate(cellAOPF);
//        if (cellAOPF != null && cellAOPF.getCellType() == CellType.FORMULA) {
//            AOPF = cellAOPF.getNumericCellValue();
//        }
//        Double LRFF = null;
//        Cell cellLRFF = row.getCell(ORDER_COLUMNS_NAME.get("LRFF"));
//        evaluator.evaluate(cellLRFF);
//        if (cellLRFF != null && cellLRFF.getCellType() == CellType.FORMULA) {
//            LRFF = cellLRFF.getNumericCellValue();
//        }


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

            

            for(int i =0;i <100;i++){
                XSSFSheet s =  workbook.getSheetAt(i);
                System.out.println(s.getSheetName());
            }
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
                    System.out.println(cell2022.getCachedFormulaResultType());
                    evaluator.evaluateFormulaCell(cell2022);
                    double volume2022 = cell2022.getNumericCellValue();
                    competitorPricing.setActual(volume2022);

                    // Cell 2023
                    Cell cell2023 = row.getCell(7);
                    evaluator.evaluate(cell2023);
                    double volume2023 = cell2023.getNumericCellValue();
                    competitorPricing.setAOPF(volume2023);

                    // Cell 2024
                    Cell cell2024 = row.getCell(8);
                    evaluator.evaluate(cell2024);
                    double volume2024 = cell2024.getNumericCellValue();
                    competitorPricing.setLRFF(volume2024);
                    return competitorPricing;
                }
            }
        }

        return competitorPricing;
    }

}
