package com.hysteryale.service;

import com.hysteryale.model.APACSerial;
import com.hysteryale.model.APICDealer;
import com.hysteryale.repository.APICDealerRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class APICDealerService {
    @Resource
    APICDealerRepository apicDealerRepository;

    private final HashMap<String, Integer> APIC_DEALER_COLUMNS = new HashMap<>();

    public void getAPICDealerColumns(Row row) {
        for(int i = 0; i < 9; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            APIC_DEALER_COLUMNS.put(columnName, i);
        }
        log.info("APIC Columns: " + APIC_DEALER_COLUMNS);
    }

    public APICDealer mapExcelToAPICDealer(Row row) throws IllegalAccessException {
        APICDealer apicDealer = new APICDealer();
        Class<? extends APICDealer> apicDealerClass = apicDealer.getClass();
        Field[] fields = apicDealerClass.getDeclaredFields();

        for(Field field : fields) {
            String fieldName = field.getName();
            String hashMapKey = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            field.setAccessible(true);

            if(fieldName.equals("billtoCode")) {
                int billToCode = (int) row.getCell(APIC_DEALER_COLUMNS.get(hashMapKey)).getNumericCellValue();
                field.set(apicDealer, billToCode);
            }
            else
                field.set(apicDealer, row.getCell(APIC_DEALER_COLUMNS.get(hashMapKey)).getStringCellValue());

        }
        return apicDealer;
    }

    public void importAPICDealer() throws FileNotFoundException, IllegalAccessException {
        InputStream is = new FileInputStream("import_files/APIC/APIC Dealer Master Template (1).xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        List<APICDealer> apicDealerList = new ArrayList<>();

        Sheet orderSheet = workbook.getSheetAt(0);
        for (Row row : orderSheet) {
            if(row.getRowNum() == 0)
                getAPICDealerColumns(row);
            else if (!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()
                    && row.getRowNum() > 0) {
                APICDealer apicDealer = mapExcelToAPICDealer(row);
                apicDealerList.add(apicDealer);
            }
        }
        apicDealerRepository.saveAll(apicDealerList);
        log.info("Newly saved " + apicDealerList.size());

        apicDealerList.clear();
    }
    public APICDealer getAPICDealerByBillToCode(int billToCode) {
        Optional<APICDealer> optionalAPICDealer = apicDealerRepository.findById(billToCode);
        if(optionalAPICDealer.isPresent())
            return optionalAPICDealer.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found APIC Dealer with " + billToCode);
    }
}
