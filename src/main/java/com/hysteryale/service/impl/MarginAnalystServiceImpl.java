package com.hysteryale.service.impl;

import com.hysteryale.model.Part;
import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import com.hysteryale.repository.MarginAnalystDataRepository;
import com.hysteryale.repository.MarginAnalystSummaryRepository;
import com.hysteryale.service.CurrencyService;
import com.hysteryale.service.MarginAnalystService;
import com.hysteryale.service.PartService;
import com.hysteryale.utils.DateUtils;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MarginAnalystServiceImpl implements MarginAnalystService {
    @Resource
    MarginAnalystDataRepository marginAnalystDataRepository;
    @Resource
    CurrencyService currencyService;
    @Resource
    PartService partService;
    @Resource
    MarginAnalystSummaryRepository marginAnalystSummaryRepository;

    /**
     * @param modelCode
     * @param currency
     * @return
     */
    @Override
    public Map<String, List<MarginAnalystData>> getMarginAnalystData(String modelCode, String currency, Calendar monthYear) {
        List<MarginAnalystData> marginAnalystDataList = marginAnalystDataRepository.getMarginDataForAnalysis(modelCode, currency, monthYear);
        return Map.of("MarginAnalystData", marginAnalystDataList);
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
    private void getColumns(Row row, String fileType) {
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
    private MarginAnalystData mapMarginAnalysisData(Row row, List<Row> rows, String currency) throws FileNotFoundException {

        // modelCode and partNumber from Margin Analysis Macro
        String modelCode = row.getCell(marginAnalysisColumns.get("Model Code")).getStringCellValue();
        String partNumber = row.getCell(marginAnalysisColumns.get("Option Code")).getStringCellValue();

        // Get List Price and Net Price from set of file named as "power bi {time}"
        Map<String, Double> priceMap = getListPriceAndNetPrice(rows, modelCode, partNumber, currency);

        if(priceMap.get("isFound") != 0.0) {
            MarginAnalystData marginAnalystData = new MarginAnalystData();

            // Non-calculated data fields
            marginAnalystData.setPlant(row.getCell(marginAnalysisColumns.get("Plant")).getStringCellValue());
            marginAnalystData.setModelCode(modelCode);
            marginAnalystData.setPriceListRegion(row.getCell(marginAnalysisColumns.get("Price List Region")).getStringCellValue());
            marginAnalystData.setClass_(row.getCell(marginAnalysisColumns.get("Class")).getStringCellValue());
            marginAnalystData.setOptionCode(partNumber);
            marginAnalystData.setStd_opt(row.getCell(marginAnalysisColumns.get("STD/OPT"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            marginAnalystData.setDescription(row.getCell(marginAnalysisColumns.get("Description")).getStringCellValue());
            marginAnalystData.setCurrency(currencyService.getCurrenciesByName(currency));
            marginAnalystData.setCostRMB(row.getCell(marginAnalysisColumns.get("Add on Cost RMB")).getNumericCellValue());


            //Calculate margin
            // if there is no Cost RMB
            //      margin @ aop USD = Dealer net (net price) * 0.1,
            //      else
            //         margin @ aop USD = (Dealer Net - Cost RMB *(1+ CostUpLift) * (1+ Warranty + Surcharge + Duty)* Margin Analysis @ AOP Rate)- Freight)

            double listPrice = priceMap.get("List Price");
            double netPrice = priceMap.get("Net Price");
            double costRMB = row.getCell(marginAnalysisColumns.get("Add on Cost RMB")).getNumericCellValue();

            // Notes: some of parameters are assigned manually

            // AUD = 0.2159, USD = 0.1569
            double marginAnalysisAOPRate = currency.equals("AUD") ? 0.2159 : 0.1569;
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
        return null;
    }

    /**
     * Get List Price and Net Price from PowerBi Export by using modelCode and partNumber
     * @param rows from PowerBi files
     * @param modelCode from Margin Analysis Macro
     * @param partNumber from Margin Analysis Macro

     */
    private Map<String, Double> getListPriceAndNetPrice(List<Row> rows, String modelCode, String partNumber, String currency) {

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
                        valueMap.put("isFound", 1.0);

                        return valueMap;
                    }
                }
        }
        valueMap.put("isFound", 0.0);
        return valueMap;
    }

    /**
     * Read List of rows in PowerBi Export file and provide columns' name into HashMap
     */
    private List<Row> readPowerBiExportFiles(String month, int year) throws FileNotFoundException {
        String folderPathPB = "import_files/bi_download";
        String fileNamePB = "power bi " + month + " " + (year - 2000) + ".xlsx";
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

        Pattern pattern = Pattern.compile(".* Macro_(\\w{3}) .*");
        Matcher matcher = pattern.matcher(fileName);
        String month = "Jan";
        int year = 2023;
        if(matcher.find()) {
           month = matcher.group(1);
        }
        Calendar monthYear = Calendar.getInstance();
        monthYear.set(year, DateUtils.monthMap.get(month), 1);


        String[] macroSheets = {"USD HYM Ruyi Staxx", "AUD HYM Ruyi Staxx"};
        for (String macroSheet : macroSheets) {
            // Init Currency
            String currency = macroSheet.equals("USD HYM Ruyi Staxx") ? "USD" : "AUD";

            // Get sheet of AUD
            Sheet audMarginAnalysisSheet = workbook.getSheet(macroSheet);

            // Get List of rows in PowerBi files
            List<Row> powerBiRows = readPowerBiExportFiles(month, year);

            List<MarginAnalystData> marginAnalystDataList = new ArrayList<>();
            for(Row row : audMarginAnalysisSheet) {
                if(row.getRowNum() == 0)
                    getColumns(row, "MarginAnalysis");
                else if(!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                    // Mapping MarginAnalysisData and add into saved list
                    MarginAnalystData marginAnalystData = mapMarginAnalysisData(row, powerBiRows, currency);
                    if(marginAnalystData != null) {
                        marginAnalystData.setMonthYear(monthYear);
                        marginAnalystDataList.add(marginAnalystData);
                    }
                }

            }
            marginAnalystDataRepository.saveAll(marginAnalystDataList);
            log.info("MarginAnalysisData are newly saved: " + marginAnalystDataList.size());
            marginAnalystDataList.clear();
        }
        calculateMarginAnalystSummaryMonthly(monthYear, "USD");
        calculateMarginAnalystSummaryMonthly(monthYear, "AUD");
    }

    /**
     * Calculate MarginAnalysisSummary monthly by {monthYear} and {currency}
     * @param monthYear
     * @param currency
     */
    public void calculateMarginAnalystSummaryMonthly(Calendar monthYear, String currency) {
        // Get all distinct modelCode based on {monthYear} and {currency} from MarginAnalystData
        List<String> modelCodeList = marginAnalystDataRepository.getModelCodesByMonthYearAndCurrency(monthYear, currency);
        List<MarginAnalystSummary> marginAnalystSummaryList = new ArrayList<>();


        /* for each modelCode -> get List<MarginAnalystData> and List<Part>
        then calculate totalListPrice, dealerNet, manufacturingCostRMB, .....
        -> save MarginAnalystSummary monthly
        */
        for(String modelCode : modelCodeList)
        {
            log.info(" === ModelCode " + modelCode + " === ");
            List<MarginAnalystData> marginAnalystDataList = getMarginAnalystData(modelCode, currency, monthYear).get("MarginAnalystData");
            List<Part> partList = partService.getPartForMarginAnalysis(modelCode, currency, monthYear);

            // Initialize values
            double costUplift = 0.0;
            double warranty = 0.0;
            double surcharge = 0.015;
            double duty = 0.0;
            double freight = 0.0;
            boolean liIonIncluded = false; // NO

            double marginAnalysisAOPRate = getMarginAnalysisAOPRate(currency, "monthly");

            double totalListPrice = 0.0;
            double dealerNet = 0.0;
            double manufacturingCostRMB = 0.0;
            log.info("margin data " + marginAnalystDataList.size());
            for(MarginAnalystData m : marginAnalystDataList) {
                log.info(m.getOptionCode());
                totalListPrice += m.getListPrice();
                manufacturingCostRMB += m.getCostRMB();
            }
            log.info("part " + partList.size());
            for(Part p : partList) {
                log.info(p.getPartNumber());
                dealerNet += p.getNetPriceEach();
            }
            double totalCostRMB = manufacturingCostRMB * (1 + costUplift) * (1 + warranty + surcharge + duty);
            double blendedDiscount = 1 - (dealerNet / totalListPrice);
            double fullCostAOPRate = totalCostRMB * marginAnalysisAOPRate;
            double margin = dealerNet - fullCostAOPRate;
            double marginPercentAopRate = margin / dealerNet;

            // Assign value of MarginAnalysisSummary
            MarginAnalystSummary marginAnalystSummary = new MarginAnalystSummary();

            // fields for both annually and monthly MarginAnalystSummary
            marginAnalystSummary.setModelCode(modelCode);
            marginAnalystSummary.setMonthYear(monthYear);
            marginAnalystSummary.setCurrency(currencyService.getCurrenciesByName(currency));
            marginAnalystSummary.setManufacturingCostRMB(manufacturingCostRMB);
            marginAnalystSummary.setCostUplift(costUplift);
            marginAnalystSummary.setAddWarranty(warranty);
            marginAnalystSummary.setSurcharge(surcharge);
            marginAnalystSummary.setDuty(duty);
            marginAnalystSummary.setFreight(freight);
            marginAnalystSummary.setLiIonIncluded(liIonIncluded);
            marginAnalystSummary.setTotalCostRMB(totalCostRMB);
            marginAnalystSummary.setTotalListPrice(totalListPrice);
            marginAnalystSummary.setBlendedDiscountPercentage(blendedDiscount);
            marginAnalystSummary.setDealerNet(dealerNet);
            marginAnalystSummary.setMargin(margin);
            marginAnalystSummary.setMarginAopRate(marginAnalysisAOPRate);

            // annually valued
            //marginAnalystSummary.setFullCostAopRate(fullCostAOPRate);
            //marginAnalystSummary.setMarginPercentAopRate(marginPercentAopRate);

            // monthly valued
            marginAnalystSummary.setFullMonthlyRate(fullCostAOPRate);
            marginAnalystSummary.setMarginPercentMonthlyRate(marginPercentAopRate);

            marginAnalystSummaryList.add(marginAnalystSummary);
        }

        marginAnalystSummaryRepository.saveAll(marginAnalystSummaryList);
        log.info("(Monthly) MarginAnalystSummary are newly saved: " + marginAnalystSummaryList.size());
        marginAnalystSummaryList.clear();
    }

    /**
     * Get MarginAnalysisAOP rate variable by currency and durationUnit (annually or monthly)
     * @param currency USD or AUD
     * @param durationUnit annually or monthly
     */
    private double getMarginAnalysisAOPRate(String currency, String durationUnit) {
        if(currency.equals("USD")) {
            if(durationUnit.equals("annually"))
                return 0.1569;
            else
                return 0.1484;
        }
        else {
            if(durationUnit.equals("annually"))
                return 0.2159;
            else
                return 0.2132;
        }
    }
}
