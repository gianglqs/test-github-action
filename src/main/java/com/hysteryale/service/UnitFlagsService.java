package com.hysteryale.service;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.repository.UnitFlagsRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@Transactional
public class UnitFlagsService {
    @Autowired
    UnitFlagsRepository unitFlagsRepository;

    /** Initial the number of Excel file's rows can be saveAll() at one time*/
    private final HashMap<String, Integer> columns = new HashMap<>();

    /**
     * Mapping columns' name in Unit Flags to HashMap with KEY : "column_name" and VALUE : "column index"
     * @param row
     */
    void getUnitFlagsColumnsIndex(Row row){
        for(int i = 0; i < 9; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            columns.put(columnName, i);
        }
        log.info( "Columns HasMap: " + columns);
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

        // Parsing String into GregorianCalendar
        GregorianCalendar createdDate = new GregorianCalendar();
        if(!strCreatedDate.isEmpty()){
            createdDate.setTime(new SimpleDateFormat("MM/dd/yyyy hh:mm:s a").parse(strCreatedDate));
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
        InputStream is = new FileInputStream("importdata/masterdata/UnitFlags.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        List<UnitFlags> saveList = new ArrayList<>();
        // Get sheet of UnitFlags
        Sheet sheet = workbook.getSheetAt(0);

        for(Row row : sheet) {
            // Get the index according to Excel columns' name
            if(row.getRowNum() == 1){
                getUnitFlagsColumnsIndex(row);
            }
            // Map the rows' value into UnitFlags object -> add the saveList for saveAll()
            if(row.getRowNum() > 1 &&
                    !row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                UnitFlags unitFlags = mapExcelRowToUnitFlags(row);
                saveList.add(unitFlags);
            }
        }
        // Save all UnitFlags in the List
        addListOfUnitFlags(saveList);
        log.info("New UnitFlags added: " + saveList.size());
    }

    /**
     * Save UnitFlags if there is any changes and add new UnitFlags if it is not existed
     * @throws IOException
     * @throws ParseException
     */
    public void importUnitFlagsChanges() throws IOException, ParseException {
        // Tracking parameter
        int numOfRowsChanged = 0;

        // Mock data files with local Excel file
        InputStream is = new FileInputStream("importdata/masterdata/MockUnitFlags.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        List<UnitFlags> saveList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0); // Unit Flags

        for(Row row : sheet) {
            // Get the index according to Excel columns' name
            if(row.getRowNum() == 1)
                getUnitFlagsColumnsIndex(row);
            if(row.getRowNum() > 1 &&
                    !row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                UnitFlags excelUnitFlags = mapExcelRowToUnitFlags(row);
                Optional<UnitFlags> optionalUnitFlags = getUnitFlagsByUnit(excelUnitFlags.getUnit());

                /*
                    If dbUnitFlags is presented -> check 2 UnitFlags are whether equal or not -> save changes
                    else add new UnitFlags to DB
                 */
                if(optionalUnitFlags.isPresent()) {
                    UnitFlags dbUnitFlags = optionalUnitFlags.get();
                    if(!isUnitFlagsEqual(dbUnitFlags, excelUnitFlags))
                    {
                        saveUnitFlagsChanges(dbUnitFlags, excelUnitFlags);
                        numOfRowsChanged++;
                    }
                }
                else {
                    // Add new UnitFlags to List
                    saveList.add(excelUnitFlags);
                }
            }
        }
        addListOfUnitFlags(saveList);
        log.info("New UnitFlags added: " + saveList.size());
        log.info("Number of UnitFlags changed: " + numOfRowsChanged);
    }
    boolean isUnitFlagsEqual(UnitFlags a, UnitFlags b) {
        return a.getUnit().equals(b.getUnit()) &&
                a.getDescription().equals(b.getDescription()) &&
                a.getUClass().equals(b.getUClass()) &&
                a.getReadyForDistribution().equals(b.getReadyForDistribution()) &&
                a.getEnableGLReadiness().equals(b.getEnableGLReadiness()) &&
                a.getFullyAttributed().equals(b.getFullyAttributed()) &&
                a.getReadyForPartsCosting().equals(b.getReadyForPartsCosting()) &&
                a.getCreatedDate().equals(b.getCreatedDate()) &&
                a.getCancelled().equals(b.getCancelled());
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
    public void saveUnitFlagsChanges(UnitFlags dbUnitFlags, UnitFlags excelUnitFlags) {
        //setting all changes
        dbUnitFlags.setUnit(excelUnitFlags.getUnit());
        dbUnitFlags.setDescription(excelUnitFlags.getDescription());
        dbUnitFlags.setUClass(excelUnitFlags.getUClass());
        dbUnitFlags.setReadyForDistribution(excelUnitFlags.getReadyForDistribution());
        dbUnitFlags.setEnableGLReadiness(excelUnitFlags.getEnableGLReadiness());
        dbUnitFlags.setFullyAttributed(excelUnitFlags.getFullyAttributed());
        dbUnitFlags.setReadyForPartsCosting(excelUnitFlags.getReadyForPartsCosting());
        dbUnitFlags.setCreatedDate(excelUnitFlags.getCreatedDate());
        dbUnitFlags.setCancelled(excelUnitFlags.getCancelled());
    }
    public List<UnitFlags> getUnitFlagsByReadyState(String readyState) {
        return unitFlagsRepository.getUnitFlagsByReadyState(readyState);
    }
}
