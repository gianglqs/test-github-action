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
        log.info(aopRate + " " + costUplift + " " + warranty + " " + surcharge + " " + duty + " " + freight);

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
    public void calculateMarginAnalystData(String originalFileName, String fileUUID) throws IOException {
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
    public void calculateMarginAnalystSummary(String fileUUID, String originalFileName, String durationUnit) {
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
        log.info(strDate);
        if(matcher.find()) {
            String strMonth = matcher.group(1);
            int year = Integer.parseInt(matcher.group(3));

            int monthIndex = DateUtils.monthMap.get(strMonth);

            calendar.set(year, monthIndex, 1);
        }
        return calendar;
    }

    public List<IMMarginAnalystData> getIMMarginAnalystData(String modelCode, String strCurrency, String fileUUID) {

        Calendar monthYear = Calendar.getInstance();
        int year = monthYear.get(Calendar.YEAR);
        int month = monthYear.get(Calendar.MONTH) - 2;

        monthYear.set(year, month - 1, 1);

        // Checking plant of the modelCode and get totalCost for calculating IMMarginAnalystData
        log.info(year + " " + month);
        List<BookingOrder> optionalBookingOrder = bookingOrderService.getDistinctBookingOrderByModelCode(modelCode, year, month);
        log.info("" + optionalBookingOrder.size());
        if(!optionalBookingOrder.isEmpty()) {
            String plant = optionalBookingOrder.get(0).getProductDimension().getPlant();
            log.info("Plant: " + plant);
            double totalCost = optionalBookingOrder.get(0).getTotalCost();

            if(!plant.equals("HYM") && !plant.equals("SN") && !plant.equals("Ruyi") && !plant.equals("Maximal") && !plant.equals("Staxx"))
            {
                List<IMMarginAnalystData> imMarginAnalystDataList = imMarginAnalystDataRepository.getEUPlantIMMarginAnalystData(modelCode, monthYear, strCurrency);
                if(imMarginAnalystDataList.isEmpty())
                    imMarginAnalystDataList = calculateEUPlantMarginData(modelCode, strCurrency, totalCost, year, month, optionalBookingOrder.get(0).getOrderNo());
                calculateEUPlantMarginSummary(modelCode, strCurrency, monthYear, "annually");
                calculateEUPlantMarginSummary(modelCode, strCurrency, monthYear, "monthly");
                return imMarginAnalystDataList;
            }
            else{
                if(fileUUID == null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please input a file");
                return imMarginAnalystDataRepository.getIMMarginAnalystData(modelCode, strCurrency, fileUUID);
            }
        }
        else
            return null;
    }

    public Map<String, Object> getIMMarginAnalystSummary(String modelCode, String strCurrency, String fileUUID) {

        Calendar monthYear = Calendar.getInstance();
        int year = monthYear.get(Calendar.YEAR);
        int month = monthYear.get(Calendar.MONTH) - 2;

        monthYear.set(year, month, 1);
        List<IMMarginAnalystSummary> imMarginAnalystSummaryList = new ArrayList<>();

        List<BookingOrder> optionalBookingOrder = bookingOrderService.getDistinctBookingOrderByModelCode(modelCode, year, month);
        if(!optionalBookingOrder.isEmpty()) {
            String plant = optionalBookingOrder.get(0).getProductDimension().getPlant();
            if(!plant.equals("HYM") && !plant.equals("SN") && !plant.equals("Ruyi") && !plant.equals("Maximal") && !plant.equals("Staxx")) {
                imMarginAnalystSummaryList = getEUPlantMarginSummary(modelCode, strCurrency, monthYear);
            }
            else {
                imMarginAnalystSummaryList = imMarginAnalystSummaryRepository.getIMMarginAnalystSummary(modelCode, strCurrency, fileUUID);
            }
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

    List<IMMarginAnalystSummary> getEUPlantMarginSummary(String modelCode, String strCurrency, Calendar monthYear) {
        int year = monthYear.get(Calendar.YEAR);
        int month = monthYear.get(Calendar.MONTH);
        List<IMMarginAnalystSummary> imMarginAnalystSummaryList = new ArrayList<>();
        imMarginAnalystSummaryList.add(imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryMonthlyByMonthYear(modelCode, strCurrency, year, month).get(0));
        imMarginAnalystSummaryList.add(imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryAnnuallyByMonthYear(modelCode, strCurrency, year).get(0));
        return imMarginAnalystSummaryList;
    }

    /**
     * Calculate IMMarginAnalystData of EU plant by query Part from BookingOrder and Part
     */
    public List<IMMarginAnalystData> calculateEUPlantMarginData(String modelCode, String strCurrency, double manufacturingCost, int year, int month, String orderNo) {
        Calendar monthYear = Calendar.getInstance();
        monthYear.set(year, month - 1, 1);

        List<IMMarginAnalystData> imMarginAnalystDataList = new ArrayList<>();
        log.info(year + " " + month);

        // calculate manufacturingCost based on orderNumber
        double totalDealerNetOfSPED = 0.0;
        Set<Part> partForMFGCost = partService.getPartByOrderNo(orderNo);
        for(Part part : partForMFGCost) {
            if(part.isSPED())
                totalDealerNetOfSPED += part.getNetPriceEach();
        }
        manufacturingCost = manufacturingCost + 0.9 * totalDealerNetOfSPED;


        // For each Part in ModelCode -> calculate IMMarginAnalystData
        List<Part> partList = partService.getDistinctPart(modelCode, monthYear, strCurrency);
        log.info("Number of part: "  + partList.size());
        for(Part part : partList) {
            IMMarginAnalystData imMarginAnalystData = new IMMarginAnalystData();

            String partNumber = part.getPartNumber();
            double listPrice = part.getListPrice();
            double netPrice = part.getNetPriceEach();

            imMarginAnalystData.setModelCode(modelCode);
            imMarginAnalystData.setOptionCode(partNumber);
            imMarginAnalystData.setCurrency(strCurrency);
            imMarginAnalystData.setMonthYear(monthYear);

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
            log.info(aopRate + " " + costUplift + " " + warranty + " " + surcharge + " " + duty + " " + freight);

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

            imMarginAnalystDataList.add(imMarginAnalystData);
        }

        log.info("Number of IMMarginAnalystData: "  + imMarginAnalystDataList.size());
        imMarginAnalystDataRepository.saveAll(imMarginAnalystDataList);
        return imMarginAnalystDataList;
    }
    public void calculateEUPlantMarginSummary(String modelCode, String strCurrency, Calendar monthYear, String durationUnit) {

        IMMarginAnalystSummary imMarginAnalystSummary = new IMMarginAnalystSummary();

        List<IMMarginAnalystData> imMarginAnalystDataList = imMarginAnalystDataRepository.getEUPlantIMMarginAnalystData(modelCode, monthYear, strCurrency);

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
            totalManufacturingCost = data.getManufacturingCost();
            dealerNet += data.getDealerNet();
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
