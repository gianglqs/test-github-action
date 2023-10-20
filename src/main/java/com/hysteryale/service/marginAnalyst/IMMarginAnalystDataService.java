package com.hysteryale.service.marginAnalyst;

import com.hysteryale.model.marginAnalyst.MarginAnalysisAOPRate;
import com.hysteryale.model.marginAnalyst.MarginAnalystMacro;
import com.hysteryale.model_h2.IMMarginAnalystData;
import com.hysteryale.model_h2.IMMarginAnalystSummary;
import com.hysteryale.repository.marginAnalyst.MarginAnalysisAOPRateRepository;
import com.hysteryale.repository_h2.IMMarginAnalystDataRepository;
import com.hysteryale.repository_h2.IMMarginAnalystSummaryRepository;
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
    static HashMap<String, Integer> COLUMN_NAME = new HashMap<>();

    void getColumnName(Row row) {
        for(int i = 0; i < 23; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            COLUMN_NAME.put(columnName, i);
        }
        log.info("Column Name: " + COLUMN_NAME);
    }

    private IMMarginAnalystData mapIMMarginAnalystData(Row row, String plant, String strCurrency, Calendar monthYear) {

        String modelCode = row.getCell(COLUMN_NAME.get("Model Code")).getStringCellValue();
        String partNumber = row.getCell(COLUMN_NAME.get("Part Number")).getStringCellValue();
        if(plant.equals("HYM"))
            plant = "Maximal";
        List<MarginAnalystMacro> optionalMarginAnalystMacro = marginAnalystMacroService.getMarginAnalystMacroByPlant(modelCode, partNumber, strCurrency, plant, monthYear);
        if(!optionalMarginAnalystMacro.isEmpty()) {
            log.info(modelCode + " " + partNumber + " " + strCurrency + " " + plant + " " + monthYear.getTime());
            IMMarginAnalystData imMarginAnalystData = new IMMarginAnalystData();
            MarginAnalystMacro marginAnalystMacro = optionalMarginAnalystMacro.get(0);

            imMarginAnalystData.setModelCode(modelCode);
            imMarginAnalystData.setOptionCode(partNumber);
            imMarginAnalystData.setDescription(row.getCell(COLUMN_NAME.get("Part Description"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            imMarginAnalystData.setPlant(plant);

            imMarginAnalystData.setPriceListRegion(marginAnalystMacro.getPriceListRegion());
            imMarginAnalystData.setClass_(marginAnalystMacro.getClazz());
            imMarginAnalystData.setStd_opt(marginAnalystMacro.getStdOpt());
            imMarginAnalystData.setCurrency(strCurrency);
            imMarginAnalystData.setMonthYear(monthYear);

            //missing monthYear
            //missing dealer

            double costRMB = marginAnalystMacro.getCostRMB();
            double listPrice = row.getCell(COLUMN_NAME.get("List Price")).getNumericCellValue();
            double netPrice = row.getCell(COLUMN_NAME.get("Net Price Each")).getNumericCellValue();

            // AUD = 0.2159, USD = 0.1569
            double aopRate = 0;
            double costUplift = 0.0;
            double surcharge = 0.015;
            double duty = 0.0;
            double freight = 0;
            double warranty = 0;

            Optional<MarginAnalysisAOPRate> optionalMarginAnalysisAOPRate = getMarginAnalysisAOPRate(strCurrency, monthYear, plant, "annually");
            if(optionalMarginAnalysisAOPRate.isPresent()) {
                log.info("exist");
                MarginAnalysisAOPRate marginAnalysisAOPRate = optionalMarginAnalysisAOPRate.get();
                aopRate = marginAnalysisAOPRate.getAopRate();
                costUplift = marginAnalysisAOPRate.getCostUplift();
                warranty = marginAnalysisAOPRate.getAddWarranty();
                surcharge = marginAnalysisAOPRate.getSurcharge();
                duty = marginAnalysisAOPRate.getDuty();
                freight = marginAnalysisAOPRate.getFreight();
            }
            log.info(aopRate + " " + costUplift + " " + warranty + " " + surcharge + " " + duty + " " + freight);

            double marginAOP;
            if(costRMB == 0.0) {
                marginAOP =  netPrice * 0.1;
            }
            else {
                marginAOP = (netPrice - costRMB * (1 + costUplift) * (1 + warranty + surcharge + duty) * aopRate) - freight;
            }

            // Calculated data fields
            imMarginAnalystData.setDealerNet(CurrencyFormatUtils.formatDoubleValue(netPrice, CurrencyFormatUtils.decimalFormatFourDigits));
            imMarginAnalystData.setCostRMB(CurrencyFormatUtils.formatDoubleValue(costRMB, CurrencyFormatUtils.decimalFormatFourDigits));
            imMarginAnalystData.setListPrice(CurrencyFormatUtils.formatDoubleValue(listPrice, CurrencyFormatUtils.decimalFormatFourDigits));
            imMarginAnalystData.setMargin_aop(CurrencyFormatUtils.formatDoubleValue(marginAOP, CurrencyFormatUtils.decimalFormatFourDigits));
            return imMarginAnalystData;
        }
        return null;
    }

    /**
     * Calculate MarginAnalystData and save into In-memory database
     * @param fileUUID identifier of uploaded file
     */
    public void calculateMarginAnalystData(String originalFileName, String fileUUID) throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("upload_files.base-folder");
        String fileName = marginAnalystFileUploadService.getFileNameByUUID(fileUUID); // fileName has been hashed

        log.info(originalFileName);
        Pattern pattern = Pattern.compile("(.*)_(.*)(.xlsx)");
        Matcher matcher = pattern.matcher(originalFileName);
        if(matcher.find()) {
            String plant = matcher.group(1);
            String strCurrency = matcher.group(2);
            log.info(plant + "-" + strCurrency);

            FileInputStream is = new FileInputStream(baseFolder + "/" + fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);

            Calendar monthYear = Calendar.getInstance();
            List<IMMarginAnalystData> imMarginAnalystDataList = new ArrayList<>();
            for(Row row : sheet) {
                if(row.getRowNum() == 0) {
                    getColumnName(row);
                } else if (!row.getCell(COLUMN_NAME.get("Part Number"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                    String strDate = row.getCell(COLUMN_NAME.get("Order Booked Date")).getStringCellValue();
                    if(!strDate.isEmpty()) {
                        monthYear = parseMonthYear(strDate);
                    }
                    IMMarginAnalystData imMarginAnalystData = mapIMMarginAnalystData(row, plant, strCurrency, monthYear);
                    if(imMarginAnalystData != null) {
                        imMarginAnalystData.setFileUUID(fileUUID);
                        imMarginAnalystDataList.add(imMarginAnalystData);
                    }
                }
            }
            log.info("IMMarginAnalystData saved: " + imMarginAnalystDataList.size());
            imMarginAnalystDataRepository.saveAll(imMarginAnalystDataList);
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File's name is not in appropriate format");

    }

    /**
     * Calculate MarginAnalystSummary and save into In-memory database
     */
    public void calculateMarginAnalystSummary(String fileUUID, String originalFileName, String durationUnit) {
        Pattern pattern = Pattern.compile("(.*)_(.*)(.xlsx)");
        Matcher matcher = pattern.matcher(originalFileName);
        if(matcher.find()) {
            log.info(durationUnit + " calculating...");
            String plant = matcher.group(1);
            String strCurrency = matcher.group(2);


            // forEach modelCode -> get MarginAnalystData -> then calculate MarginAnalystSummary
            List<String> modelCodeList = imMarginAnalystDataRepository.getModelCodesByFileUUID(fileUUID);
            log.info("ModelCodes: " + modelCodeList.size());
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

                double aopRate = 0;
                Optional<MarginAnalysisAOPRate> optionalMarginAnalysisAOPRate = getMarginAnalysisAOPRate(strCurrency, monthYear, plant, durationUnit);
                if(optionalMarginAnalysisAOPRate.isPresent()) {
                    MarginAnalysisAOPRate marginAnalysisAOPRate = optionalMarginAnalysisAOPRate.get();
                    log.info("exist");
                    aopRate = marginAnalysisAOPRate.getAopRate();
                    costUplift = marginAnalysisAOPRate.getCostUplift();
                    warranty = marginAnalysisAOPRate.getAddWarranty();
                    surcharge = marginAnalysisAOPRate.getSurcharge();
                    duty = marginAnalysisAOPRate.getDuty();
                    freight = marginAnalysisAOPRate.getFreight();
                }

                double totalListPrice = 0.0;
                double dealerNet = 0.0;
                double manufacturingCostRMB = 0.0;

                // calculate sum of the listPrice, costRMB and dealerNet
                for(IMMarginAnalystData data : imMarginAnalystDataList) {
                    totalListPrice += data.getListPrice();
                    manufacturingCostRMB += data.getCostRMB();
                    dealerNet += data.getDealerNet();
                }

                double totalCostRMB = manufacturingCostRMB * (1 + costUplift) * (1 + warranty + surcharge + duty);
                double blendedDiscount = 1 - (dealerNet / totalListPrice);
                double fullCostAOPRate = totalCostRMB * aopRate;
                double margin = dealerNet - fullCostAOPRate;

                // check whether dealerNet == 0 or not
                double marginPercentAopRate;
                if(dealerNet == 0)
                    marginPercentAopRate = 0;
                else marginPercentAopRate = margin / dealerNet;

                imMarginAnalystSummary.setModelCode(modelCode);
                imMarginAnalystSummary.setCurrency(strCurrency);
                imMarginAnalystSummary.setManufacturingCostRMB(CurrencyFormatUtils.formatDoubleValue(manufacturingCostRMB, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setCostUplift(costUplift);
                imMarginAnalystSummary.setAddWarranty(warranty);
                imMarginAnalystSummary.setSurcharge(surcharge);
                imMarginAnalystSummary.setDuty(duty);
                imMarginAnalystSummary.setFreight(freight);
                imMarginAnalystSummary.setLiIonIncluded(liIonIncluded);
                imMarginAnalystSummary.setTotalCostRMB(CurrencyFormatUtils.formatDoubleValue(totalCostRMB, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setTotalListPrice(CurrencyFormatUtils.formatDoubleValue(totalListPrice, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setBlendedDiscountPercentage(CurrencyFormatUtils.formatDoubleValue(blendedDiscount, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setDealerNet(CurrencyFormatUtils.formatDoubleValue(dealerNet, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setMargin(CurrencyFormatUtils.formatDoubleValue(margin, CurrencyFormatUtils.decimalFormatFourDigits));
                imMarginAnalystSummary.setMarginAopRate(aopRate);
                imMarginAnalystSummary.setFileUUID(fileUUID);


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
        return imMarginAnalystDataRepository.getIMMarginAnalystData(modelCode, strCurrency, fileUUID);
    }

    public Map<String, Object> getIMMarginAnalystSummary(String modelCode, String strCurrency, String fileUUID) {
        List<IMMarginAnalystSummary> imMarginAnalystSummaryList = imMarginAnalystSummaryRepository.getIMMarginAnalystSummary(modelCode, strCurrency, fileUUID);

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

}
