package com.hysteryale.service;

import com.hysteryale.model.SAPCustomer;
import com.hysteryale.repository.SAPCustomerRepository;
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
public class SAPCustomerService {
    @Autowired
    SAPCustomerRepository sapCustomerRepository;
    private static final HashMap<String, Integer> CUSTOMER_COLUMNS_NAME = new HashMap<>();

    private void getColumnsName(Row row) {
        for(int i = 0; i < 7; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            CUSTOMER_COLUMNS_NAME.put(columnName, i);
        }
        log.info("SAPCustomer Columns:" + CUSTOMER_COLUMNS_NAME);
    }

    private SAPCustomer mapExcelToSAPCustomer(Row row) {
        return new SAPCustomer(
                row.getCell(CUSTOMER_COLUMNS_NAME.get("KNA1 Customer Data.Customer")).getStringCellValue(),
                row.getCell(CUSTOMER_COLUMNS_NAME.get("KNA1 Customer Data.Country")).getStringCellValue(),
                row.getCell(CUSTOMER_COLUMNS_NAME.get("KNA1 Customer Data.Name 1")).getStringCellValue(),
                row.getCell(CUSTOMER_COLUMNS_NAME.get("Legacy System ID"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                (int) row.getCell(CUSTOMER_COLUMNS_NAME.get("Legacy Source Company Code"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue(),
                (int) row.getCell(CUSTOMER_COLUMNS_NAME.get("Legacy field value"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue(),
                (int) row.getCell(CUSTOMER_COLUMNS_NAME.get("SAP field value"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue()
        );
    }

    public void importSAPCustomer() throws FileNotFoundException {
        InputStream is = new FileInputStream("importdata/masterdata/SAPCustomerMasterlist.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);
        Sheet sheet = workbook.getSheetAt(0);
        List<SAPCustomer> sapCustomerList = new ArrayList<>();

        for(Row row : sheet) {
            if(row.getRowNum() == 0) {
                getColumnsName(row);
            }
            else if(row.getRowNum() > 0 && !Objects.equals(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(), "")) {
                SAPCustomer sapCustomer = mapExcelToSAPCustomer(row);
                sapCustomerList.add(sapCustomer);
            }
        }
        sapCustomerRepository.saveAll(sapCustomerList);
        log.info(String.valueOf(sapCustomerList.size()));
    }
}
