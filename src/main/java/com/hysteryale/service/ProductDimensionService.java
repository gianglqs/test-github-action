package com.hysteryale.service;

import com.hysteryale.model.ProductDimension;
import com.hysteryale.model.ProductDimension;
import com.hysteryale.repository.ProductDimensionRepository;
import com.hysteryale.repository.ProductDimensionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;


@Service
@Slf4j
public class ProductDimensionService {
    @Resource
    ProductDimensionRepository productDimensionRepository;

    private final HashMap<String, Integer> APAC_COLUMNS = new HashMap<>();

    public void getAPACColumnsName(Row row) {
        for (int i = 0; i < 35; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            APAC_COLUMNS.put(columnName, i);
        }
        log.info("APAC Columns: " + APAC_COLUMNS);
    }

    public ProductDimension mapExcelSheetToProductDimension(Row row) throws IllegalAccessException {
        ProductDimension productDimension = new ProductDimension();
        Class<? extends ProductDimension> ProductDimensionClass = productDimension.getClass();
        Field[] fields = ProductDimensionClass.getDeclaredFields();

        for (Field field : fields) {
            // String key of column's name and capitalize first character
            String fieldName = field.getName();
            String hashMapKey = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            if (fieldName.equals("id"))
                continue;

            field.setAccessible(true);
            if (fieldName.equals("brand")) {
                field.set(productDimension, row.getCell(APAC_COLUMNS.get("Brand")).getStringCellValue());
            } else if (fieldName.equals("metaSeries")) {
                field.set(productDimension, row.getCell(APAC_COLUMNS.get("Metaseries")).getStringCellValue());
            } else if (fieldName.equals("model")) {
                field.set(productDimension, row.getCell(APAC_COLUMNS.get("Model")).getStringCellValue());
            } else if (fieldName.equals("plant")) {
                field.set(productDimension, row.getCell(APAC_COLUMNS.get("Plant")).getStringCellValue());
            } else if (fieldName.equals("clazz")) {
                field.set(productDimension, row.getCell(APAC_COLUMNS.get("Class_wBT")).getStringCellValue());
            } else if (fieldName.equals("segment")) {
                field.set(productDimension, row.getCell(APAC_COLUMNS.get("Segment")).getStringCellValue());
            }
        }
        return productDimension;
    }


    public void importProductDimension() throws IOException, IllegalAccessException {
        InputStream is = new FileInputStream("import_files/APAC/Product Fcst dimension 2023_02_24.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        List<ProductDimension> ProductDimensionList = new ArrayList<>();

        Sheet orderSheet = workbook.getSheet("Data");

        for (Row row : orderSheet) {
            if (row.getRowNum() == 1)
                getAPACColumnsName(row);
            else if (row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() != CellType.BLANK
                    && row.getRowNum() >= 2) {

                ProductDimension newProductDimension = mapExcelSheetToProductDimension(row);
                Optional<ProductDimension> getProductDimensionFromDB = productDimensionRepository.findByMetaSeries(newProductDimension.getMetaSeries());
                if (getProductDimensionFromDB.isPresent()) {
                    ProductDimension productDimension = getProductDimensionFromDB.get();
                    productDimension.setModel(newProductDimension.getModel());
                    productDimensionRepository.save(productDimension);
                } else {
                    productDimensionRepository.save(newProductDimension);
                }
            }

            // ProductDimensionRepository.saveAll(ProductDimensionList);

            log.info("Newly saved " + ProductDimensionList.size());

            ProductDimensionList.clear();
        }
    }

    /**
     * Get List of APAC Serial's Model for selecting filter
     */
    public List<Map<String, String>> getAllMetaSeries() {
        List<Map<String, String>> metaSeriesMap = new ArrayList<>();
        List<String> metaSeries = productDimensionRepository.getAllMetaSeries();

        for (String m : metaSeries) {
            Map<String, String> mMap = new HashMap<>();
            mMap.put("value", m);
            metaSeriesMap.add(mMap);
        }

        return metaSeriesMap;
    }

    /**
     * Get list of distinct Plants
     */
    public List<Map<String, String>> getAllPlants() {
        List<Map<String, String>> plantListMap = new ArrayList<>();
        List<String> plants = productDimensionRepository.getPlants();

        for (String p : plants) {
            Map<String, String> pMap = new HashMap<>();
            pMap.put("value", p);

            plantListMap.add(pMap);
        }

        return plantListMap;
    }

    public ProductDimension getProductDimensionByMetaseries(String series) {
        Optional<ProductDimension> productDimensionOptional = productDimensionRepository.findByMetaSeries(series.substring(1));
        return productDimensionOptional.orElse(null);
    }


    public List<Map<String, String>> getAllClasses() {
        List<Map<String, String>> classMap = new ArrayList<>();
        List<String> classes = productDimensionRepository.getAllClass();

        for (String m : classes) {
            Map<String, String> mMap = new HashMap<>();
            mMap.put("value", m);
            classMap.add(mMap);
        }

        return classMap;
    }

    public List<Map<String, String>> getAllSegments() {
        List<Map<String, String>> segmentMap = new ArrayList<>();
        List<String> segments = productDimensionRepository.getAllClass();

        for (String m : segments) {
            Map<String, String> mMap = new HashMap<>();
            mMap.put("value", m);
            segmentMap.add(mMap);
        }

        return segmentMap;
    }

}
