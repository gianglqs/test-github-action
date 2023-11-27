package com.hysteryale.service.marginAnalyst;

import com.hysteryale.model.Currency;
import com.hysteryale.model.marginAnalyst.MarginAnalysisAOPRate;
import com.hysteryale.model.marginAnalyst.MarginAnalystMacro;
import com.hysteryale.repository.marginAnalyst.MarginAnalysisAOPRateRepository;
import com.hysteryale.repository.marginAnalyst.MarginAnalystMacroRepository;
import com.hysteryale.service.CurrencyService;
import com.hysteryale.utils.CurrencyFormatUtils;
import com.hysteryale.utils.DateUtils;
import com.hysteryale.utils.EnvironmentUtils;
import com.hysteryale.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MarginAnalystMacroService {
    @Resource
    MarginAnalystMacroRepository marginAnalystMacroRepository;
    @Resource
    CurrencyService currencyService;
    @Resource
    MarginAnalysisAOPRateRepository marginAnalysisAOPRateRepository;

    static HashMap<String, Integer> MACRO_COLUMNS = new HashMap<>();

    private void getMacroColumns(Row row) {
        for(int i = 1; i <= 19; i++) {
            String columnName = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
            MACRO_COLUMNS.put(columnName, i);
        }
        log.info("Macro Columns: " + MACRO_COLUMNS);
    }

    private MarginAnalystMacro mapExcelDataToMarginAnalystMacro(Row row, String strCurrency, Calendar monthYear, String plant) {
        MarginAnalystMacro marginAnalystMacro = new MarginAnalystMacro();

        double costRMB;

        // Assign values based on plant (due to 2 different format of HYM and SN sheets)
        if(plant.equals("SN")){
            marginAnalystMacro.setPlant(plant);
            marginAnalystMacro.setSeriesCode(row.getCell(MACRO_COLUMNS.get("Series")).getStringCellValue());

            marginAnalystMacro.setClazz(row.getCell(MACRO_COLUMNS.get("Class")).getStringCellValue());
            try {
                marginAnalystMacro.setModelCode(row.getCell(MACRO_COLUMNS.get("MODEL CD    (inc \"-\")")).getStringCellValue());
            } catch (Exception e) {
                marginAnalystMacro.setModelCode(row.getCell(MACRO_COLUMNS.get("MODEL CD (incl \"-\")")).getStringCellValue());
            }
            marginAnalystMacro.setPartNumber(row.getCell(MACRO_COLUMNS.get("Option Code")).getStringCellValue());
            marginAnalystMacro.setDescription(row.getCell(MACRO_COLUMNS.get("DESCRIPTION"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            marginAnalystMacro.setMonthYear(monthYear);

            costRMB = CurrencyFormatUtils.formatDoubleValue(row.getCell(MACRO_COLUMNS.get("TP USD")).getNumericCellValue(), CurrencyFormatUtils.decimalFormatFourDigits);
        }
        else {
            marginAnalystMacro.setPlant(row.getCell(MACRO_COLUMNS.get("Plant")).getStringCellValue());
            marginAnalystMacro.setSeriesCode(row.getCell(MACRO_COLUMNS.get("Series Code")).getStringCellValue());
            marginAnalystMacro.setPriceListRegion(row.getCell(MACRO_COLUMNS.get("Price List Region")).getStringCellValue());
            marginAnalystMacro.setClazz(row.getCell(MACRO_COLUMNS.get("Class")).getStringCellValue());
            marginAnalystMacro.setModelCode(row.getCell(MACRO_COLUMNS.get("Model Code")).getStringCellValue());
            marginAnalystMacro.setPartNumber(row.getCell(MACRO_COLUMNS.get("Option Code")).getStringCellValue());
            marginAnalystMacro.setDescription(row.getCell(MACRO_COLUMNS.get("Description"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            marginAnalystMacro.setMonthYear(monthYear);

            costRMB = CurrencyFormatUtils.formatDoubleValue(row.getCell(MACRO_COLUMNS.get("Add on Cost RMB")).getNumericCellValue(), CurrencyFormatUtils.decimalFormatFourDigits);
        }


        try {
            marginAnalystMacro.setStdOpt(row.getCell(MACRO_COLUMNS.get("STD/OPT"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
        } catch (Exception e) {
            log.error(e.getMessage());
            marginAnalystMacro.setStdOpt("");
        }

        // Set currency
        Currency currency = currencyService.getCurrenciesByName(strCurrency);
        marginAnalystMacro.setCurrency(currency);

        // Numeric values
        marginAnalystMacro.setCostRMB(costRMB);

        return marginAnalystMacro;
    }

    public void importMarginAnalystMacro() throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = EnvironmentUtils.getEnvironmentValue("import-files.margin_macro");

        List<String> files = FileUtils.getAllFilesInFolder(baseFolder + folderPath);
        for(String fileName : files) {

            try {
                InputStream is = new FileInputStream( baseFolder + folderPath + "/" + fileName);
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
                monthYear.set(year, DateUtils.monthMap.get(month) -1, 1);

                String[] macroSheets = {"USD HYM Ruyi Staxx", "AUD HYM Ruyi Staxx", "SN USD Asia Template", "SN USD Pacific Template", "SN AUD Template"};
                for(String macroSheet : macroSheets) {
                    log.info("Importing " + macroSheet);

                    String currency = macroSheet.contains("USD") ? "USD" : "AUD";
                    String plant  = macroSheet.contains("SN") ? "SN" : "HYM";
                    int columnNameRow = plant.equals("SN") ? 8 : 0;

                    Sheet sheet = workbook.getSheet(macroSheet);

                    List<MarginAnalystMacro> marginAnalystMacroList = new ArrayList<>();
                    for(Row row : sheet) {
                        if(row.getRowNum() == columnNameRow) {
                            log.info("Column name row: " + columnNameRow);
                            MACRO_COLUMNS.clear();
                            getMacroColumns(row);
                        }
                        else if(!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > columnNameRow) {
                            if(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                                log.info("End: " + row.getRowNum());
                            }
                            MarginAnalystMacro marginAnalystMacro = mapExcelDataToMarginAnalystMacro(row, currency, monthYear, plant);
                            marginAnalystMacroList.add(marginAnalystMacro);
                        }
                    }

                    // Save MarginAnalystMacro
                    log.info("Newly save MarginAnalystMacro: " + marginAnalystMacroList.size());
                    marginAnalystMacroRepository.saveAll(marginAnalystMacroList);
                    marginAnalystMacroList.clear();

                    // Save MarginAnalysisAOPRate
                    saveMarginAnalysisAOPRate(sheet, currency, monthYear, "monthly");
                    saveMarginAnalysisAOPRate(sheet, currency, monthYear, "annually");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Get Margin Analysis @ AOP Rate from Excel sheet: 'USD HYM Ruyi Staxx', 'SN AUD Template' and 'AUD HYM Ruyi Staxx'
     */
    void saveMarginAnalysisAOPRate(Sheet sheet, String currency, Calendar monthYear, String durationUnit) {

        if(sheet.getSheetName().equals("USD HYM Ruyi Staxx") || sheet.getSheetName().equals("SN AUD Template") ||sheet.getSheetName().equals("AUD HYM Ruyi Staxx")) {
            MarginAnalysisAOPRate marginAnalysisAOPRate = new MarginAnalysisAOPRate();
            String plant = sheet.getSheetName().contains("SN") ? "SN" : "HYM";

            double aopRate;
            double costUplift;
            double addWarranty;
            double surcharge;
            double duty;
            double freight;

            int cellIndex;
            int rowIndex;

            if (sheet.getSheetName().equals("SN AUD Template")) {
                cellIndex = durationUnit.equals("annually") ? 31 : 34;
                rowIndex = 1;

            }
            else {
                cellIndex = durationUnit.equals("annually") ? 21 : 24;
                rowIndex = 0;

            }
            aopRate = sheet.getRow(rowIndex).getCell(cellIndex).getNumericCellValue();
            costUplift = sheet.getRow(rowIndex + 2).getCell(cellIndex).getNumericCellValue();
            surcharge = sheet.getRow(rowIndex + 4).getCell(cellIndex).getNumericCellValue();
            try {
                addWarranty = sheet.getRow(rowIndex + 3).getCell(cellIndex).getNumericCellValue();
            }
            catch (Exception e) {
                addWarranty = 0;
            }
            try {
                duty = sheet.getRow(rowIndex + 5).getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
            } catch (Exception e) {
                duty = 0;
            }
            try {
                freight = sheet.getRow(rowIndex + 6).getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
            } catch (Exception e) {
                freight = 0;
            }

            marginAnalysisAOPRate.setMonthYear(monthYear);
            marginAnalysisAOPRate.setPlant(plant);

            marginAnalysisAOPRate.setAopRate(aopRate);
            marginAnalysisAOPRate.setCostUplift(costUplift);
            marginAnalysisAOPRate.setAddWarranty(addWarranty);
            marginAnalysisAOPRate.setSurcharge(surcharge);
            marginAnalysisAOPRate.setDuty(duty);
            marginAnalysisAOPRate.setFreight(freight);
            marginAnalysisAOPRate.setCurrency(currencyService.getCurrenciesByName(currency));
            marginAnalysisAOPRate.setDurationUnit(durationUnit);

            if(marginAnalysisAOPRateRepository.getMarginAnalysisAOPRate(plant, currency, monthYear, durationUnit).isEmpty())
                marginAnalysisAOPRateRepository.save(marginAnalysisAOPRate);
        }

    }

    public Optional<MarginAnalystMacro> getMarginAnalystMacroByMonthYear(String modelCode, String partNumber, String strCurrency, Calendar monthYear) {
        return marginAnalystMacroRepository.getMarginAnalystMacroByMonthYear(modelCode, partNumber, strCurrency, monthYear);
    }
    public Optional<MarginAnalystMacro> getMarginAnalystMacro(String modelCode, String partNumber, String strCurrency) {
        return marginAnalystMacroRepository.getMarginAnalystMacro(modelCode, partNumber, strCurrency);
    }

    public Double getManufacturingCost(String modelCode, String partNumber, String strCurrency, List<String> plants, Calendar monthYear) {
        return marginAnalystMacroRepository.getManufacturingCost(modelCode, partNumber, strCurrency, plants, monthYear);
    }

    public List<MarginAnalystMacro> getMarginAnalystMacroByPlant(String modelCode, String partNumber, String strCurrency, String plant, Calendar monthYear) {
        return marginAnalystMacroRepository.getMarginAnalystMacroByPlant(modelCode, partNumber, strCurrency, plant, monthYear);
    }
    public List<MarginAnalystMacro> getMarginAnalystMacroByHYMPlant(String modelCode, String partNumber, String currency, Calendar monthYear) {
        return marginAnalystMacroRepository.getMarginAnalystMacroByHYMPlant(modelCode, partNumber, currency, monthYear);
    }

    public List<MarginAnalystMacro> getMarginAnalystMacroByPlantAndListPartNumber(String modelCode, List<String> partNumber, String strCurrency, String plant, Calendar monthYear) {
        return marginAnalystMacroRepository.getMarginAnalystMacroByPlantAndListPartNumber(modelCode, partNumber, strCurrency, plant, monthYear);
    }
    public List<MarginAnalystMacro> getMarginAnalystMacroByHYMPlantAndListPartNumber(String modelCode, List<String> partNumber, String currency, Calendar monthYear) {
        return marginAnalystMacroRepository.getMarginAnalystMacroByHYMPlantAndListPartNumber(modelCode, partNumber, currency, monthYear);
    }
}
