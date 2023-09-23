package com.hysteryale.service;

import com.hysteryale.model.Currency;
import com.hysteryale.repository.CurrencyRepository;
import com.hysteryale.utils.FileUtils;
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
public class CurrencyService {
    @Resource
    CurrencyRepository currencyRepository;

    public void importCurrencies(String folderPath) throws FileNotFoundException {

        log.info("========= Start importing Currencies ==========");

        // Try to find if there is file in this folder
        List<String> files = FileUtils.getAllFilesInFolder(folderPath);

        //if there is a file, then use it to extract currency, all files in this folder can be used to do that because they have the same currencies
        if (files.size() > 0) {

            List<Currency> currencyList = new ArrayList<>();

            log.info("=== Use file " + files.get(0) + "to import currencies");

            InputStream is = new FileInputStream(folderPath + "/" + files.get(0));

            Workbook workbook = StreamingReader
                    .builder()              //setting Buffer
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(is);

            int numberOfSheets = workbook.getNumberOfSheets();

            for (int i = 0 ; i < numberOfSheets; i++){
                Sheet sheet = workbook.getSheetAt(i);
                //have a list of all available currencies and we check if sheetname is currency code, then get it
                if(sheet.getSheetName())
            }

            // Get sheet contains Currencies table and get row contains Currencies
            Sheet sheet = workbook.getSheet("Summary AOP");

            for (Row row : sheet) {
                if (!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() == 3) {
                    for (Cell cell : row) {
                        if (!cell.getStringCellValue().isEmpty()) {
                            String currencyName = cell.getStringCellValue();

                            if (currencyRepository.getCurrenciesByName(currencyName.toUpperCase()).isEmpty()) {
                                Currency newCurrency = new Currency();
                                newCurrency.setCurrency(currencyName.toUpperCase());

                                currencyList.add(newCurrency);
                            }
                        }
                    }
                }
            }

            currencyRepository.saveAll(currencyList);

            log.info("Import Currencies Completed");
        }

    }

    public Currency getCurrenciesByName(String currencyName) {
        Optional<Currency> optionalCurrencies = currencyRepository.getCurrenciesByName(currencyName);
        if(optionalCurrencies.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No currencies found with " + currencyName);
        return optionalCurrencies.get();
    }
}
