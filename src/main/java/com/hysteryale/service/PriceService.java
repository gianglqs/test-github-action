package com.hysteryale.service;

import com.hysteryale.model.Price;
import com.hysteryale.repository.PriceRepository;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;
    /** Define number of Excel file's rows can be saved at one time */
    private static final Integer NUM_OF_ROWS = 1000;
    private HashMap<String, Integer> columns = new HashMap<>();
    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * Mapping columns' name in Price Book file into HashMap with KEY: "column_name" and VALUE: "column index"
     * @param row
     */
    private void getPriceBookColumnsIndex(Row row){
        for(int i = 0; i < 12; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            columns.put(columnName, i);
        }
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

        if(strStartDate != "" && strEndDate != ""){
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

        InputStream is = new FileInputStream(new File("importdata/masterdata/MockPriceBook.xlsx"));
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
                    System.out.println(Arrays.asList(columns));
                }
                else if(row.getRowNum() > 4 &&
                        !row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {

                    // map to Price object and store in *priceList
                    Price price = mapExcelDataToPrice(row);
                    priceList.add(price);
                    // if *priceList stores 1000 objects -> saveAll()
                    if(priceList.size() > NUM_OF_ROWS) {
                        addListOfPrices(priceList);
                        priceList.clear();  // clear the List
                    }
                }
            }
            // save the remaining Price in the List
            addListOfPrices(priceList);
        }
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
}
