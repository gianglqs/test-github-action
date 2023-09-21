package com.hysteryale.service;

import com.hysteryale.model.Currency;
import com.hysteryale.model.ExchangeRate;
import com.hysteryale.repository.ExchangeRateRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
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
public class ExchangeRateService {
    @Resource
    ExchangeRateRepository exchangeRateRepository;
    @Resource
    CurrenciesService currenciesService;
    public static Map<Integer, String> fromCurrenciesTitle = new HashMap<>();
    public static Map<String, Integer> monthMap = new HashMap<>();

    public void initMonthMap() {
        monthMap.put("JAN", 0);
        monthMap.put("FEB", 1);
        monthMap.put("MAR", 2);
        monthMap.put("APR", 3);
        monthMap.put("MAY", 4);
        monthMap.put("JUN", 5);
        monthMap.put("JUL", 6);
        monthMap.put("AUG", 7);
        monthMap.put("SEP", 8);
        monthMap.put("OCT", 9);
        monthMap.put("NOV", 10);
        monthMap.put("DEC", 11);
    }


    public Map<Integer, String> getFromCurrencyTitle(Row row) {
        int end = 31;
        for(int i = 1; i <= end; i++) {
            String currency = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
            fromCurrenciesTitle.put(i, currency.toUpperCase());
        }
        log.info("Currencies: " + fromCurrenciesTitle);
        return fromCurrenciesTitle;
    }

    /**
     * Get List of Currencies rates based on a toCurrency
     */
    public List<ExchangeRate> mapExcelDataToExchangeRate(Row row, Calendar date) {
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        String strToCurrency = row.getCell(0).getStringCellValue().toUpperCase();

        //special cases of currency name
        switch (strToCurrency.strip()) {
            case "NORWEGIAN KRONER":
                strToCurrency = "NORWAY KRONER";
                break;
            case "BRAZILIAN":
                strToCurrency = "BRAZILIAN REAL";
                break;
            case "SINGAPORE DOLLAR":
                strToCurrency = "SING. DOLLAR";
                break;
            case "N.Z.DOLLAR":
                strToCurrency = "N.Z. DOLLAR";
                break;
        }
        Currency toCurrency = currenciesService.getCurrenciesByName(strToCurrency.toUpperCase());


        for(Cell cell : row) {
            if(!cell.getStringCellValue().isEmpty()) {
                ExchangeRate exchangeRate = new ExchangeRate();

                if(cell.getColumnIndex() > 0) {
                    double rate = cell.getNumericCellValue();

                    Currency fromCurrency = currenciesService.getCurrenciesByName(fromCurrenciesTitle.get(cell.getColumnIndex()));

                    exchangeRate.setFrom(fromCurrency);
                    exchangeRate.setTo(toCurrency);
                    exchangeRate.setRate(rate);
                    exchangeRate.setDate(date);

                    log.info("from: " + fromCurrency.getCurrency() + " to: " + toCurrency.getCurrency());

                    if(exchangeRateRepository.getExchangeRateByFromToCurrencyAndDate(fromCurrency.getId(), toCurrency.getId(), date).isEmpty())
                        exchangeRateList.add(exchangeRate);
                }
            }
        }return exchangeRateList;
    }

    public void importExchangeRate() throws FileNotFoundException {
        // Initialize folder path and file name
        String folderPath = "import_files/currency_exchangerate";
        String fileName = "EXCSEP2023.xlsx";

        //Pattern for getting date from fileName
        Pattern pattern = Pattern.compile("^\\w{3}(\\w{3})(\\d{4}).");
        Matcher matcher = pattern.matcher(fileName);

        // Assign date get from fileName
        Calendar date = new GregorianCalendar();
        if(matcher.find())
        {
            initMonthMap();
            String month = matcher.group(1);
            int year = Integer.parseInt(matcher.group(2));

            date.set(year, monthMap.get(month), 1);
        }

        InputStream is = new FileInputStream(folderPath + "/" + fileName);
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        Sheet sheet = workbook.getSheet("Summary AOP");
        List<ExchangeRate> exchangeRatesList = new ArrayList<>();

        for (Row row : sheet) {
            if(row.getRowNum() == 3)
                fromCurrenciesTitle = getFromCurrencyTitle(row);

            if(!row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()
                && !row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {

                exchangeRatesList.addAll(mapExcelDataToExchangeRate(row, date));

            }
        }

        exchangeRateRepository.saveAll(exchangeRatesList);
        log.info("ExchangeRate are newly saved or updated: " + exchangeRatesList.size());
        exchangeRatesList.clear();
    }
}
