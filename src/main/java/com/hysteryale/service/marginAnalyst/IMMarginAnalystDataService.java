package com.hysteryale.service.marginAnalyst;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.marginAnalyst.MarginAnalysisAOPRate;
import com.hysteryale.model_h2.IMMarginAnalystData;
import com.hysteryale.model_h2.IMMarginAnalystSummary;
import com.hysteryale.repository.marginAnalyst.MarginAnalysisAOPRateRepository;
import com.hysteryale.repository_h2.IMMarginAnalystDataRepository;
import com.hysteryale.repository_h2.IMMarginAnalystSummaryRepository;
import com.hysteryale.service.BookingOrderService;
import com.hysteryale.service.ExchangeRateService;
import com.hysteryale.service.FileUploadService;
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
    FileUploadService fileUploadService;
    @Resource
    MarginAnalysisAOPRateRepository marginAnalysisAOPRateRepository;
    @Resource
    BookingOrderService bookingOrderService;
    @Resource
    ExchangeRateService exchangeRateService;
    static HashMap<String, Integer> COLUMN_NAME = new HashMap<>();

    void getColumnName(Row row) {
        for(int i = 0; i < 22; i++) {
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
    double getManufacturingCost(String modelCode, String partNumber, String strCurrency, String plant, Calendar monthYear, double dealerNet, double exchangeRate) {


        //HYM can be Ruyi, Staxx or Maximal
        List<String> plantList = plant.equals("HYM")
                ? new ArrayList<>(List.of("HYM", "Ruyi", "Staxx", "Maximal"))
                : new ArrayList<>(List.of("SN"));
        Double manufacturingCost = marginAnalystMacroService.getManufacturingCost(modelCode, partNumber, strCurrency, plantList, monthYear);

        // if manufacturingCost is null -> it will equal 90% of DealerNet
        return Objects.requireNonNullElseGet(manufacturingCost, () -> plant.equals("HYM")
                ? (dealerNet / exchangeRate) * 0.9
                : dealerNet * 0.9);
    }

    /**
     * Mapping the data from uploaded files / template files as SN_AUD ... into a model
     */
    private IMMarginAnalystData mapIMMarginAnalystData(Row row, String plant, String strCurrency, Calendar monthYear) {

        // Initialize variables
        double aopRate = 1;
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

        String modelCode = row.getCell(COLUMN_NAME.get("Model Code")).getStringCellValue();
        String partNumber = row.getCell(COLUMN_NAME.get("Part Number")).getStringCellValue();
        String description = row.getCell(COLUMN_NAME.get("Part Description"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
        double listPrice = row.getCell(COLUMN_NAME.get("List Price")).getNumericCellValue();
        double netPrice = row.getCell(COLUMN_NAME.get("Net Price Each")).getNumericCellValue();
        int type = (int) row.getCell(COLUMN_NAME.get("#")).getNumericCellValue();

        IMMarginAnalystData imMarginAnalystData =
                new IMMarginAnalystData(
                        plant, modelCode, partNumber, description,
                        CurrencyFormatUtils.formatDoubleValue(listPrice, CurrencyFormatUtils.decimalFormatFourDigits),
                        monthYear, strCurrency,
                        CurrencyFormatUtils.formatDoubleValue(netPrice, CurrencyFormatUtils.decimalFormatFourDigits)
                );

        // Assign ManufacturingCost
        // if Part is marked as SPED then
        // ManufacturingCost = ManufacturingCost * ExchangeRate based on Plant * (1 + costUplift) + 90% of DealerNet

        // ManufacturingCost must be multiplied by aopRate (to exchange the currency)
        // ManufacturingCost can be in RMB(CNY), USD or AUD -> then it must be exchanged to be the same as the currency of DealerNet
        double manufacturingCost = getManufacturingCost(modelCode, partNumber, strCurrency, plant, monthYear, netPrice, aopRate);
        boolean isSPED = false;
        if(description.contains("SPED")) {
            isSPED = true;
            manufacturingCost = manufacturingCost * aopRate * (1 + costUplift) + 0.9 * netPrice;
        }

        // calculate marginAOP (consider to remove cuz not needed)
        double marginAOP;
        if(manufacturingCost == 0.0)
            marginAOP =  netPrice * 0.1;
        else {
            if(isSPED)
                marginAOP = (netPrice - manufacturingCost * (1 + warranty + surcharge + duty)) - freight;
            else
                marginAOP = (netPrice - manufacturingCost * (1 + costUplift) * (1 + warranty + surcharge + duty) * aopRate) - freight;
        }

        // after finishing calculation => exchange manufacturingCost back to based currency (for HYM is RMB; for SN is USD)
        manufacturingCost = isSPED ? (manufacturingCost / aopRate) : manufacturingCost;

        imMarginAnalystData.setManufacturingCost(CurrencyFormatUtils.formatDoubleValue(manufacturingCost, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystData.setMargin_aop(CurrencyFormatUtils.formatDoubleValue(marginAOP, CurrencyFormatUtils.decimalFormatFourDigits));
        imMarginAnalystData.setType(type);

        return imMarginAnalystData;
    }

    public String checkPlantOfFile(String fileUUID) throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("upload_files.base-folder");
        String fileName = fileUploadService.getFileNameByUUID(fileUUID); // fileName has been hashed

        FileInputStream is = new FileInputStream(baseFolder + "/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        Sheet sheet = workbook.getSheetAt(0);
        Row columnRow = sheet.getRow(0);
        getColumnName(columnRow);
        Row row = sheet.getRow(1);

        String modelCode = row.getCell(COLUMN_NAME.get("Model Code"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
        log.info(modelCode);
        Optional<BookingOrder> optionalBooking = bookingOrderService.getDistinctBookingOrderByModelCode(modelCode);
        if(optionalBooking.isPresent()) {
            return optionalBooking.get().getProductDimension().getPlant();
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Model Code not found");
    }

    /**
     * Calculate MarginAnalystData and save into In-memory database
     * @param fileUUID identifier of uploaded file
     */
    public void calculateNonUSMarginAnalystData(String fileUUID, String plant, String currency) throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("upload_files.base-folder");
        String fileName = fileUploadService.getFileNameByUUID(fileUUID); // fileName has been hashed

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
                IMMarginAnalystData imMarginAnalystData = mapIMMarginAnalystData(row, plant, currency, monthYear);
                imMarginAnalystData.setFileUUID(fileUUID);
                imMarginAnalystDataList.add(imMarginAnalystData);
            }
        }
        log.info("IMMarginAnalystData saved: " + imMarginAnalystDataList.size());
        imMarginAnalystDataRepository.saveAll(imMarginAnalystDataList);
        imMarginAnalystDataList.clear();
    }

    /**
     * Calculate MarginAnalystSummary and save into In-memory database
     */
    public void calculateNonUSMarginAnalystSummary(String fileUUID, String plant, String strCurrency, String durationUnit, Integer type) {
        log.info(durationUnit + " calculating...");

        // forEach modelCode -> get MarginAnalystData -> then calculate MarginAnalystSummary
        List<String> modelCodeList = imMarginAnalystDataRepository.getModelCodesByFileUUID(fileUUID);
        for(String modelCode : modelCodeList) {
            log.info(modelCode);
            log.info(type + "");
            log.info(strCurrency);
            List<IMMarginAnalystData> imMarginAnalystDataList = imMarginAnalystDataRepository.getIMMarginAnalystData(modelCode, strCurrency, fileUUID, type);
            if(imMarginAnalystDataList.isEmpty()) {
                log.info("Empty List of Margin Data: {modelCode: " + modelCode + ", type: " + type + ", currency: " + strCurrency + "}");
                continue;
            }
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

            double warrantyCost = manufacturingCostUSD * warranty;
            double surchargeCost = manufacturingCostUSD * surcharge;
            double dutyCost = manufacturingCostUSD * duty;
            double totalCostWithoutFreight = manufacturingCostUSD + warrantyCost + surchargeCost + dutyCost;
            double totalCostWithFreight = 0;

            if(strCurrency.equals("AUD")) {
                fullCostAOPRate = (totalCost * aopRate) + freight;       // AUD only
                totalCostWithFreight = totalCostWithoutFreight + freight * aopRate;
            }

            double margin = dealerNet - fullCostAOPRate;

            // check whether dealerNet == 0 or not
            double marginPercentAopRate = dealerNet == 0 ? 0 : margin / dealerNet;

            IMMarginAnalystSummary imMarginAnalystSummary = new IMMarginAnalystSummary
                    (
                            modelCode, strCurrency,
                            CurrencyFormatUtils.formatDoubleValue(totalManufacturingCost, CurrencyFormatUtils.decimalFormatFourDigits),
                            costUplift, warranty, surcharge, duty, freight, liIonIncluded,
                            CurrencyFormatUtils.formatDoubleValue(totalCost, CurrencyFormatUtils.decimalFormatFourDigits),
                            CurrencyFormatUtils.formatDoubleValue(totalListPrice, CurrencyFormatUtils.decimalFormatFourDigits),
                            CurrencyFormatUtils.formatDoubleValue(blendedDiscount, CurrencyFormatUtils.decimalFormatFourDigits),
                            CurrencyFormatUtils.formatDoubleValue(dealerNet, CurrencyFormatUtils.decimalFormatFourDigits),
                            CurrencyFormatUtils.formatDoubleValue(margin, CurrencyFormatUtils.decimalFormatFourDigits),
                            aopRate,
                            manufacturingCostUSD,
                            warrantyCost, surchargeCost, dutyCost, totalCostWithoutFreight, totalCostWithFreight, fileUUID, plant
                    );
            imMarginAnalystSummary.setType(type);
            if(durationUnit.equals("monthly")) {
                imMarginAnalystSummary.setDurationUnit(durationUnit);
                // monthly valued
                imMarginAnalystSummary.setFullMonthlyRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setMarginPercentMonthlyRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
            }
            else {
                imMarginAnalystSummary.setDurationUnit(durationUnit);
                // annually valued
                imMarginAnalystSummary.setFullCostAopRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setMarginPercentAopRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
            }

            imMarginAnalystSummaryRepository.save(imMarginAnalystSummary);
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
    public List<IMMarginAnalystData> getIMMarginAnalystData(String modelCode, String strCurrency, String fileUUID, String orderNumber, Integer type) {

        Calendar monthYear = Calendar.getInstance();
        monthYear.set(2023, Calendar.SEPTEMBER, 1);

        // check the plant of the modelCode
        // if we cannot find any plant with the modelCode --> then return nothing
        Optional<BookingOrder> optionalBookingOrder = bookingOrderService.getDistinctBookingOrderByModelCode(modelCode);
        if(optionalBookingOrder.isPresent()) {
            String plant = optionalBookingOrder.get().getProductDimension().getPlant();
            log.info("Model Code: " + modelCode + " -> Plant: " + plant);
            log.info("Order Number: " + orderNumber);

            if(!plant.equals("HYM") && !plant.equals("SN") && !plant.equals("Ruyi") && !plant.equals("Maximal") && !plant.equals("Staxx")) {
                log.info("Getting US Plant data ...");
                return imMarginAnalystDataRepository.getUSPlantIMMarginAnalystData(modelCode, orderNumber, strCurrency, type);
            }
            else {
                return imMarginAnalystDataRepository.getIMMarginAnalystData(modelCode, strCurrency, fileUUID, type);
            }
        }
        else
            return new ArrayList<>();
    }

    public Map<String, Object> getIMMarginAnalystSummary(String modelCode, String strCurrency, String fileUUID, String orderNumber, Integer type) {

        List<IMMarginAnalystSummary> imMarginAnalystSummaryList = new ArrayList<>();
        List<String> nonUSPlantList = new ArrayList<>(List.of("HYM", "Ruyi", "Staxx", "Maximal", "SN"));

        Optional<BookingOrder> optionalBookingOrder = bookingOrderService.getDistinctBookingOrderByModelCode(modelCode);
        if(optionalBookingOrder.isPresent()) {
            String plant = optionalBookingOrder.get().getProductDimension().getPlant();
            log.info("Model Code: " + modelCode + " -> Plant: " + plant);

            // if modelCode is US Plant
            if(!nonUSPlantList.contains(plant)) {
                log.info("Getting US plant summary ...");
                imMarginAnalystSummaryList = getUSPlantMarginSummary(modelCode, strCurrency, orderNumber, type);
                if(imMarginAnalystSummaryList.isEmpty()) {
                    log.info("Calculate new summary ...");

                    calculateUSPlantMarginSummary(modelCode, strCurrency, "annually", orderNumber, type);
                    calculateUSPlantMarginSummary(modelCode, strCurrency, "monthly", orderNumber, type);

                    imMarginAnalystSummaryList = getUSPlantMarginSummary(modelCode, strCurrency, orderNumber, type);
                }
            }
            else {
                // get MarginAnalystData from uploaded file (non-US plant)
                imMarginAnalystSummaryList = imMarginAnalystSummaryRepository.getIMMarginAnalystSummary(modelCode, strCurrency, fileUUID, type);
            }
        }

        log.info("Number of summary " + imMarginAnalystSummaryList.size());

        IMMarginAnalystSummary monthly = new IMMarginAnalystSummary();
        IMMarginAnalystSummary annually = new IMMarginAnalystSummary();
        for(IMMarginAnalystSummary summary : imMarginAnalystSummaryList) {
            if(summary.getDurationUnit().equals("monthly"))
                monthly = summary;
            else
                annually = summary;
        }

        return Map.of(
                "MarginAnalystSummaryMonthly", monthly,
                "MarginAnalystSummaryAnnually", annually
        );
    }

    List<IMMarginAnalystSummary> getUSPlantMarginSummary(String modelCode, String strCurrency, String orderNumber, int type) {
        List<IMMarginAnalystSummary> imMarginAnalystSummaryList = new ArrayList<>();
        log.info(modelCode + " " + orderNumber + " " + strCurrency);
        Optional<IMMarginAnalystSummary> monthly = imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryMonthly(modelCode, strCurrency, orderNumber, type);
        Optional<IMMarginAnalystSummary> annually = imMarginAnalystSummaryRepository.getIMMarginAnalystSummaryAnnually(modelCode, strCurrency, orderNumber, type);

        if(monthly.isPresent() && annually.isPresent())
            imMarginAnalystSummaryList.addAll(List.of(monthly.get(), annually.get()));

        return imMarginAnalystSummaryList;
    }

    /**
     * Calculate IMMarginAnalystData of US plant by querying Part from BookingOrder and Part
     */
    public void calculateUSPlantMarginData(String strCurrency, String orderNumber, String fileUUID) throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("upload_files.base-folder");
        String fileName = fileUploadService.getFileNameByUUID(fileUUID); // fileName has been hashed

        FileInputStream is = new FileInputStream(baseFolder + "/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        Sheet sheet = workbook.getSheetAt(0);
        List<IMMarginAnalystData> imMarginAnalystDataList = new ArrayList<>();

        // Initialize variables for assigning later in for loop
        Calendar monthYear = Calendar.getInstance();

        double manufacturingCost;
        String plant;
        Optional<BookingOrder> bookingOrder = bookingOrderService.getBookingOrderByOrderNumber(orderNumber);
        if(bookingOrder.isPresent())
        {
            manufacturingCost = bookingOrder.get().getTotalCost();
            plant = bookingOrder.get().getProductDimension().getPlant();
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Number not found: " + orderNumber);

        for(Row row : sheet) {
            if(row.getRowNum() == 0) {
                getColumnName(row);
            } else if (!row.getCell(COLUMN_NAME.get("Part Number"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                String strDate = row.getCell(COLUMN_NAME.get("Order Booked Date")).getStringCellValue();
                if(!strDate.isEmpty())
                    monthYear = parseMonthYear(strDate);

                String modelCode = row.getCell(COLUMN_NAME.get("Model Code")).getStringCellValue();
                String partNumber = row.getCell(COLUMN_NAME.get("Part Number")).getStringCellValue();
                double listPrice = row.getCell(COLUMN_NAME.get("List Price")).getNumericCellValue();
                double netPrice = row.getCell(COLUMN_NAME.get("Net Price Each")).getNumericCellValue();
                String partDescription = row.getCell(COLUMN_NAME.get("Part Description"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                int type = (int) row.getCell(COLUMN_NAME.get("#")).getNumericCellValue();

                double manufacturingCostWithSPED = manufacturingCost;
                boolean isSPED = false;
                if(partDescription.contains("SPED"))
                {
                    isSPED = true;
                    manufacturingCostWithSPED += 0.9 * netPrice;
                }

                // Assigning value for imMarginAnalystData
                IMMarginAnalystData imMarginAnalystData = new IMMarginAnalystData(
                                plant, modelCode, partNumber, partDescription,
                                CurrencyFormatUtils.formatDoubleValue(listPrice, CurrencyFormatUtils.decimalFormatFourDigits),
                                monthYear, strCurrency,
                                CurrencyFormatUtils.formatDoubleValue(netPrice, CurrencyFormatUtils.decimalFormatFourDigits)
                        );
                imMarginAnalystData.setOrderNumber(orderNumber);
                imMarginAnalystData.setManufacturingCost(CurrencyFormatUtils.formatDoubleValue(manufacturingCostWithSPED, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystData.setFileUUID(fileUUID);
                imMarginAnalystData.setSPED(isSPED);
                imMarginAnalystData.setType(type);
                imMarginAnalystDataList.add(imMarginAnalystData);
            }
        }
        log.info("IMMarginAnalystData saved: " + imMarginAnalystDataList.size());
        imMarginAnalystDataRepository.saveAll(imMarginAnalystDataList);
        imMarginAnalystDataList.clear();
    }
    public void calculateUSPlantMarginSummary(String modelCode, String strCurrency, String durationUnit, String orderNumber, Integer type) {
        double defMFGCost = 0;
        Calendar monthYear = Calendar.getInstance();
        Optional<BookingOrder> optionalBookingOrder = bookingOrderService.getBookingOrderByOrderNumber(orderNumber);
        if(optionalBookingOrder.isPresent())
        {
            defMFGCost = optionalBookingOrder.get().getTotalCost();
            monthYear = optionalBookingOrder.get().getDate();
        }

        // Initialize values
        double costUplift = 0.0;
        double warranty = 0.0;
        double surcharge = 0.015;
        double duty = 0.0;
        double freight = 0.0;
        boolean liIonIncluded = false; // NO

        // ExchangeRate from strCurrency to USD
        monthYear.set(monthYear.get(Calendar.YEAR), monthYear.get(Calendar.MONTH), 1);
        log.info("Exchange from: " + strCurrency + " to: USD " + " of " + monthYear.getTime());
        double aopRate = exchangeRateService.getExchangeRate(strCurrency, "USD", monthYear).getRate();     // considered as the ExchangeRate

        double totalListPrice = 0.0;
        double dealerNet = 0.0;
        double totalManufacturingCost = defMFGCost;

        // calculate sum of the listPrice, costRMB and dealerNet
        List<IMMarginAnalystData> imMarginAnalystDataList = imMarginAnalystDataRepository.getUSPlantIMMarginAnalystData(modelCode, orderNumber, strCurrency, type);
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

        double warrantyCost = manufacturingCostUSD * warranty;
        double surchargeCost = manufacturingCostUSD * surcharge;
        double dutyCost = manufacturingCostUSD * duty;
        double totalCostWithoutFreight = manufacturingCostUSD + warrantyCost + surchargeCost + dutyCost;
        double totalCostWithFreight = 0;

        if(strCurrency.equals("AUD")) {
            fullCostAOPRate = totalCost + freight;       // AUD only
            totalCostWithFreight = totalCostWithoutFreight + freight * aopRate;
        }

        double margin = dealerNet - fullCostAOPRate;

        // check whether dealerNet == 0 or not
        double marginPercentAopRate = (dealerNet == 0) ? 0 : (margin / dealerNet);

        IMMarginAnalystSummary imMarginAnalystSummary = new IMMarginAnalystSummary
                (
                        modelCode, strCurrency,
                        CurrencyFormatUtils.formatDoubleValue(totalManufacturingCost, CurrencyFormatUtils.decimalFormatFourDigits),
                        costUplift, warranty, surcharge, duty, freight, liIonIncluded,
                        CurrencyFormatUtils.formatDoubleValue(totalCost, CurrencyFormatUtils.decimalFormatFourDigits),
                        CurrencyFormatUtils.formatDoubleValue(totalListPrice, CurrencyFormatUtils.decimalFormatFourDigits),
                        CurrencyFormatUtils.formatDoubleValue(blendedDiscount, CurrencyFormatUtils.decimalFormatFourDigits),
                        CurrencyFormatUtils.formatDoubleValue(dealerNet, CurrencyFormatUtils.decimalFormatFourDigits),
                        CurrencyFormatUtils.formatDoubleValue(margin, CurrencyFormatUtils.decimalFormatFourDigits),
                        aopRate,
                        manufacturingCostUSD,
                        warrantyCost, surchargeCost, dutyCost, totalCostWithoutFreight, totalCostWithFreight, null, null
                );

        imMarginAnalystSummary.setOrderNumber(orderNumber);
        imMarginAnalystSummary.setType(type);

        if(durationUnit.equals("monthly")) {
            imMarginAnalystSummary.setDurationUnit(durationUnit);
            // monthly valued
            imMarginAnalystSummary.setFullMonthlyRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
            imMarginAnalystSummary.setMarginPercentMonthlyRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
        }
        else {
            imMarginAnalystSummary.setDurationUnit(durationUnit);
            // annually valued
            imMarginAnalystSummary.setFullCostAopRate(CurrencyFormatUtils.formatDoubleValue(fullCostAOPRate, CurrencyFormatUtils.decimalFormatFourDigits));
            imMarginAnalystSummary.setMarginPercentAopRate(CurrencyFormatUtils.formatDoubleValue(marginPercentAopRate, CurrencyFormatUtils.decimalFormatFourDigits));
        }
        log.info("summary's data: " + imMarginAnalystSummary.getOrderNumber() + " " + imMarginAnalystSummary.getModelCode() + " " + imMarginAnalystSummary.getCurrency());
        imMarginAnalystSummaryRepository.save(imMarginAnalystSummary);
    }
}
