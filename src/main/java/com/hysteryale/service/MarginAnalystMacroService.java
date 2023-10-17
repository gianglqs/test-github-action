package com.hysteryale.service;

import com.hysteryale.model.Currency;
import com.hysteryale.model.marginAnalyst.MarginAnalystMacro;
import com.hysteryale.repository.MarginAnalystMacroRepository;
import com.hysteryale.utils.CurrencyFormatUtils;
import com.hysteryale.utils.DateUtils;
import com.hysteryale.utils.EnvironmentUtils;
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

    static HashMap<String, Integer> MACRO_COLUMNS = new HashMap<>();

    private void getMacroColumns(Row row) {
        for(int i = 1; i <= 11; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            MACRO_COLUMNS.put(columnName, i);
        }
        log.info("Macro Columns: " + MACRO_COLUMNS);
    }

    private MarginAnalystMacro mapExcelDataToMarginAnalystMacro(Row row, String strCurrency, Calendar monthYear) {
        MarginAnalystMacro marginAnalystMacro = new MarginAnalystMacro();

        // String values
        marginAnalystMacro.setPlant(row.getCell(MACRO_COLUMNS.get("Plant")).getStringCellValue());
        marginAnalystMacro.setSeriesCode(row.getCell(MACRO_COLUMNS.get("Series Code")).getStringCellValue());
        marginAnalystMacro.setPriceListRegion(row.getCell(MACRO_COLUMNS.get("Price List Region")).getStringCellValue());
        marginAnalystMacro.setClazz(row.getCell(MACRO_COLUMNS.get("Class")).getStringCellValue());
        marginAnalystMacro.setModelCode(row.getCell(MACRO_COLUMNS.get("Model Code")).getStringCellValue());
        marginAnalystMacro.setPartNumber(row.getCell(MACRO_COLUMNS.get("Option Code")).getStringCellValue());
        marginAnalystMacro.setMonthYear(monthYear);

        try {
            marginAnalystMacro.setStdOpt(row.getCell(MACRO_COLUMNS.get("STD/OPT"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
        } catch (Exception e) {
            log.error(e.getMessage());
            marginAnalystMacro.setStdOpt("");
        }
        marginAnalystMacro.setDescription(row.getCell(MACRO_COLUMNS.get("Description"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());

        // Set currency
        Currency currency = currencyService.getCurrenciesByName(strCurrency);
        marginAnalystMacro.setCurrency(currency);

        // Numeric values
        double costRMB = CurrencyFormatUtils.formatDoubleValue(row.getCell(MACRO_COLUMNS.get("Add on Cost RMB")).getNumericCellValue(), CurrencyFormatUtils.decimalFormatFourDigits);

        marginAnalystMacro.setCostRMB(costRMB);

        return marginAnalystMacro;
    }

    public void importMarginAnalystMacro() throws IOException {
        String homePath = EnvironmentUtils.getEnvironmentValue("import-files.home-path");
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = EnvironmentUtils.getEnvironmentValue("import-files.margin_analyst_data");
        String fileName = "/Copy of USD AUD Margin Analysis Template Macro_Aug 1st.xlsx";

        log.info("Path: " + homePath + baseFolder + folderPath + fileName);

        InputStream is = new FileInputStream(homePath + baseFolder + folderPath + fileName);
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

        for(String macroSheet : macroSheets) {
            String currency = macroSheet.equals("USD HYM Ruyi Staxx") ? "USD" : "AUD";

            Sheet sheet = workbook.getSheet(macroSheet);

            List<MarginAnalystMacro> marginAnalystMacroList = new ArrayList<>();
            for(Row row : sheet) {
                if(row.getRowNum() == 0)
                    getMacroColumns(row);
                else if(!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                    MarginAnalystMacro marginAnalystMacro = mapExcelDataToMarginAnalystMacro(row, currency, monthYear);
                    marginAnalystMacroList.add(marginAnalystMacro);
                }
            }

            log.info("Newly save MarginAnalystMacro: " + marginAnalystMacroList.size());
            marginAnalystMacroRepository.saveAll(marginAnalystMacroList);
            marginAnalystMacroList.clear();
        }
    }

    public Optional<MarginAnalystMacro> getMarginAnalystMacro(String modelCode, String partNumber, String strCurrency, Calendar monthYear) {
        return marginAnalystMacroRepository.getMarginAnalystMacro(modelCode, partNumber, strCurrency, monthYear);
    }
}
