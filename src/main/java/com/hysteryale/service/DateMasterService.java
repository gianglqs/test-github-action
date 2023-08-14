package com.hysteryale.service;

import com.hysteryale.model.DateMaster;
import com.hysteryale.repository.DateMasterRepository;
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

@Service
@Slf4j
public class DateMasterService {
    @Autowired
    DateMasterRepository dateMasterRepository;
    private static final HashMap<String, Integer> DATE_MASTER_COLUMNS_NAMES = new HashMap<>();

    private void getColumnsName(Row row) {
        for(int i = 0; i < 60; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            DATE_MASTER_COLUMNS_NAMES.put(columnName, i);
        }
        log.info("DateMaster Columns:" + DATE_MASTER_COLUMNS_NAMES);
    }
    private DateMaster mapExcelToDateMaster(Row row) {

        return new DateMaster(
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Date")).getDateCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Year")).getNumericCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("CurrYearOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("YearCompleted")).getBooleanCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Quarter Number")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Quarter")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Start of Quarter")).getDateCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("End of Quarter")).getDateCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Quarter & Year")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("QuarternYear")).getNumericCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("CurrQuarterOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("QuarterCompleted")).getBooleanCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Month")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Start of Month")).getDateCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("End of Month")).getDateCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Month & Year")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("MonthnYear")).getNumericCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("CurrMonthOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("MonthCompleted")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Month Name")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Month Short")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Month Initial")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Day of Month")).getNumericCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Week Number")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Start of Week")).getDateCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("End of Week")).getDateCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Week & Year")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("WeeknYear")).getNumericCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("CurrWeekOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("WeekCompleted")).getBooleanCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Day of Week Number")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Day of Week Name")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Day of Week Initial")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("DateInt")).getNumericCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("CurrDayOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsAfterToday")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsWeekDay")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsHoliday")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsBusinessDay")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Day Type")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("ISO Year")).getNumericCellValue(),
                (int)row.getCell(DATE_MASTER_COLUMNS_NAMES.get("ISO CurrYearOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("ISO Quarter")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("ISO Quarter & Year")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("ISO QuarternYear")).getNumericCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("ISO CurrQuarterOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Fiscal Year")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Fiscal CurrYearOffset")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Fiscal Quarter")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("FQuarternYear")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Fiscal Period")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("FPeriodnYear")).getStringCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("Fiscal Week")).getStringCellValue(),
                (int) row.getCell(DATE_MASTER_COLUMNS_NAMES.get("FWeeknYear")).getNumericCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsCurrentFY")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsCurrentFQ")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsCurrentFP")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsCurrentFW")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsPYTD")).getBooleanCellValue(),
                row.getCell(DATE_MASTER_COLUMNS_NAMES.get("IsPFYTD")).getBooleanCellValue()
        );
    }
    public void importDateMaster() throws FileNotFoundException {
        InputStream is = new FileInputStream("importdata/masterdata/DateMaster.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);
        Sheet sheet = workbook.getSheetAt(0);
        List<DateMaster> dateMasterList = new ArrayList<>();

        for(Row row : sheet) {
            if(row.getRowNum() == 0) {
                getColumnsName(row);
            }
            else if(row.getRowNum() > 0 && row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getDateCellValue() != null) {
                DateMaster dateMaster = mapExcelToDateMaster(row);
                dateMasterList.add(dateMaster);
            }
        }
        dateMasterRepository.saveAll(dateMasterList);
        log.info(String.valueOf(dateMasterList.size()));
    }

    public List<DateMaster> getAllDateMaster() {
        return dateMasterRepository.findAll();
    }
}
