package com.hysteryale.service;

import com.hysteryale.model.TM1ProductRange;
import com.hysteryale.repository.TM1ProductRangeRepository;
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
public class TM1ProductRangeService {
    @Autowired
    TM1ProductRangeRepository tm1ProductRangeRepository;
    private static final HashMap<String, Integer> PRODUCT_RANGE_COLUMNS_NAME = new HashMap<>();

    private void getColumnsName(Row row) {
        for(int i = 0; i < 38; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            PRODUCT_RANGE_COLUMNS_NAME.put(columnName, i);
        }
        log.info("TM1ProductRange Columns:" + PRODUCT_RANGE_COLUMNS_NAME);
    }
    private TM1ProductRange mapExcelToTm1Product(Row row) {
        return new TM1ProductRange(
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Metaseries"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Model"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Family_Att"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Class_wBT"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Brand"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Description"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Family_Name"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Plant"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Europe_Plant"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Plant_AP"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Total_All_Classes"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Truck_Type"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Class_totals"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("vTtlbyPL"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("vClass_total2"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("vClass_total3"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("vEUseries"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("General_Series"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Hyster_Series"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Yale_Series"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("BW_Model_AP_AUST"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("BW_Model_AP_ASIA"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("BW_Model_AP_PACIFIC"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("AP_Model"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Segment_Family"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Segment"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Segment_Consolidation"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Eng_Fam_Lvl2_Descr"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Eng_Fam_Lvl2"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                (int) row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Eng_Fam_Lvl1"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Eng_Consolidation"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Notes"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                (int) row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("#"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue(),
                (int) row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Countif"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Metaseries w/o a"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Seg Fam Position"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Seg Fam Position Execption"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                row.getCell(PRODUCT_RANGE_COLUMNS_NAME.get("Seg Fam Position Final"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()
        );
    }
    public void importProductRange() throws FileNotFoundException {
        InputStream is = new FileInputStream("importdata/masterdata/TM1ProductRange.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);
        Sheet sheet = workbook.getSheetAt(0);
        List<TM1ProductRange> tm1ProductRangeList = new ArrayList<>();

        for(Row row : sheet) {
            if(row.getRowNum() == 0) {
                getColumnsName(row);
            }
            else if(row.getRowNum() > 4 && !Objects.equals(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(), "")) {
                TM1ProductRange tm1ProductRange = mapExcelToTm1Product(row);
                tm1ProductRangeList.add(tm1ProductRange);
            }
        }
        tm1ProductRangeRepository.saveAll(tm1ProductRangeList);
        log.info(String.valueOf(tm1ProductRangeList.size()));
    }
}
