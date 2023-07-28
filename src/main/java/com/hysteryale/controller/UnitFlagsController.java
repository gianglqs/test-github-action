package com.hysteryale.controller;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.service.UnitFlagsService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UnitFlagsController {
    @Autowired
    UnitFlagsService unitFlagsService;

    /** List of Unit Flags in order to save to DB*/
    private List<UnitFlags> unitFlagsList = new ArrayList<>();
    /** Initial the number of Excel file's rows can be saveAll() at one time*/
    private static final Integer NUM_OF_ROWS = 100;

    /**
     * Create new Unit Flags according to Excel file
     * @param row a data row in Excel file
     * @return new Unit Flags object
     */
    public UnitFlags mapExcelRowToUnitFlags(XSSFRow row) {
        return new UnitFlags(
                row.getCell(0).getStringCellValue(),        //Unit
                row.getCell(1).getStringCellValue(),        //Description
                row.getCell(2).getStringCellValue(),        //Class
                row.getCell(3).getStringCellValue(),        //Ready for Distribution
                row.getCell(4).getStringCellValue(),        //Enable GL Readiness
                row.getCell(5).getStringCellValue(),        //Fully Attributed
                row.getCell(6).getStringCellValue(),        //Ready for Parts Costing
                new SimpleDateFormat("MM/dd/YYYY hh:mm:s a").format(row.getCell(7).getDateCellValue()), //Created Date
                row.getCell(8).getStringCellValue()         //Cancelled
        );
    }

    @GetMapping(path = "/unitFlags")
    public List<UnitFlags> getAllUnitFlags() {
        return unitFlagsService.getAllUnitFlags();
    }

    /**
     * Import all rows in the Excel files
     * Notes: not checking same value
     * @throws IOException
     */
    @PostMapping(path = "/unitFlags/import")
    public void mapDataExcelToDB() throws IOException {
        //Mock data files with local Excel file
        File file = new File("src/main/resources/masterdata/UnitFlags.xlsx");
        InputStream inputStream = new FileInputStream(file);

        //TODO: should import Excel data file from outside
        //MultipartFile excelDataFile = new MockMultipartFile("ExcelData", inputStream);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workbook.getSheetAt(0);

        // init start iterator
        int i = 2;
        //clear current unitFlags components
        unitFlagsList.clear();

        while (workSheet.getRow(i) != null){
            XSSFRow row = workSheet.getRow(i);
            UnitFlags tempUnitFlags = mapExcelRowToUnitFlags(row);
            unitFlagsList.add(tempUnitFlags);

            if(unitFlagsList.size() > NUM_OF_ROWS){
                unitFlagsService.addListOfUnitFlags(unitFlagsList);
                unitFlagsList.clear();
            }
            i++;
        }
        unitFlagsService.addListOfUnitFlags(unitFlagsList);
    }

    @PostMapping(path = "/unitFlags/saveChanges")
    @Transactional
    public void saveUnitFlagsChanges() throws IOException {
        //Mock data files with local excel file
        File file = new File("src/main/resources/masterdata/MockUnitFlags.xlsx");
        InputStream inputStream = new FileInputStream(file);

        //TODO: should import Excel data file from outside
        //MultipartFile excelDataFile = new MockMultipartFile("ExcelData", inputStream);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workbook.getSheetAt(0);

        // init iterator
        int i = 2;
        // clear current unitFlags components
        unitFlagsList.clear();

        while (workSheet.getRow(i) != null) {
            XSSFRow row = workSheet.getRow(i);

            UnitFlags tempUnitFlags = mapExcelRowToUnitFlags(row);

            /* Get Unit Flags with the unit in Excel:
              if present() -> check if there is any change -> changed -> save these changes to DB
              if empty() -> add new to DB
             */
            Optional<UnitFlags> optionalUnitFlags = unitFlagsService.getUnitFlagsByUnit(tempUnitFlags.getUnit());
            if(optionalUnitFlags.isPresent()){
                if(!optionalUnitFlags.get().equals(tempUnitFlags)){
                    unitFlagsService.saveUnitFlagsChanges(optionalUnitFlags.get(), tempUnitFlags);
                }
            }
            else {
                unitFlagsList.add(tempUnitFlags);
                System.out.println(unitFlagsList.size());
                if(unitFlagsList.size() > NUM_OF_ROWS){
                    unitFlagsService.addListOfUnitFlags(unitFlagsList);
                    unitFlagsList.clear();
                }
            }
            //increase iterator
            i++;
        }
        System.out.println(unitFlagsList.size());
        unitFlagsService.addListOfUnitFlags(unitFlagsList);
    }
}
