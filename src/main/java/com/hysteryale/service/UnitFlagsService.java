package com.hysteryale.service;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.repository.UnitFlagsRepository;
import com.monitorjbl.xlsx.StreamingReader;
import javassist.NotFoundException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class UnitFlagsService {
    @Autowired
    private UnitFlagsRepository unitFlagsRepository;

    /** Initial the number of Excel file's rows can be saveAll() at one time*/
    private static final Integer NUM_OF_ROWS = 100;
    private HashMap<String, Integer> columns = new HashMap<>();

    public UnitFlagsService(UnitFlagsRepository unitFlagsRepository) {
        this.unitFlagsRepository = unitFlagsRepository;
    }

    /**
     * Mapping columns' name in Unit Flags to HashMap with KEY : "column_name" and VALUE : "column index"
     * @param row
     */
    void getUnitFlagsColumnsIndex(Row row){
        for(int i = 0; i < 9; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            columns.put(columnName, i);
        }
    }


    /**
     * Create new Unit Flags according to Excel file using HashMap to indicate cell index
     * @param row a data row in Excel file
     * @return new Unit Flags object
     */
    public UnitFlags mapExcelRowToUnitFlags(Row row) throws ParseException {
        // Format the value in startDate and endDate in Excel file
        DataFormatter df = new DataFormatter();
        String strCreatedDate = df.formatCellValue(row.getCell(columns.get("Created Date"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));

        // convert into sql.Date type
        Timestamp createdDate = null;
        if(strCreatedDate != ""){
            createdDate = new Timestamp(new SimpleDateFormat("MM/dd/yyyy hh:mm:s a").parse(strCreatedDate).getTime());
        }

        return new UnitFlags(
                row.getCell(columns.get("Unit")).getStringCellValue(),                          //Unit
                row.getCell(columns.get("Description")).getStringCellValue(),                   //Description
                row.getCell(columns.get("Class")).getStringCellValue(),                         //Class
                row.getCell(columns.get("Ready for Distribution")).getStringCellValue(),        //Ready for Distribution
                row.getCell(columns.get("Enable GL Readiness")).getStringCellValue(),           //Enable GL Readiness
                row.getCell(columns.get("Fully Attributed")).getStringCellValue(),              //Fully Attributed
                row.getCell(columns.get("Ready for Parts Costing")).getStringCellValue(),       //Ready for Parts Costing
                createdDate,                                                                    //Created Date
                row.getCell(columns.get("Cancelled")).getStringCellValue()                      //Cancelled
        );
    }

    /**
     * Import all rows in the Excel files
     * @throws IOException
     */
    public void mapDataExcelToDB() throws IOException, ParseException {
        InputStream is = new FileInputStream(new File("importdata/masterdata/UnitFlags.xlsx"));
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        List<UnitFlags> unitFlagsList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0); // Unit Flags

        for(Row row : sheet) {
            if(row.getRowNum() == 1){
                getUnitFlagsColumnsIndex(row);
            }
            if(row.getRowNum() > 1 &&
                    !row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                UnitFlags unitFlags = mapExcelRowToUnitFlags(row);
                unitFlagsList.add(unitFlags);

                if(unitFlagsList.size() > NUM_OF_ROWS) {
                    addListOfUnitFlags(unitFlagsList);
                    unitFlagsList.clear();
                }
            }
        }
        addListOfUnitFlags(unitFlagsList);
    }

    public void saveUnitFlagsChanges() throws IOException, ParseException {
        // Mock data files with local excel file
        File file = new File("importdata/masterdata/MockUnitFlags.xlsx");
        InputStream inputStream = new FileInputStream(file);

        //TODO: should import Excel data file from outside
        // MultipartFile excelDataFile = new MockMultipartFile("ExcelData", inputStream);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workbook.getSheetAt(0);

        // get Excel file columns
        // Row 1 : columns' name
        getUnitFlagsColumnsIndex(workSheet.getRow(1));
        System.out.println(Arrays.asList(columns));
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
            // increase iterator
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
    public List<UnitFlags> getUnitFlagsByReadyState(String readyState) {
        return unitFlagsRepository.getUnitFlagsByReadyState(readyState);
    }
}
