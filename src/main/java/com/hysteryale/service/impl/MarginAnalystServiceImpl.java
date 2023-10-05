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
import com.hysteryale.utils.CurrencyFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
     * Get List of MarginAnalystData having modelCode, currency in a month {monthYear}
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
    public Map<String, MarginAnalystSummary> getMarginAnalystSummary(String modelCode, String currency, Calendar monthYear) {
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

        Map<String, MarginAnalystSummary> marginAnalystSummaryMap = new HashMap<>();

        Calendar calendarForAnnually = Calendar.getInstance();
        calendarForAnnually.set(monthYear.get(Calendar.YEAR), Calendar.JANUARY, 28);

        MarginAnalystSummary marginAnalystSummaryMonthly = marginAnalystSummaryRepository.getMarginAnalystSummaryMonthly(modelCode, currency, monthYear).get();
        MarginAnalystSummary marginAnalystSummaryAnnually = marginAnalystSummaryRepository.getMarginAnalystSummaryAnnually(modelCode, currency, calendarForAnnually).get();

        marginAnalystSummaryMap.put("MarginAnalystSummaryMonthly", marginAnalystSummaryMonthly);
        marginAnalystSummaryMap.put("MarginAnalystSummaryAnnually", marginAnalystSummaryAnnually);
        return marginAnalystSummaryMap;
    }

    public static HashMap<String, Integer> marginAnalysisColumns = new HashMap<>();
    /**
     * Mapping Columns' name with cell index in Excel file
     * @param row contains columns' name
     */
    private void getColumns(Row row) {
        boolean isEnded = false;
        int index = 1;
        while(!isEnded) {
            if(row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty())
                isEnded = true;
            else{
                String columnName = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                marginAnalysisColumns.put(columnName, index);
            }
            index++;
        }
            log.info("MarginAnalysis Column: " + marginAnalysisColumns);
    }

    /**
     * Assign value for MarginAnalysisData from Excel row
     * @param row in Margin Analysis Macro
     */
    private MarginAnalystData mapMarginAnalysisData(Row row, String currency, Calendar monthYear, String dealer) {

        // modelCode and partNumber from Margin Analysis Macro
        String modelCode = row.getCell(marginAnalysisColumns.get("Model Code")).getStringCellValue();
        String partNumber = row.getCell(marginAnalysisColumns.get("Option Code")).getStringCellValue();

        Optional<Part> optionalPart = partService.getPartForMarginAnalysis(modelCode, partNumber, currency, monthYear, dealer);

        // if Net Price and List Price are found -> then create new and assign value for MarginAnalystData
        if(optionalPart.isPresent()) {
            Part part = optionalPart.get();
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
            marginAnalystData.setMonthYear(monthYear);
            marginAnalystData.setDealer(dealer);


            //Calculate margin
            // if there is no Cost RMB
            //      margin @ aop USD = Dealer net (net price) * 0.1,
            //      else
            //         margin @ aop USD = (Dealer Net - Cost RMB *(1+ CostUpLift) * (1+ Warranty + Surcharge + Duty)* Margin Analysis @ AOP Rate)- Freight)

            double listPrice = part.getListPrice();
            double netPrice = part.getNetPriceEach();
            double costRMB = row.getCell(marginAnalysisColumns.get("Add on Cost RMB")).getNumericCellValue();

            marginAnalystData.setDealerNet(BigDecimal.valueOf(netPrice).setScale(4, RoundingMode.HALF_UP).doubleValue());

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
            marginAnalystData.setCostRMB(CurrencyFormatUtils.formatDoubleValue(costRMB, CurrencyFormatUtils.decimalFormatFourDigits));
            marginAnalystData.setListPrice(CurrencyFormatUtils.formatDoubleValue(listPrice, CurrencyFormatUtils.decimalFormatFourDigits));
            marginAnalystData.setMargin_aop(CurrencyFormatUtils.formatDoubleValue(marginAOP, CurrencyFormatUtils.decimalFormatFourDigits));
            return marginAnalystData;
        }
        return null;
    }

    public void importMarginAnalystData() throws IOException {
        // Init folderPath and fileName
        String folderPath = "import_files/margin_analyst_data";
        String fileName = "Copy of USD AUD Margin Analysis Template Macro_Aug 1st.xlsx";       // "Margin Analysis Macro"

        InputStream is = new FileInputStream(folderPath + "/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        // Extract monthYear from fileName pattern
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

            List<MarginAnalystData> marginAnalystDataList = new ArrayList<>();
            for(Row row : audMarginAnalysisSheet) {
                if(row.getRowNum() == 0)
                    getColumns(row);
                else if(!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {

                    // modelCode + partNumber + monthYear + currency -> List<Part> with different Dealer(netPrice, discount....)
                    // modelCode and partNumber from Margin Analysis Macro
                    String modelCode = row.getCell(marginAnalysisColumns.get("Model Code")).getStringCellValue();
                    String partNumber = row.getCell(marginAnalysisColumns.get("Option Code")).getStringCellValue();
                    List<String> dealerNames = partService.getDistinctDealerNames(modelCode, currency, monthYear, partNumber);

                    for(String dealer : dealerNames) {
                        // Mapping MarginAnalysisData and add into saved list
                        MarginAnalystData marginAnalystData = mapMarginAnalysisData(row, currency, monthYear, dealer);

                        //if it is null -> then do not save
                        if(marginAnalystData != null) {
                            marginAnalystDataList.add(marginAnalystData);
                        }
                    }
                }

            }
            marginAnalystDataRepository.saveAll(marginAnalystDataList);
            log.info("MarginAnalysisData are newly saved: " + marginAnalystDataList.size());
            marginAnalystDataList.clear();
        }

        // after having MarginAnalystData -> calculate MarginAnalystSummary
        calculateMarginAnalystSummary(monthYear, "USD", "monthly");
        calculateMarginAnalystSummary(monthYear, "AUD", "monthly");

        calculateMarginAnalystSummary(monthYear, "USD", "annually");
        calculateMarginAnalystSummary(monthYear, "AUD", "annually");

    }

    /**
     * Calculate MarginAnalysisSummary by {monthYear} and {currency}
     * @param monthYear
     * @param currency
     */
    public void calculateMarginAnalystSummary(Calendar monthYear, String currency, String durationUnit) {
        // Get all distinct modelCode based on {monthYear} and {currency} from MarginAnalystData
        List<String> modelCodeList = marginAnalystDataRepository.getModelCodesByMonthYearAndCurrency(monthYear, currency);
        List<MarginAnalystSummary> marginAnalystSummaryList = new ArrayList<>();


        /* for each modelCode -> get List<MarginAnalystData> and List<Part>
        then calculate totalListPrice, dealerNet, manufacturingCostRMB, .....
        -> save MarginAnalystSummary monthly
        */
        log.info("Number of modelCode: " + modelCodeList.size());
        for(String modelCode : modelCodeList)
        {
            log.info(" === ModelCode " + modelCode + " === ");
            List<MarginAnalystData> marginAnalystDataList = getMarginAnalystData(modelCode, currency, monthYear).get("MarginAnalystData");

            // Initialize values
            double costUplift = 0.0;
            double warranty = 0.0;
            double surcharge = 0.015;
            double duty = 0.0;
            double freight = 0.0;
            boolean liIonIncluded = false; // NO

            double marginAnalysisAOPRate = getMarginAnalysisAOPRate(currency, durationUnit);

            double totalListPrice = 0.0;
            double dealerNet = 0.0;
            double manufacturingCostRMB = 0.0;
            log.info("  Margin data count: " + marginAnalystDataList.size());
            for(MarginAnalystData m : marginAnalystDataList) {
                log.info(m.getOptionCode() + " LP: " + m.getListPrice() + " CostRMB: " + m.getCostRMB());
                totalListPrice += m.getListPrice();
                manufacturingCostRMB += m.getCostRMB();

                dealerNet += partService.getNetPriceInPart(modelCode, currency, monthYear, m.getOptionCode());
            }
            double totalCostRMB = manufacturingCostRMB * (1 + costUplift) * (1 + warranty + surcharge + duty);
            double blendedDiscount = 1 - (dealerNet / totalListPrice);
            double fullCostAOPRate = totalCostRMB * marginAnalysisAOPRate;
            double margin = dealerNet - fullCostAOPRate;
            double marginPercentAopRate = margin / dealerNet;

            log.info("manufacturing cost RMB: " + manufacturingCostRMB);
            log.info("totalCost RMB: " + totalCostRMB);
            log.info("fullCostAOPRate: " + fullCostAOPRate);
            log.info("totalListPrice: " + totalListPrice);
            log.info("blendedDiscount: " + blendedDiscount);
            log.info("totalDealerNet: " + dealerNet);

            // Assign value of MarginAnalysisSummary
            MarginAnalystSummary marginAnalystSummary = new MarginAnalystSummary();

            // fields for both annually and monthly MarginAnalystSummary
            marginAnalystSummary.setModelCode(modelCode);
            marginAnalystSummary.setCurrency(currencyService.getCurrenciesByName(currency));
            marginAnalystSummary.setManufacturingCostRMB(BigDecimal.valueOf(manufacturingCostRMB).setScale(4, RoundingMode.HALF_UP).doubleValue());
            marginAnalystSummary.setCostUplift(costUplift);
            marginAnalystSummary.setAddWarranty(warranty);
            marginAnalystSummary.setSurcharge(surcharge);
            marginAnalystSummary.setDuty(duty);
            marginAnalystSummary.setFreight(freight);
            marginAnalystSummary.setLiIonIncluded(liIonIncluded);
            marginAnalystSummary.setTotalCostRMB(BigDecimal.valueOf(totalCostRMB).setScale(4, RoundingMode.HALF_UP).doubleValue());
            marginAnalystSummary.setTotalListPrice(BigDecimal.valueOf(totalListPrice).setScale(4, RoundingMode.HALF_UP).doubleValue());
            marginAnalystSummary.setBlendedDiscountPercentage(BigDecimal.valueOf(blendedDiscount).setScale(4, RoundingMode.HALF_UP).doubleValue());
            marginAnalystSummary.setDealerNet(BigDecimal.valueOf(dealerNet).setScale(4, RoundingMode.HALF_UP).doubleValue());
            marginAnalystSummary.setMargin(BigDecimal.valueOf(margin).setScale(4, RoundingMode.HALF_UP).doubleValue());
            marginAnalystSummary.setMarginAopRate(marginAnalysisAOPRate);

            if(durationUnit.equals("monthly")) {
                marginAnalystSummary.setMonthYear(monthYear);
                // monthly valued
                marginAnalystSummary.setFullMonthlyRate(BigDecimal.valueOf(fullCostAOPRate).setScale(4, RoundingMode.HALF_UP).doubleValue());
                marginAnalystSummary.setMarginPercentMonthlyRate(BigDecimal.valueOf(marginPercentAopRate).setScale(4, RoundingMode.HALF_UP).doubleValue());
            }
            else {
                // annually valued

                // if MarginAnalystSummary is annual then set {Date into 28, Month into JANUARY} (monthly date would be 1)
                Calendar annualDate = Calendar.getInstance();
                annualDate.set(monthYear.get(Calendar.YEAR), Calendar.JANUARY, 28);

                marginAnalystSummary.setMonthYear(annualDate);
                marginAnalystSummary.setFullCostAopRate(BigDecimal.valueOf(fullCostAOPRate).setScale(4, RoundingMode.HALF_UP).doubleValue());
                marginAnalystSummary.setMarginPercentAopRate(BigDecimal.valueOf(marginPercentAopRate).setScale(4, RoundingMode.HALF_UP).doubleValue());
            }
            marginAnalystSummaryList.add(marginAnalystSummary);

            log.info(" === End === ");
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
    @Override
    public Map<String, List<Map<String, String>>>  getDealersFromMarginAnalystData() {
        List<String> dealerList = marginAnalystDataRepository.getDealersFromMarginAnalystData();
        List<Map<String, String>> dealerNameList = new ArrayList<>();
        for(String dealerName : dealerList) {
            Map<String, String> dMap = new HashMap<>();
            dMap.put("value", dealerName);

            dealerNameList.add(dMap);
        }
        return Map.of("dealers", dealerNameList);
    }
    @Override
    public Map<String, List<MarginAnalystData>> getMarginDataForAnalysisByDealer(String modelCode, String currency, Calendar monthYear, String dealer) {
        return Map.of("MarginAnalystData", marginAnalystDataRepository.getMarginDataForAnalysisByDealer(modelCode, currency, monthYear, dealer));
    }
}
