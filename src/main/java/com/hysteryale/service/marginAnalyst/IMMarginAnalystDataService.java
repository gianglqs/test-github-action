package com.hysteryale.service.marginAnalyst;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.Part;
import com.hysteryale.model.marginAnalyst.MarginAnalysisAOPRate;
import com.hysteryale.model.marginAnalyst.MarginAnalystMacro;
import com.hysteryale.model_h2.IMMarginAnalystData;
import com.hysteryale.model_h2.IMMarginAnalystSummary;
import com.hysteryale.repository.marginAnalyst.MarginAnalysisAOPRateRepository;
import com.hysteryale.repository_h2.IMMarginAnalystDataRepository;
import com.hysteryale.repository_h2.IMMarginAnalystSummaryRepository;
import com.hysteryale.service.BookingOrderService;
import com.hysteryale.service.ExchangeRateService;
import com.hysteryale.service.PartService;
import com.hysteryale.utils.CurrencyFormatUtils;
import com.hysteryale.utils.DateUtils;
import com.hysteryale.utils.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@EnableTransactionManagement
public class IMMarginAnalystDataService {
    @Resource
    IMMarginAnalystDataRepository imMarginAnalystDataRepository;
    @Resource
    IMMarginAnalystSummaryRepository imMarginAnalystSummaryRepository;
    @Resource
    MarginAnalystMacroService marginAnalystMacroService;
    @Resource
    MarginAnalystFileUploadService marginAnalystFileUploadService;
    @Resource
    MarginAnalysisAOPRateRepository marginAnalysisAOPRateRepository;
    @Resource
    BookingOrderService bookingOrderService;
    @Resource
    PartService partService;
    @Resource
    ExchangeRateService exchangeRateService;
    static HashMap<String, Integer> COLUMN_NAME = new HashMap<>();

    void getColumnName(Row row) {
        for(int i = 0; i < 23; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            COLUMN_NAME.put(columnName, i);
        }
        log.info("Column Name: " + COLUMN_NAME);
    }

    /**
     * Get ManufacturingCost value based on plant
     * if plant == HYM or SN -> then find manufacturingCost(or CostRMB) in Macro
     * else plant == [EU_Plant] -> then getting from BookingOrder (Cost_Data file)
     */
    double getManufacturingCost(String modelCode, String partNumber, String strCurrency, String plant, Calendar monthYear, double netPrice) {
        double manufacturingCost = 0;
        List<MarginAnalystMacro> marginAnalystMacroList;
        if(plant.equals("HYM"))     //HYM can be Ruyi, Staxx or Maximal
        {
            marginAnalystMacroList = marginAnalystMacroService.getMarginAnalystMacroByHYMPlant(modelCode, partNumber, strCurrency, monthYear);
            // if the Macro data is not existed -> then using exchangeNetPrice into current Currency to calculate costRMB
            if(marginAnalystMacroList.isEmpty()) {
                Optional<MarginAnalysisAOPRate> optionalAOPRate = getMarginAnalysisAOPRate(strCurrency, monthYear, plant, "annually");
                if (optionalAOPRate.isPresent())
                {
                    double aopRate = optionalAOPRate.get().getAopRate();
                    manufacturingCost = (netPrice / aopRate) * 0.9;
                }
                else
                    manufacturingCost = 0;
            }
            else
                manufacturingCost = marginAnalystMacroList.get(0).getCostRMB();

        }
        else if (plant.equals("SN")){
            marginAnalystMacroList = marginAnalystMacroService.getMarginAnalystMacroByPlant(modelCode, partNumber, strCurrency, plant, monthYear);
            // if the Macro data is not existed -> then using exchangeNetPrice into current Currency to calculate costRMB
            if(marginAnalystMacroList.isEmpty())
                manufacturingCost = netPrice * 0.9;
            else
                manufacturingCost = marginAnalystMacroList.get(0).getCostRMB();
        }
        return manufacturingCost;
    }

    private IMMarginAnalystData mapIMMarginAnalystData(Row row, String plant, String strCurrency, Calendar monthYear) {

        String modelCode = row.getCell(COLUMN_NAME.get("Model Code")).getStringCellValue();
        String partNumber = row.getCell(COLUMN_NAME.get("Part Number")).getStringCellValue();
        double listPrice = row.getCell(COLUMN_NAME.get("List Price")).getNumericCellValue();
        double netPrice = row.getCell(COLUMN_NAME.get("Net Price Each")).getNumericCellValue();

        IMMarginAnalystData imMarginAnalystData = new IMMarginAnalystData();

        imMarginAnalystData.setModelCode(modelCode);
        imMarginAnalystData.setOptionCode(partNumber);
        imMarginAnalystData.setDescription(row.getCell(COLUMN_NAME.get("Part Description"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
        imMarginAnalystData.setPlant(plant);
        imMarginAnalystData.setCurrency(strCurrency);
        imMarginAnalystData.setMonthYear(monthYear);

        // get Manufacturing Cost
        double manufacturingCost = getManufacturingCost(modelCode, partNumber, strCurrency, plant, monthYear, netPrice);
        if(imMarginAnalystData.getDescription().contains("SPED"))
            manufacturingCost += 0.9 * imMarginAnalystData.getDealerNet();

        // Initialize variables
        double aopRate = 0;
        double costUplift = 0.0;
        double surcharge = 0.015;
        double duty = 0.0;
        double freight = 0;
        double warranty = 0;

        // Assign value for variables if existed
        Optional<MarginAnalysisAOPRate> optionalMarginAnalysisAOPRate = getMarginAnalysisAOPRate(strCurrency, monthYear, plant, "annually");
        if(optionalMarginAnalysisAOPRate.isPresent()) {
            MarginAnalysisAOPRate marginAnalysisAOPRate = optionalMarginAnalysisAOPRate.get();
            aopRate = marginAnalysisAOPRate.getAopRate();
            costUplift = marginAnalysisAOPRate.getCostUplift();
            warranty = marginAnalysisAOPRate.getAddWarranty();
            surcharge = marginAnalysisAOPRate.getSurcharge();
            duty = marginAnalysisAOPRate.getDuty();
            freight = marginAnalysisAOPRate.getFreight();
        }

        // calculate marginAOP (consider to remove cuz not needed)
        double marginAOP;
        if(manufacturingCost == 0.0) {
            marginAOP =  netPrice * 0.1;
        }
        else {
            marginAOP = (netPrice - manufacturingCost * (1 + costUplift) * (1 + warranty + surcharge + duty) * aopRate) - freight;
        }

        // Set calculated data fields and round double value
        imMarginAnalystData.setDealerNet(CurrencyFormatUtils.formatDoubleValue(netPrice, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystData.setManufacturingCost(CurrencyFormatUtils.formatDoubleValue(manufacturingCost, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystData.setListPrice(CurrencyFormatUtils.formatDoubleValue(listPrice, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystData.setMargin_aop(CurrencyFormatUtils.formatDoubleValue(marginAOP, CurrencyFormatUtils.decimalFormatFourDigits));
        return imMarginAnalystData;
    }

    /**
     * Calculate MarginAnalystData and save into In-memory database
     * @param fileUUID identifier of uploaded file
     */
    public void calculateNonUSMarginAnalystData(String originalFileName, String fileUUID) throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("upload_files.base-folder");
        String fileName = marginAnalystFileUploadService.getFileNameByUUID(fileUUID); // fileName has been hashed

        log.info(originalFileName);
        Pattern pattern = Pattern.compile("(.*)[_|\\s](.*)(.xlsx)");
        Matcher matcher = pattern.matcher(originalFileName);
        if(matcher.find()) {
            // Extract plant and currency from fileName
            String plant = matcher.group(1);
            String strCurrency = matcher.group(2);
            log.info(plant + "-" + strCurrency);

            FileInputStream is = new FileInputStream(baseFolder + "/" + fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);
            List<IMMarginAnalystData> imMarginAnalystDataList = new ArrayList<>();

            // Initialize variables for assigning later in for loop
            Calendar monthYear = Calendar.getInstance();

            for(Row row : sheet) {
                if(row.getRowNum() == 0) {
                    getColumnName(row);
                } else if (!row.getCell(COLUMN_NAME.get("Part Number"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {

                    String strDate = row.getCell(COLUMN_NAME.get("Order Booked Date")).getStringCellValue();
                    if(!strDate.isEmpty()) {
                        monthYear = parseMonthYear(strDate);
                    }
                    IMMarginAnalystData imMarginAnalystData = mapIMMarginAnalystData(row, plant, strCurrency, monthYear);
                    imMarginAnalystData.setFileUUID(fileUUID);
                    imMarginAnalystDataList.add(imMarginAnalystData);
                }
            }
            log.info("IMMarginAnalystData saved: " + imMarginAnalystDataList.size());
            imMarginAnalystDataRepository.saveAll(imMarginAnalystDataList);
            imMarginAnalystDataList.clear();
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File's name is not in appropriate format");

    }

    /**
     * Calculate MarginAnalystSummary and save into In-memory database
     */
    public void calculateNonUSMarginAnalystSummary(String fileUUID, String originalFileName, String durationUnit) {
        Pattern pattern = Pattern.compile("(.*)[_|\\s](.*)(.xlsx)");
        Matcher matcher = pattern.matcher(originalFileName);
        if(matcher.find()) {
            log.info(durationUnit + " calculating...");

            // Extract plant and currency from fileName
            String plant = matcher.group(1);
            String strCurrency = matcher.group(2);

            // forEach modelCode -> get MarginAnalystData -> then calculate MarginAnalystSummary
            List<String> modelCodeList = imMarginAnalystDataRepository.getModelCodesByFileUUID(fileUUID);
            for(String modelCode : modelCodeList) {
                IMMarginAnalystSummary imMarginAnalystSummary = new IMMarginAnalystSummary();

                List<IMMarginAnalystData> imMarginAnalystDataList = imMarginAnalystDataRepository.getIMMarginAnalystData(modelCode, strCurrency, fileUUID);
                Calendar monthYear =  imMarginAnalystDataList.get(0).getMonthYear();

                // Initialize values
                double costUplift = 0.0;
                double warranty = 0.0;
                double surcharge = 0.015;
                double duty = 0.0;
                double freight = 0.0;
                boolean liIonIncluded = false; // NO

                double aopRate = 0;     // considered as the ExchangeRate
                Optional<MarginAnalysisAOPRate> optionalMarginAnalysisAOPRate = getMarginAnalysisAOPRate(strCurrency, monthYear, plant, durationUnit);
                if(optionalMarginAnalysisAOPRate.isPresent()) {
                    MarginAnalysisAOPRate marginAnalysisAOPRate = optionalMarginAnalysisAOPRate.get();
                    aopRate = marginAnalysisAOPRate.getAopRate();
                    costUplift = marginAnalysisAOPRate.getCostUplift();
                    warranty = marginAnalysisAOPRate.getAddWarranty();
                    surcharge = marginAnalysisAOPRate.getSurcharge();
                    duty = marginAnalysisAOPRate.getDuty();
                    freight = marginAnalysisAOPRate.getFreight();
                }

                double totalListPrice = 0.0;
                double dealerNet = 0.0;
                double totalManufacturingCost = 0.0;

                double warrantyCost;
                double surchargeCost;
                double dutyCost;
                double totalCostWithoutFreight;
                double totalCostWithFreight = 0;

                // calculate sum of the listPrice, costRMB and dealerNet
                log.info(modelCode + " - " + imMarginAnalystDataList.size());
                for(IMMarginAnalystData data : imMarginAnalystDataList) {
                    totalListPrice += data.getListPrice();
                    totalManufacturingCost += data.getManufacturingCost();
                    dealerNet += data.getDealerNet();
                }

                double totalCost = totalManufacturingCost * (1 + costUplift) * (1 + warranty + surcharge + duty);
                double blendedDiscount = 1 - (dealerNet / totalListPrice);

                double fullCostAOPRate = totalCost * aopRate;
                double manufacturingCostUSD = totalManufacturingCost;

                // manufacturingCost in HYM plant is in RMB Currency -> then exchange to USD or AUD
                // manufacturingCost in SN plant is in USD Currency -> no need to exchange
                if(plant.equals("HYM"))
                    manufacturingCostUSD = totalManufacturingCost * aopRate;

                warrantyCost = manufacturingCostUSD * warranty;
                surchargeCost = manufacturingCostUSD * surcharge;
                dutyCost = manufacturingCostUSD * duty;
                totalCostWithoutFreight = manufacturingCostUSD + warrantyCost + surchargeCost + dutyCost;

                if(strCurrency.equals("AUD")) {
                    fullCostAOPRate = (totalCost * aopRate) + freight;       // AUD only
                    totalCostWithFreight = totalCostWithoutFreight + freight * aopRate;
                }

                double margin = dealerNet - fullCostAOPRate;

                // check whether dealerNet == 0 or not
                double marginPercentAopRate;
                if(dealerNet == 0)
                    marginPercentAopRate = 0;
                else marginPercentAopRate = margin / dealerNet;

                imMarginAnalystSummary.setModelCode(modelCode);
                imMarginAnalystSummary.setCurrency(strCurrency);
                imMarginAnalystSummary.setTotalManufacturingCost(CurrencyFormatUtils.formatDoubleValue(totalManufacturingCost, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setCostUplift(costUplift);
                imMarginAnalystSummary.setAddWarranty(warranty);
                imMarginAnalystSummary.setSurcharge(surcharge);
                imMarginAnalystSummary.setDuty(duty);
                imMarginAnalystSummary.setFreight(freight);
                imMarginAnalystSummary.setLiIonIncluded(liIonIncluded);
                imMarginAnalystSummary.setTotalCost(CurrencyFormatUtils.formatDoubleValue(totalCost, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setTotalListPrice(CurrencyFormatUtils.formatDoubleValue(totalListPrice, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setBlendedDiscountPercentage(CurrencyFormatUtils.formatDoubleValue(blendedDiscount, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setDealerNet(CurrencyFormatUtils.formatDoubleValue(dealerNet, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setMargin(CurrencyFormatUtils.formatDoubleValue(margin, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setMarginAopRate(aopRate);
                imMarginAnalystSummary.setFileUUID(fileUUID);
                imMarginAnalystSummary.setPlant(plant);

                imMarginAnalystSummary.setWarrantyCost(warrantyCost);
                imMarginAnalystSummary.setSurchargeCost(surchargeCost);
                imMarginAnalystSummary.setDutyCost(dutyCost);
                imMarginAnalystSummary.setTotalCostWithoutFreight(totalCostWithoutFreight);
                imMarginAnalystSummary.setTotalCostWithFreight(totalCostWithFreight);
                imMarginAnalystSummary.setManufacturingCostUSD(manufacturingCostUSD);


                if(durationUnit.equals("monthly")) {
                    imMarginAnalystSummary.setMonthYear(monthYear);
                    // monthly valued
                    imMarginAnalystSummary.setFullMonthlyRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
                    imMarginAnalystSummary.setMarginPercentMonthlyRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
                }
                else {
                    // annually valued
                    // if MarginAnalystSummary is annual then set {Date into 28, Month into JANUARY} (monthly date would be 1)
                    Calendar annualDate = Calendar.getInstance();
                    annualDate.set(monthYear.get(Calendar.YEAR), Calendar.JANUARY, 28);

                    imMarginAnalystSummary.setMonthYear(annualDate);
                    imMarginAnalystSummary.setFullCostAopRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
                    imMarginAnalystSummary.setMarginPercentAopRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
                }

                imMarginAnalystSummaryRepository.save(imMarginAnalystSummary);
            }
        }
    }
    private Optional<MarginAnalysisAOPRate> getMarginAnalysisAOPRate(String currency, Calendar monthYear, String plant, String durationUnit) {
        return marginAnalysisAOPRateRepository.getMarginAnalysisAOPRate(plant, currency, monthYear, durationUnit);
    }

    /**
     * Parse String to Calendar with format MMM dd yyyy (Sep 12 2023)
     */
    private Calendar parseMonthYear(String strDate) {
        Calendar calendar = Calendar.getInstance();
        Pattern pattern = Pattern.compile("(\\w{3}) (\\d{2}) (\\d{4})");
        Matcher matcher = pattern.matcher(strDate);
        if(matcher.find()) {
            String strMonth = matcher.group(1);
            int year = Integer.parseInt(matcher.group(3));

            int monthIndex = DateUtils.monthMap.get(strMonth);

            calendar.set(year, monthIndex, 1);
        }
        return calendar;
    }

    /**
     * Get the In-memory Data which has already been calculated in the uploaded file if the plant is non-US
     * Calculate new MarginData (by getting Parts from DB) if the plant is US plant
     */
    public List<IMMarginAnalystData> getIMMarginAnalystData(String modelCode, String strCurrency, String fileUUID, String orderNumber) {

        Calendar monthYear = Calendar.getInstance();
        monthYear.set(2023, Calendar.SEPTEMBER, 1);

        // check the plant of the modelCode
        // if we cannot find any plant with the modelCode --> then return nothing
        Optional<BookingOrder> optionalBookingOrder = bookingOrderService.getDistinctBookingOrderByModelCode(modelCode);
        if(optionalBookingOrder.isPresent()) {
            String plant = optionalBookingOrder.get().getProductDimension().getPlant();
            log.info("Model Code: " + modelCode + " -> Plant: " + plant);
            log.info("Order Number: " + orderNumber);

            // if it is US-plant modelCode
            if(!plant.equals("HYM") && !plant.equals("SN") && !plant.equals("Ruyi") && !plant.equals("Maximal") && !plant.equals("Staxx")) {
                // using OrderNumber to calculate data
                List<IMMarginAnalystData> imMarginAnalystDataList = imMarginAnalystDataRepository.getEUPlantIMMarginAnalystData(modelCode, orderNumber, strCurrency);
                if(imMarginAnalystDataList.isEmpty()) imMarginAnalystDataList = calculateUSPlantMarginData(modelCode, strCurrency, orderNumber);

                calculateEUPlantMarginSummary(modelCode, strCurrency, monthYear, "annually", orderNumber);
                calculateEUPlantMarginSummary(modelCode, strCurrency, monthYear, "monthly", orderNumber);

                return imMarginAnalystDataList;
            }
            else {
                // get the data from uploaded file
                return imMarginAnalystDataRepository.getIMMarginAnalystData(modelCode, strCurrency, fileUUID);
            }

        }
        else
            return new ArrayList<>();
    }

    public Map<String, Object> getIMMarginAnalystSummary(String modelCode, String strCurrency, String fileUUID, String orderNumber) {

        Calendar monthYear = Calendar.getInstance();
        monthYear.set(2023, Calendar.SEPTEMBER, 1);

        List<IMMarginAnalystSummary> imMarginAnalystSummaryList = imMarginAnalystSummaryRepository.getIMMarginAnalystSummary(modelCode, strCurrency, fileUUID);
        if(imMarginAnalystSummaryList.isEmpty()) {
            Optional<BookingOrder> optionalBookingOrder = bookingOrderService.getDistinctBookingOrderByModelCode(modelCode);
            if(optionalBookingOrder.isPresent()) {
                String plant = optionalBookingOrder.get().getProductDimension().getPlant();

                if (!plant.equals("HYM") && !plant.equals("SN") && !plant.equals("Ruyi") && !plant.equals("Maximal") && !plant.equals("Staxx")) {
                    imMarginAnalystSummaryList = getUSPlantMarginSummary(modelCode, strCurrency, monthYear, orderNumber);
                }
                else
                    imMarginAnalystSummaryList = new ArrayList<>();
            }
            else
                imMarginAnalystSummaryList = new ArrayList<>();
        }

        log.info("Number of summary " + imMarginAnalystSummaryList.size());

        IMMarginAnalystSummary monthly = new IMMarginAnalystSummary();
        IMMarginAnalystSummary annually = new IMMarginAnalystSummary();
        for(IMMarginAnalystSummary summary : imMarginAnalystSummaryList) {
            if(summary.getMonthYear().get(Calendar.DATE) == 1)
                monthly = summary;
            else
                annually = summary;
        }

        return Map.of(
                "MarginAnalystSummaryMonthly", monthly,
                "MarginAnalystSummaryAnnually", annually
        );
    }

    List<IMMarginAnalystSummary> getUSPlantMarginSummary(String modelCode, String strCurrency, Calendar monthYear, String orderNumber) {
        int year = monthYear.get(Calendar.YEAR);
        int month = monthYear.get(Calendar.MONTH) + 1;
        List<IMMarginAnalystSummary> imMarginAnalystSummaryList = new ArrayList<>();
        imMarginAnalystSummaryList.add(
                !imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryMonthlyByMonthYear(modelCode, strCurrency, year, month, orderNumber).isEmpty()
                    ? imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryMonthlyByMonthYear(modelCode, strCurrency, year, month, orderNumber).get(0)
                    : new IMMarginAnalystSummary()
        );
        imMarginAnalystSummaryList.add(
                !imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryAnnuallyByMonthYear(modelCode, strCurrency, year, orderNumber).isEmpty()
                    ? imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryAnnuallyByMonthYear(modelCode, strCurrency, year, orderNumber).get(0)
                    : new IMMarginAnalystSummary()
        );
        return imMarginAnalystSummaryList;
    }

    /**
     * Calculate IMMarginAnalystData of US plant by querying Part from BookingOrder and Part
     */
    public List<IMMarginAnalystData> calculateUSPlantMarginData(String modelCode, String strCurrency, String orderNumber) {
        Calendar monthYear = Calendar.getInstance();
        monthYear.set(2023, Calendar.SEPTEMBER, 1);

        Optional<BookingOrder> optionalBookingOrder = bookingOrderService.getBookingOrderByOrderNumber(orderNumber);
        if(optionalBookingOrder.isPresent()) {
            List<IMMarginAnalystData> imMarginAnalystDataList = new ArrayList<>();
            double manufacturingCost = optionalBookingOrder.get().getTotalCost();

            // For each Part in ModelCode -> calculate IMMarginAnalystData
            List<Part> partList = partService.getDistinctPart(modelCode, strCurrency);
            log.info("Number of part: "  + partList.size());
            for(Part part : partList) {
                IMMarginAnalystData imMarginAnalystData = new IMMarginAnalystData();
                imMarginAnalystData.setSPED(false);

                String partNumber = part.getPartNumber();
                double listPrice = part.getListPrice();
                double netPrice = part.getNetPriceEach();

                // Initialize variables
                double aopRate = 0;
                double costUplift = 0.0;
                double surcharge = 0.015;
                double duty = 0.0;
                double freight = 0;
                double warranty = 0;

                String plant = "";
                // Assign value for variables if existed
                Optional<MarginAnalysisAOPRate> optionalMarginAnalysisAOPRate = getMarginAnalysisAOPRate(strCurrency, monthYear, plant, "annually");
                if(optionalMarginAnalysisAOPRate.isPresent()) {
                    MarginAnalysisAOPRate marginAnalysisAOPRate = optionalMarginAnalysisAOPRate.get();
                    aopRate = marginAnalysisAOPRate.getAopRate();
                    costUplift = marginAnalysisAOPRate.getCostUplift();
                    warranty = marginAnalysisAOPRate.getAddWarranty();
                    surcharge = marginAnalysisAOPRate.getSurcharge();
                    duty = marginAnalysisAOPRate.getDuty();
                    freight = marginAnalysisAOPRate.getFreight();
                }

                double manufacturingCostWithSPED = manufacturingCost;
                if(part.isSPED())
                {
                    imMarginAnalystData.setSPED(true);
                    manufacturingCostWithSPED += 0.9 * part.getNetPriceEach();
                }

                // calculate marginAOP (consider to remove cuz not needed)
                double marginAOP;
                if(manufacturingCost == 0.0) {
                    marginAOP =  netPrice * 0.1;
                }
                else {
                    marginAOP = (netPrice - manufacturingCostWithSPED * (1 + costUplift) * (1 + warranty + surcharge + duty) * aopRate) - freight;
                }

                // Assigning value for imMarginAnalystData
                imMarginAnalystData.setOrderNumber(orderNumber);
                imMarginAnalystData.setModelCode(modelCode);
                imMarginAnalystData.setOptionCode(partNumber);
                imMarginAnalystData.setCurrency(strCurrency);
                imMarginAnalystData.setMonthYear(monthYear);
                imMarginAnalystData.setDealerNet(CurrencyFormatUtils.formatDoubleValue(netPrice, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystData.setManufacturingCost(CurrencyFormatUtils.formatDoubleValue(manufacturingCostWithSPED, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystData.setListPrice(CurrencyFormatUtils.formatDoubleValue(listPrice, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystData.setMargin_aop(CurrencyFormatUtils.formatDoubleValue(marginAOP, CurrencyFormatUtils.decimalFormatFourDigits));

                imMarginAnalystDataList.add(imMarginAnalystData);
            }

            log.info("Number of IMMarginAnalystData: "  + imMarginAnalystDataList.size());
            imMarginAnalystDataRepository.saveAll(imMarginAnalystDataList);
            return imMarginAnalystDataList;
        }
        return new ArrayList<>();
    }
    public void calculateEUPlantMarginSummary(String modelCode, String strCurrency, Calendar monthYear, String durationUnit, String orderNumber) {
        IMMarginAnalystSummary imMarginAnalystSummary = new IMMarginAnalystSummary();
        double defMFGCost = 0;
        Optional<BookingOrder> optionalBookingOrder = bookingOrderService.getBookingOrderByOrderNumber(orderNumber);
        if(optionalBookingOrder.isPresent())
            defMFGCost = optionalBookingOrder.get().getTotalCost();

        // Initialize values
        double costUplift = 0.0;
        double warranty = 0.0;
        double surcharge = 0.015;
        double duty = 0.0;
        double freight = 0.0;
        boolean liIonIncluded = false; // NO

        // ExchangeRate from strCurrency to USD
        double aopRate = exchangeRateService.getExchangeRate(strCurrency, "USD", monthYear).getRate();     // considered as the ExchangeRate

        double totalListPrice = 0.0;
        double dealerNet = 0.0;
        double totalManufacturingCost = defMFGCost;

        double warrantyCost;
        double surchargeCost;
        double dutyCost;
        double totalCostWithoutFreight;
        double totalCostWithFreight = 0;

        // calculate sum of the listPrice, costRMB and dealerNet
        List<IMMarginAnalystData> imMarginAnalystDataList = imMarginAnalystDataRepository.getEUPlantIMMarginAnalystData(modelCode, orderNumber, strCurrency);
        log.info(modelCode + " - " + imMarginAnalystDataList.size());
        for(IMMarginAnalystData data : imMarginAnalystDataList) {
            totalListPrice += data.getListPrice();
            dealerNet += data.getDealerNet();
            if(data.isSPED())
                totalManufacturingCost += (data.getManufacturingCost() - defMFGCost);

        }

        double totalCost = totalManufacturingCost * (1 + costUplift) * (1 + warranty + surcharge + duty);
        double blendedDiscount = 1 - (dealerNet / totalListPrice);

        double fullCostAOPRate = totalCost;
        double manufacturingCostUSD = totalManufacturingCost * aopRate;

        warrantyCost = manufacturingCostUSD * warranty;
        surchargeCost = manufacturingCostUSD * surcharge;
        dutyCost = manufacturingCostUSD * duty;
        totalCostWithoutFreight = manufacturingCostUSD + warrantyCost + surchargeCost + dutyCost;

        if(strCurrency.equals("AUD")) {
            fullCostAOPRate = totalCost + freight;       // AUD only
            totalCostWithFreight = totalCostWithoutFreight + freight * aopRate;
        }

        double margin = dealerNet - fullCostAOPRate;

        // check whether dealerNet == 0 or not
        double marginPercentAopRate;
        if(dealerNet == 0)
            marginPercentAopRate = 0;
        else marginPercentAopRate = margin / dealerNet;

        imMarginAnalystSummary.setModelCode(modelCode);
        imMarginAnalystSummary.setCurrency(strCurrency);
        imMarginAnalystSummary.setTotalManufacturingCost(CurrencyFormatUtils.formatDoubleValue(totalManufacturingCost, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystSummary.setCostUplift(costUplift);
        imMarginAnalystSummary.setAddWarranty(warranty);
        imMarginAnalystSummary.setSurcharge(surcharge);
        imMarginAnalystSummary.setDuty(duty);
        imMarginAnalystSummary.setFreight(freight);
        imMarginAnalystSummary.setLiIonIncluded(liIonIncluded);
        imMarginAnalystSummary.setTotalCost(CurrencyFormatUtils.formatDoubleValue(totalCost, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystSummary.setTotalListPrice(CurrencyFormatUtils.formatDoubleValue(totalListPrice, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystSummary.setBlendedDiscountPercentage(CurrencyFormatUtils.formatDoubleValue(blendedDiscount, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystSummary.setDealerNet(CurrencyFormatUtils.formatDoubleValue(dealerNet, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystSummary.setMargin(CurrencyFormatUtils.formatDoubleValue(margin, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystSummary.setMarginAopRate(aopRate);
        imMarginAnalystSummary.setOrderNumber(orderNumber);

        imMarginAnalystSummary.setWarrantyCost(warrantyCost);
        imMarginAnalystSummary.setSurchargeCost(surchargeCost);
        imMarginAnalystSummary.setDutyCost(dutyCost);
        imMarginAnalystSummary.setTotalCostWithoutFreight(totalCostWithoutFreight);
        imMarginAnalystSummary.setTotalCostWithFreight(totalCostWithFreight);
        imMarginAnalystSummary.setManufacturingCostUSD(manufacturingCostUSD);


        if(durationUnit.equals("monthly")) {
            imMarginAnalystSummary.setMonthYear(monthYear);
            // monthly valued
            imMarginAnalystSummary.setFullMonthlyRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
            imMarginAnalystSummary.setMarginPercentMonthlyRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
        }
        else {
            // annually valued
            // if MarginAnalystSummary is annual then set {Date into 28, Month into JANUARY} (monthly date would be 1)
            Calendar annualDate = Calendar.getInstance();
            annualDate.set(monthYear.get(Calendar.YEAR), Calendar.JANUARY, 28);

            imMarginAnalystSummary.setMonthYear(annualDate);
            imMarginAnalystSummary.setFullCostAopRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
            imMarginAnalystSummary.setMarginPercentAopRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
        }
        imMarginAnalystSummaryRepository.save(imMarginAnalystSummary);
    }
}
