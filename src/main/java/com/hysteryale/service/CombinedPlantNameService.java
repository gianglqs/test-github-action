package com.hysteryale.service;

import com.hysteryale.model.CombinedPlantName;
import com.hysteryale.repository.CombinedPlantNameRepository;
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
public class CombinedPlantNameService {
    @Autowired
    CombinedPlantNameRepository combinedPlantNameRepository;
    private static final HashMap<String, Integer> COMBINED_PLANT_COLUMNS_NAME = new HashMap<>();

    private void getColumnsName(Row row) {
        for(int i = 0; i < 2; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            COMBINED_PLANT_COLUMNS_NAME.put(columnName, i);
        }
        log.info("CombinedPlantName Columns:" + COMBINED_PLANT_COLUMNS_NAME);
    }

    private CombinedPlantName mapExcelToCombinedPlant(Row row) {
        return new CombinedPlantName(
                (int) row.getCell(COMBINED_PLANT_COLUMNS_NAME.get("Combined Plant Number")).getNumericCellValue(),
                row.getCell(COMBINED_PLANT_COLUMNS_NAME.get("Plant name")).getStringCellValue()
        );
    }

    public void importCombinedPlantName() throws FileNotFoundException {
        InputStream is = new FileInputStream("importdata/masterdata/CombinedPlantName.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);
        Sheet sheet = workbook.getSheetAt(0);
        List<CombinedPlantName> combinedPlantNameList = new ArrayList<>();

        for(Row row : sheet) {
            if(row.getRowNum() == 0) {
                getColumnsName(row);
            }
            else if(row.getRowNum() > 0 && !Objects.equals(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(), "")) {
                CombinedPlantName combinedPlantName = mapExcelToCombinedPlant(row);
                combinedPlantNameList.add(combinedPlantName);
            }
        }
        combinedPlantNameRepository.saveAll(combinedPlantNameList);
        log.info(String.valueOf(combinedPlantNameList.size()));
    }
    public List<CombinedPlantName> getAllCombinedPlantName() {
        return combinedPlantNameRepository.findAll();
    }
}
