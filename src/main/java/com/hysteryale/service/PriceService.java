package com.hysteryale.service;

import com.hysteryale.model.Price;
import com.hysteryale.repository.PriceRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class PriceService {
    @Resource
    PriceRepository priceRepository;
    /** Define number of Excel file's rows can be saved at one time */
    private final HashMap<String, Integer> columns = new HashMap<>();

    /**
     * Mapping columns' name in Price Book file into HashMap with KEY: "column_name" and VALUE: "column index"
     * @param row
     */
    private void getPriceBookColumnsIndex(Row row){
        for(int i = 0; i < 12; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            columns.put(columnName, i);
        }
        log.info("Price HashMap: " + columns);
    }

    /**
     * Map each Excel rows into Price using HashMap to indicate cell index
     * @param row Excel file's row
     * @return new Price object
     */
    private Price mapExcelDataToPrice(Row row) throws ParseException {
        // Format the value in startDate and endDate in Excel file
        DataFormatter df = new DataFormatter();
        String strStartDate = df.formatCellValue(row.getCell(columns.get("startDate"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String strEndDate = df.formatCellValue(row.getCell(columns.get("endDate"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));

        // convert into sql.Date type
        Date startDate = null;
        Date endDate = null;

        //TODO: need to consider to change this parse Date
        if(!strStartDate.isEmpty() && !strEndDate.isEmpty()){
            startDate = new Date(new SimpleDateFormat("MM/dd/yy").parse(strStartDate).getTime());
            endDate = new Date(new SimpleDateFormat("MM/dd/yy").parse(strEndDate).getTime());

            // change endDate from 99 -> 1999
            //                to 99 -> 2099
            if(endDate.getYear() < startDate.getYear())
            {
                if(endDate.getYear() + 100 > 2099)
                    endDate = new Date(2099, 12,31);
                else
                    endDate.setYear(endDate.getYear() + 100);
            }
        }
        return new Price(
                row.getCell(columns.get("_update_action"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),          // update action
                row.getCell(columns.get("partNumber"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),              // partNumber
                row.getCell(columns.get("customerType"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),            // customerType
                row.getCell(columns.get("brand"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),                   // brand
                row.getCell(columns.get("series"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),                  // series
                row.getCell(columns.get("modelTruck"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),              // modelTruck
                row.getCell(columns.get("currency"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),                // currency
                row.getCell(columns.get("price"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue(),                  // price
                row.getCell(columns.get("soldAlonePrice"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue(),         // soldAlonePrice
                startDate,
                endDate,
                row.getCell(columns.get("standard"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()                 // standard
        );
    }

    public void importPriceBook() throws IOException, ParseException {

        InputStream is = new FileInputStream("importdata/masterdata/PriceBook.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        List<Price> priceList = new ArrayList<>();

        for(int i = 0; i < 2; i++){
            Sheet sheet = workbook.getSheetAt(i);
            for(Row row : sheet) {
                //get the columns name to HashMap
                if(row.getRowNum() == 1){
                    getPriceBookColumnsIndex(row);
                }
                else if(row.getRowNum() > 4 &&
                        !row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {

                    // Map to Price object and store in *priceList for saveAll()
                    Price price = mapExcelDataToPrice(row);
                    priceList.add(price);
                }
            }
        }
        // save all Price (s) in the List
        addListOfPrices(priceList);
        log.info("New Price added: " + String.valueOf(priceList.size()));
    }
//    public void importPriceChanges() throws ParseException, FileNotFoundException {
//        // Tracking parameter
//        int numOfRowsChanged = 0;
//
//        InputStream is = new FileInputStream(new File("importdata/masterdata/MockPriceBook.xlsx"));
//        Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is);
//
//        List<Price> saveList = new ArrayList<>();
//
//        for(int i = 0; i < 2; i++) {
//            Sheet sheet = workbook.getSheetAt(i); // sheet 0: USD Price, sheet 1 AUD Price
//            for(Row row : sheet) {
//                // get the columns' name into HashMap
//                if(row.getRowNum() == 1) {
//                    getPriceBookColumnsIndex(row);
//                    System.out.println(Arrays.asList(columns));
//                }
//                if(row.getRowNum() > 4 && !row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
//                    Price excelPrice = mapExcelDataToPrice(row);
//                    Optional<Price> optionalPrice = getSinglePrice(excelPrice.getCurrency(), excelPrice.getPartNumber(), excelPrice.getSeries());
//
//                    if(optionalPrice.isPresent()) {
//                        Price dbPrice = optionalPrice.get();
//                        if(!isPriceEqual(dbPrice, excelPrice)){
//                            setChanges(dbPrice, excelPrice);
//                            numOfRowsChanged++;
//                        }
//                    }
//                    else {
//                        saveList.add(excelPrice);
//                        if(saveList.size() > NUM_OF_ROWS) {
//                            addListOfPrices(saveList);
//                            saveList.clear();
//                        }
//                    }
//                }
//            }
//            // save the remaining in the list
//            addListOfPrices(saveList);
//            saveList.clear();
//        }
//        System.out.println(numOfRowsChanged);
//    }

    /**
     * Checking two Price objects field by field are whether equal or not
     * @param a
     * @param b
     * @return
     */
    public boolean isPriceEqual(Price a, Price b){
        return a.getUpdateAction().equals(b.getUpdateAction()) &&
                a.getCustomerType().equals(b.getCustomerType()) &&
                a.getPartNumber().equals(b.getCustomerType()) &&
                a.getBrand().equals(b.getBrand()) &&
                a.getPrice().equals(b.getPrice()) &&
                a.getModelTruck().equals(b.getModelTruck()) &&
                a.getCurrency().equals(b.getCurrency()) &&
                a.getSeries().equals(b.getSeries()) &&
                a.getSoldAlonePrice().equals(b.getSoldAlonePrice()) &&
                a.getStartDate().equals(b.getStartDate()) &&
                a.getEndDate().equals(b.getEndDate()) &&
                a.getStandard().equals(b.getStandard());
    }

    public List<Price> getAllPrices(){
        return priceRepository.findAll();
    }

    public void addListOfPrices(List<Price> priceList){
        priceRepository.saveAll(priceList);
    }

    public List<Price> getPricesBySeries(String seriesNum){
        return priceRepository.getPricesListBySeries(seriesNum);
    }
    @Transactional
    public void setChanges(Price dbPrice, Price excelPrice) {
        dbPrice.setUpdateAction(excelPrice.getUpdateAction());
        dbPrice.setCustomerType(excelPrice.getCustomerType());
        dbPrice.setPartNumber(excelPrice.getPartNumber());
        dbPrice.setBrand(excelPrice.getBrand());
        dbPrice.setPrice(excelPrice.getPrice());
        dbPrice.setModelTruck(excelPrice.getModelTruck());
        dbPrice.setCurrency(excelPrice.getCurrency());
        dbPrice.setSeries(excelPrice.getSeries());
        dbPrice.setSoldAlonePrice(excelPrice.getSoldAlonePrice());
        dbPrice.setStartDate(excelPrice.getStartDate());
        dbPrice.setEndDate(excelPrice.getEndDate());
        dbPrice.setStandard(excelPrice.getStandard());
    }
}
