package com.hysteryale.service;

import com.hysteryale.model.CompetitorPricing;
import com.hysteryale.repository.CompetitorPricingRepository;
import com.hysteryale.utils.EnvironmentUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
                pattern = Pattern.compile("^Cost_Data.*(.xlsx)$");
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

    public List<CompetitorPricing> mapExcelDataIntoOrderObject(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) throws IllegalAccessException {
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
        if (cellLeadTime.getCellType() == CellType.NUMERIC) {
            leadTime = cellLeadTime.getNumericCellValue();
        }

        Cell cellActual = row.getCell(ORDER_COLUMNS_NAME.get("Actual"));
        Double actual = null;
        if (cellActual.getCellType() == CellType.NUMERIC) {
            actual = cellActual.getNumericCellValue();
        }

        Double AOPF = null;
        Cell cellAOPF = row.getCell(ORDER_COLUMNS_NAME.get("AOPF"));
        if (cellAOPF.getCellType() == CellType.NUMERIC) {
            AOPF = cellAOPF.getNumericCellValue();
        }
        Double LRFF = null;
        Cell cellLRFF = row.getCell(ORDER_COLUMNS_NAME.get("LRFF"));
        if (cellLRFF.getCellType() == CellType.NUMERIC) {
            LRFF = cellLRFF.getNumericCellValue();
        }
        Cell cellSeries = row.getCell(ORDER_COLUMNS_NAME.get("HYG Series"));
        String seriesString = cellSeries.getCellType() == CellType.STRING ? cellSeries.getStringCellValue() : "";
        StringTokenizer stk = new StringTokenizer(seriesString, "/");


        if (stk.countTokens() > 0) {
            while (stk.hasMoreTokens()) {
                CompetitorPricing cp = new CompetitorPricing(region, clazz, leadTime, stk.nextToken(), actual, AOPF, LRFF);
                competitorPricingList.add(cp);
            }
        } else {
            CompetitorPricing cp = new CompetitorPricing(region, clazz, leadTime, actual, AOPF, LRFF);
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
            List<CompetitorPricing> competitorPricingList  =new ArrayList<>();


            for (Row row : competitorSheet) {
                if (row.getRowNum() == 0) getOrderColumnsName(row, COMPETITOR_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 1) {
                    List<CompetitorPricing> competitorPricings = mapExcelDataIntoOrderObject(row, COMPETITOR_COLUMNS_NAME);
                    competitorPricingList.addAll(competitorPricings);
                }
            }
            competitorPricingRepository.saveAll(competitorPricingList);
            updateStateImportFile(pathFile);
        }
    }


}
