package com.hysteryale.service;

import com.hysteryale.model.CountryCode;
import com.hysteryale.repository.CountryCodeRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CountryCodeService {
    @Autowired
    CountryCodeRepository countryCodeRepository;

    private static final HashMap<String, Integer> COUNTRY_CODE_COLUMNS_NAME = new HashMap<>();

    private void getColumnsName(Row row) {
        for(int i = 0; i < 4; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            COUNTRY_CODE_COLUMNS_NAME.put(columnName, i);
        }
        log.info("CountryCode Columns:" + COUNTRY_CODE_COLUMNS_NAME);
    }
    private CountryCode mapExcelToCountryCode(Row row) {
        return new CountryCode(
                row.getCell(COUNTRY_CODE_COLUMNS_NAME.get("Country Code")).getStringCellValue(),
                row.getCell(COUNTRY_CODE_COLUMNS_NAME.get("Country")).getStringCellValue(),
                row.getCell(COUNTRY_CODE_COLUMNS_NAME.get("Region"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(COUNTRY_CODE_COLUMNS_NAME.get("remarks"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()
        );
    }

    public void importCountryCode() throws FileNotFoundException {
        InputStream is = new FileInputStream("importdata/masterdata/CountryCode.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);
        Sheet sheet = workbook.getSheetAt(0);
        List<CountryCode> countryCodeList = new ArrayList<>();

        for(Row row : sheet) {
            if(row.getRowNum() == 0) {
                getColumnsName(row);
            }
            else if(row.getRowNum() > 0 && !Objects.equals(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(), "")) {
                CountryCode countryCode = mapExcelToCountryCode(row);
                countryCodeList.add(countryCode);
            }
        }
        countryCodeRepository.saveAll(countryCodeList);
        log.info(String.valueOf(countryCodeList.size()));
    }
}
