package com.hysteryale.service.impl;

import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import com.hysteryale.repository.MarginAnalystDataRepository;
import com.hysteryale.service.MarginAnalystService;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MarginAnalystServiceImpl implements MarginAnalystService {
    @Resource
    MarginAnalystDataRepository marginAnalystDataRepository;

    /**
     * @param modelCode
     * @param currency
     * @return
     */
    @Override
    public Map<String, List<MarginAnalystData>> getMarginAnalystData(String modelCode, String currency) {
        return null;
    }

    /**
     * @param modelCode
     * @param currency
     * @return
     */
    @Override
    public Map<String, MarginAnalystSummary> getMarginAnalystSummary(String modelCode, String currency) {
        // Based on the model Code, we can query all the parts related to it
        // At first, Yurini said that we can find all the parts in the "Novo Quotation Download", after that she said if we do this way
        // then they will have to download for each model code
        // So she wants to use the Power BI file, we can find the Model (AI) then we can get part Number (AT) then we can get the List price (AZ)

        //for the cost RMB, use Margin Analysis, model code + part -> Cost RMB

        //then find the dealer net

        //Calculate margin
        // if there is no Cost RMB
        //      margin @ aop USD = Dealer net (net price) * 0.1,
        //      else
        //         margin @ aop USD = (Dealer Net - Cost RMB *(1+ CostUpLift) * (1+ Warranty + Surcharge + Duty)* Margin Analysis @ AOP Rate)- Freight)


        return null;
    }
    public static HashMap<String, Integer> marginAnalysisColumns = new HashMap<>();
    public static HashMap<String, Integer> powerBIColumn = new HashMap<>();

    /**
     * Mapping Columns' name with cell index in Excel file
     * @param row contains columns' name
     * @param fileType is either Margin Analysis Data or PowerBI Export
     */
    public void getColumns(Row row, String fileType) {
        boolean isEnded = false;
        int index = 1;

        while(!isEnded) {
            if(row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty())
                isEnded = true;
            else{
                String columnName = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                if(fileType.equals("MarginAnalysis"))
                    marginAnalysisColumns.put(columnName, index);
                else
                    powerBIColumn.put(columnName, index);
            }
            index++;
        }

        if(fileType.equals("MarginAnalysis"))
            log.info("MarginAnalysis Column: " + marginAnalysisColumns);
        else
            log.info("PowerBiColumn: " + powerBIColumn);

    }

    /**
     * Assign value for MarginAnalysisData from Excel row
     */
    public MarginAnalystData mapMarginAnalysisData(Row row, List<Row> rows, String currency) throws FileNotFoundException {

        // modelCode and partNumber from Margin Analysis Macro
        String modelCode = row.getCell(marginAnalysisColumns.get("Model Code")).getStringCellValue();
        String partNumber = row.getCell(marginAnalysisColumns.get("Option Code")).getStringCellValue();

        // Get List Price and Net Price from set of file named as "power bi {time}"
        Map<String, Double> priceMap = getListPriceAndNetPrice(rows, modelCode, partNumber, currency);

        MarginAnalystData marginAnalystData = new MarginAnalystData();

        // Non-calculated data fields
        marginAnalystData.setPlant(row.getCell(marginAnalysisColumns.get("Plant")).getStringCellValue());
        marginAnalystData.setModelCode(modelCode);
        marginAnalystData.setPriceListRegion(row.getCell(marginAnalysisColumns.get("Price List Region")).getStringCellValue());
        marginAnalystData.setClass_(row.getCell(marginAnalysisColumns.get("Class")).getStringCellValue());
        marginAnalystData.setOptionCode(partNumber);
        marginAnalystData.setStd_opt(row.getCell(marginAnalysisColumns.get("STD/OPT")).getStringCellValue());
        marginAnalystData.setDescription(row.getCell(marginAnalysisColumns.get("Description")).getStringCellValue());


        //Calculate margin
        // if there is no Cost RMB
        //      margin @ aop USD = Dealer net (net price) * 0.1,
        //      else
        //         margin @ aop USD = (Dealer Net - Cost RMB *(1+ CostUpLift) * (1+ Warranty + Surcharge + Duty)* Margin Analysis @ AOP Rate)- Freight)

        double listPrice = priceMap.get("List Price");
        double netPrice = priceMap.get("Net Price");
        double costRMB = row.getCell(marginAnalysisColumns.get("Add on Cost RMB")).getNumericCellValue();

        // Notes: some of parameters are assigned manually

        double marginAnalysisAOPRate = 0.2159;
        double costUplift = 0.0;
        double surcharge = 0.015;
        double duty = 0.0;
        double marginAOP = 0.0;
        double freight = 0;
        double warranty = 0;

        if(costRMB == 0.0) {
            marginAOP =  netPrice * 0.1;
        }
        else {
            marginAOP = (netPrice - costRMB * (1 + costUplift) * (1 + warranty + surcharge + duty) * marginAnalysisAOPRate) - freight;
        }

        // Calculated data fields
        marginAnalystData.setListPrice(listPrice);
        marginAnalystData.setMargin_aop(marginAOP);

        return marginAnalystData;
    }

    /**
     * Get List Price and Net Price from PowerBi Export by using modelCode and partNumber
     * @param rows from PowerBi files
     * @param modelCode from Margin Analysis Macro
     * @param partNumber from Margin Analysis Macro

     */
    public Map<String, Double> getListPriceAndNetPrice(List<Row> rows, String modelCode, String partNumber, String currency) {

        Map<String, Double> valueMap = new HashMap<>(); // Map contains List Price and Net Price
        valueMap.put("List Price", 0.0);
        valueMap.put("Net Price", 0.0);

        for(Row row : rows) {
                if(row.getRowNum() != 0) {
                    String excelModelCode = row.getCell(powerBIColumn.get("Model")).getStringCellValue();
                    String excelPartNumber = row.getCell(powerBIColumn.get("Part Number")).getStringCellValue();
                    String excelCurrency = row.getCell(powerBIColumn.get("Currency")).getStringCellValue();

                    // if found modelCode and partNumber and currency then giving 2 values into Map and return
                    if(excelPartNumber.equals(partNumber) && excelModelCode.equals(modelCode) && excelCurrency.equals(currency)) {
                        double listPrice = row.getCell(powerBIColumn.get("ListPrice")).getNumericCellValue();
                        double netPrice = row.getCell(powerBIColumn.get("Net Price")).getNumericCellValue();

                        valueMap.put("List Price", listPrice);
                        valueMap.put("Net Price", netPrice);

                        return valueMap;
                    }
                }
        }
        return valueMap;
    }

    /**
     * Read List of rows in PowerBi Export file and provide columns' name into HashMap
     */
    public List<Row> readPowerBiExportFiles() throws FileNotFoundException {
        String folderPathPB = "import_files/margin_analyst_data";
        String fileNamePB = "power bi Aug 23.xlsx";       // "power bi Aug 23"
        InputStream isPB = new FileInputStream(folderPathPB + "/" + fileNamePB);
        Workbook workbookPB = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(isPB);

        Sheet exportSheet = workbookPB.getSheet("Export");
        List<Row> rows = new ArrayList<>();


        for(Row r : exportSheet) {
            if(r.getRowNum() == 0)
            {
                getColumns(r, "PowerBi");
            }
            else {
                rows.add(r);
            }
        }
        return rows;
    }
    public void importMarginAnalystData() throws FileNotFoundException {
        // Init folderPath and fileName
        String folderPath = "import_files/margin_analyst_data";
        String fileName = "Copy of USD AUD Margin Analysis Template Macro_Aug 1st.xlsx";       // "Margin Analysis Macro"
        InputStream is = new FileInputStream(folderPath + "/" + fileName);
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        // Init Currency
        String currency = "AUD";

        // Get sheet of AUD
        Sheet audMarginAnalysisSheet = workbook.getSheet("AUD HYM Ruyi Staxx");

        // Get List of rows in PowerBi files
        List<Row> powerBiRows = readPowerBiExportFiles();

        List<MarginAnalystData> marginAnalystDataList = new ArrayList<>();
        for(Row row : audMarginAnalysisSheet) {
            if(row.getRowNum() == 0)
                getColumns(row, "MarginAnalysis");
            else if(!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {

                // Mapping MarginAnalysisData and add into saved list
                MarginAnalystData marginAnalystData = mapMarginAnalysisData(row, powerBiRows, currency);
                marginAnalystDataList.add(marginAnalystData);
            }

        }
        marginAnalystDataRepository.saveAll(marginAnalystDataList);
        log.info("MarginAnalysisData are newly saved: " + marginAnalystDataList.size());
        marginAnalystDataList.clear();
    }
}
