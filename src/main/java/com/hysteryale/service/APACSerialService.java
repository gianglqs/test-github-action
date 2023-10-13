package com.hysteryale.service;

import com.hysteryale.model.APACSerial;
import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.MetaSeries;
import com.hysteryale.repository.APACSerialRepository;
import com.hysteryale.repository.CustomAPACSerialRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;


@Service
@Slf4j
public class APACSerialService {
    @Resource
    APACSerialRepository apacSerialRepository;
    @Resource
    MetaSeriesService metaSeriesService;

    //private final HashMap<String, Integer> APAC_COLUMNS = new HashMap<>();

//    public void getAPACColumnsName(Row row, HashMap<String, Integer> APAC_COLUMNS) {
//
//        for (int i = 0; i < 11; i++) {
//            String columnName = row.getCell(i).getStringCellValue();
//            APAC_COLUMNS.put(columnName, i);
//        }
//        log.info("APAC Columns: " + APAC_COLUMNS);
//    }

    public void getAPACColumnsName(Row row, HashMap<String, Integer> APAC_COLUMNS) {
        for (int i = 0; i < 11; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            if (APAC_COLUMNS.get(columnName) != null)
                columnName += "_Yale";
            APAC_COLUMNS.put(columnName, i);
        }
        log.info("APAC Columns: " + APAC_COLUMNS);
    }

    public APACSerial mapExcelToAPACSerial(Row row, HashMap<String, Integer> APAC_COLUMNS, String brand) throws IllegalAccessException {
        APACSerial apacSerial = new APACSerial();
        Class<? extends APACSerial> apacSerialClass = apacSerial.getClass();
        Field[] fields = apacSerialClass.getDeclaredFields();

        for (Field field : fields) {
            // String key of column's name and capitalize first character
            String fieldName = field.getName();
            String hashMapKey = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            if (fieldName.equals("id"))
                continue;
            field.setAccessible(true);
            if (fieldName.equals("brand")) {
                field.set(apacSerial, brand);
            } else if (fieldName.equals("series")) {
                Cell seriesCell = row.getCell(APAC_COLUMNS.get(brand));
                String series = "";
                if (seriesCell.getCellType() == CellType.STRING) {
                    series = seriesCell.getStringCellValue();
                } else if (seriesCell.getCellType() == CellType.NUMERIC) {
                    series = String.valueOf(seriesCell.getNumericCellValue());
                }
                field.set(apacSerial, series);

            } else {
                // Suffix for specify Model and Quote reference of either Hyster or Yale
                String suffix = brand.equals("Hyster") ? "" : ("_" + "Yale");
                switch (hashMapKey) {
                    case "Line":
                        hashMapKey = hashMapKey + (brand.equals("Hyster") ? " " : (" _" + "Yale"));
                        break;
                    case "Model":
                        hashMapKey = hashMapKey + suffix;
                        break;
                    case "QuoteReference":
                        hashMapKey = hashMapKey.substring(0, 5) + hashMapKey.substring(5, 6).toLowerCase() + hashMapKey.substring(6);
                        hashMapKey = hashMapKey.substring(0, 5) + " " + hashMapKey.substring(5) + suffix;
                        break;
                    case "Clazz":
                        hashMapKey = "Class";
                        break;
                }
                Cell cell = row.getCell(APAC_COLUMNS.get(hashMapKey), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cell.getCellType() == CellType.NUMERIC) {
                    field.set(apacSerial, String.valueOf(cell.getNumericCellValue()));
                } else if (cell.getCellType() == CellType.STRING) {
                    field.set(apacSerial, cell.getStringCellValue());
                }
//
//                String value = row.getCell(APAC_COLUMNS.get(hashMapKey), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
//                if (!value.equals("NA"))
//                    field.set(apacSerial, value);

            }
        }
        return apacSerial;
    }

    public APACSerial mapExcelSheetToAPACSerial(Row row, HashMap<String, Integer> APAC_COLUMNS, String brand) throws IllegalAccessException {
        APACSerial apacSerial = new APACSerial();
        Class<? extends APACSerial> apacSerialClass = apacSerial.getClass();
        Field[] fields = apacSerialClass.getDeclaredFields();

        for (Field field : fields) {
            // String key of column's name and capitalize first character
            String fieldName = field.getName();
            String hashMapKey = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            if (fieldName.equals("id"))
                continue;

            field.setAccessible(true);
            if (fieldName.equals("brand"))
                field.set(apacSerial, brand);
            else if (fieldName.equals("series")) {
                field.set(apacSerial, row.getCell(APAC_COLUMNS.get("Series")).getStringCellValue());
            } else if (fieldName.equals("quoteReference")) {
                Cell quoteCell = row.getCell(APAC_COLUMNS.get("Quote #"));
                field.set(apacSerial, quoteCell.getStringCellValue());
            } else if (fieldName.equals("model")) {
                Cell quoteCell = row.getCell(APAC_COLUMNS.get("Model"));
                field.set(apacSerial, quoteCell.getStringCellValue());
            } else {
                // Suffix for specify Model and Quote reference of either Hyster or Yale

                Cell cell = row.getCell(APAC_COLUMNS.get(hashMapKey), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cell.getCellType() == CellType.NUMERIC) {
                    field.set(apacSerial, String.valueOf(cell.getNumericCellValue()));
                } else if (cell.getCellType() == CellType.STRING) {
                    field.set(apacSerial, cell.getStringCellValue());
                }
            }
        }
        return apacSerial;
    }


    public void importAPACSerial() throws IOException, IllegalAccessException {
        InputStream is = new FileInputStream("import_files/APAC/APAC Serial in NOVO master file.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        String[] brands = {"Hyster", "Yale"};

        List<APACSerial> apacSerialList = new ArrayList<>();

        Sheet orderSheet = workbook.getSheet("Master Summary");
        HashMap<String, Integer> columnSheet = new HashMap<>();

        for (Row row : orderSheet) {
            if (row.getRowNum() == 0)
                getAPACColumnsName(row, columnSheet);
            else if (row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() != CellType.BLANK
                    && row.getRowNum() > 0) {
                for (String brand : brands) {
                    APACSerial apacSerial = mapExcelToAPACSerial(row, columnSheet, brand);
                     if ((apacSerial.getModel() != null) && !isExist(apacSerial))
                        apacSerialRepository.save(apacSerial);
                }
            }
        }

        for (String brand : brands) {

            Sheet brandSheet = workbook.getSheet(brand + " Model");
            log.info(brandSheet.getSheetName());
            columnSheet = new HashMap<>();
            for (Row row : brandSheet) {
                if (row.getRowNum() == 0)
                    getAPACColumnsName(row, columnSheet);
                else if (row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() != CellType.BLANK
                        && row.getRowNum() > 0) {
                    APACSerial apacSerial = mapExcelSheetToAPACSerial(row, columnSheet, brand);
                    if ((apacSerial.getModel() != null) && !isExist(apacSerial))
                        apacSerialRepository.save(apacSerial);
                }
            }
        }
        //    apacSerialRepository.saveAll(apacSerialList);

     //   log.info("Newly saved " + apacSerialList.size());

        apacSerialList.clear();
    }

    private boolean isExist(APACSerial apacSerial) {
        Optional<APACSerial> optionalAPACSerial = apacSerialRepository.findByModelAndSeries(apacSerial.getModel(), apacSerial.getSeries());
        if (optionalAPACSerial.isPresent()) {
            return true;
        }
        return false;
    }

    public APACSerial getAPACSerialByModel(String model) {
        Optional<APACSerial> optionalAPACSerial = apacSerialRepository.findByModel(model);
        if (optionalAPACSerial.isPresent())
            return optionalAPACSerial.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "APAC Serial not found with " + model);
    }

    /**
     * Get List of APAC Serial's Model for selecting filter
     */
    public List<Map<String, String>> getAllAPACSerialModels() {
        List<Map<String, String>> modelsMap = new ArrayList<>();
        List<String> models = apacSerialRepository.getModels();

        for (String m : models) {
            Map<String, String> mMap = new HashMap<>();
            mMap.put("value", m);

            modelsMap.add(mMap);
        }

        return modelsMap;
    }

    /**
     * Get list of distinct Plants
     */
    public List<Map<String, String>> getAllPlants() {
        List<Map<String, String>> plantListMap = new ArrayList<>();
        List<String> plants = apacSerialRepository.getPlants();

        for (String p : plants) {
            Map<String, String> pMap = new HashMap<>();
            pMap.put("value", p);

            plantListMap.add(pMap);
        }

        return plantListMap;
    }
}
