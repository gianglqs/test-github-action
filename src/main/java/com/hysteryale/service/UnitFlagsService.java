package com.hysteryale.service;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.repository.UnitFlagsRepository;
import javassist.NotFoundException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitFlagsService {
    @Autowired
    private UnitFlagsRepository unitFlagsRepository;

    /** Initial the number of Excel file's rows can be saveAll() at one time*/
    private static final Integer NUM_OF_ROWS = 100;

    public UnitFlagsService(UnitFlagsRepository unitFlagsRepository) {
        this.unitFlagsRepository = unitFlagsRepository;
    }

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

    /**
     * Import all rows in the Excel files
     * Notes: not checking same value
     * @throws IOException
     */
    public void mapDataExcelToDB() throws IOException {
        //Mock data files with local Excel file
        File file = new File("importdata/masterdata/UnitFlags.xlsx");
        InputStream inputStream = new FileInputStream(file);

        //TODO: should import Excel data file from outside
        //MultipartFile excelDataFile = new MockMultipartFile("ExcelData", inputStream);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workbook.getSheetAt(0);

        // init start iterator
        int i = 2;
        List<UnitFlags> unitFlagsList = new ArrayList<>();

        while (workSheet.getRow(i) != null){
            XSSFRow row = workSheet.getRow(i);
            UnitFlags tempUnitFlags = mapExcelRowToUnitFlags(row);
            unitFlagsList.add(tempUnitFlags);

            if(unitFlagsList.size() > NUM_OF_ROWS){
                addListOfUnitFlags(unitFlagsList);
                unitFlagsList.clear();
            }
            i++;
        }
        addListOfUnitFlags(unitFlagsList);
    }

    public void saveUnitFlagsChanges() throws IOException {
        //Mock data files with local excel file
        File file = new File("importdata/masterdata/MockUnitFlags.xlsx");
        InputStream inputStream = new FileInputStream(file);

        //TODO: should import Excel data file from outside
        //MultipartFile excelDataFile = new MockMultipartFile("ExcelData", inputStream);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workbook.getSheetAt(0);

        // init iterator
        int i = 2;
        List<UnitFlags> unitFlagsList = new ArrayList<>();

        while (workSheet.getRow(i) != null) {
            XSSFRow row = workSheet.getRow(i);

            UnitFlags tempUnitFlags = mapExcelRowToUnitFlags(row);

            /* Get Unit Flags with the unit in Excel:
              if present() -> check if there is any change -> changed -> save these changes to DB
              if empty() -> add new to DB
             */
            Optional<UnitFlags> optionalUnitFlags = getUnitFlagsByUnit(tempUnitFlags.getUnit());
            if(optionalUnitFlags.isPresent()){
                if(!optionalUnitFlags.get().equals(tempUnitFlags)){
                    saveUnitFlagsChanges(optionalUnitFlags.get(), tempUnitFlags);
                }
            }
            else {
                unitFlagsList.add(tempUnitFlags);
                System.out.println(unitFlagsList.size());
                if(unitFlagsList.size() > NUM_OF_ROWS){
                    addListOfUnitFlags(unitFlagsList);
                    unitFlagsList.clear();
                }
            }
            //increase iterator
            i++;
        }
        System.out.println(unitFlagsList.size());
        addListOfUnitFlags(unitFlagsList);
    }

    public void addUnitFlags(UnitFlags unitFlags) {
        unitFlagsRepository.save(unitFlags);
    }
    public List<UnitFlags> getAllUnitFlags() {
        return unitFlagsRepository.findAll();
    }
    public void addListOfUnitFlags(List<UnitFlags> unitFlagsList) {
        unitFlagsRepository.saveAll(unitFlagsList);
    }
    public Optional<UnitFlags> getUnitFlagsByUnit(String unit) {
        return unitFlagsRepository.findById(unit);
    }
    public void saveUnitFlagsChanges(UnitFlags dbUnitFlags, UnitFlags tempUnitFlags) {
        //setting all changes
        dbUnitFlags.setUnit(tempUnitFlags.getUnit());
        dbUnitFlags.setDescription(tempUnitFlags.getDescription());
        dbUnitFlags.setUClass(tempUnitFlags.getUClass());
        dbUnitFlags.setReadyForDistribution(tempUnitFlags.getReadyForDistribution());
        dbUnitFlags.setEnableGLReadiness(tempUnitFlags.getEnableGLReadiness());
        dbUnitFlags.setFullyAttributed(tempUnitFlags.getFullyAttributed());
        dbUnitFlags.setReadyForPartsCosting(tempUnitFlags.getReadyForPartsCosting());
        dbUnitFlags.setCreatedDate(tempUnitFlags.getCreatedDate());
        dbUnitFlags.setCancelled(tempUnitFlags.getCancelled());
    }
}
