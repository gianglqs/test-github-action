package com.hysteryale.service;

import com.hysteryale.model.Currency;
import com.hysteryale.repository.CurrenciesRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CurrenciesService {
    @Resource
    CurrenciesRepository currenciesRepository;

    public void importCurrencies() throws FileNotFoundException {
        // Initialize folder path and file name
        String folderPath = "import_files/currency_exchangerate";
        String fileName = "EXCSEP2023 (exchange rate).xlsx";

        InputStream is = new FileInputStream(folderPath + "/" + fileName);
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        List<Currency> currencyList = new ArrayList<>();

        // Get sheet contains Currencies table and get row contains Currencies
        Sheet sheet = workbook.getSheet("Summary AOP");

        for (Row row : sheet) {
            if (!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() == 3) {
                for (Cell cell : row) {
                    if(!cell.getStringCellValue().isEmpty()) {
                        String currencyName = cell.getStringCellValue();

                        if(currenciesRepository.getCurrenciesByName(currencyName.toUpperCase()).isEmpty()) {
                            Currency newCurrency = new Currency();
                            newCurrency.setCurrency(currencyName.toUpperCase());

                            currencyList.add(newCurrency);
                        }
                    }
                }
            }
        }
        currenciesRepository.saveAll(currencyList);
        log.info("Newly saved or updated Currencies: " + currencyList.size());
        currencyList.clear();
    }

    public Currency getCurrenciesByName(String currencyName) {
        Optional<Currency> optionalCurrencies = currenciesRepository.getCurrenciesByName(currencyName);
        if(optionalCurrencies.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No currencies found with " + currencyName);
        return optionalCurrencies.get();
    }
}
