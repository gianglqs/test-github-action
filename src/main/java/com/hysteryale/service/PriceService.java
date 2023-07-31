package com.hysteryale.service;

import com.hysteryale.model.Price;
import com.hysteryale.repository.PriceRepository;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;
    /** Define number of Excel file's rows can be saved at one time */
    private static final Integer NUM_OF_ROWS = 1000;
    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * Map each Excel rows into Price
     * @param row Excel file's row
     * @return new Price object
     */
    private Price mapExcelDataToPrice(XSSFRow row) throws ParseException {
        // Format the value in startDate and endDate in Excel file
        DataFormatter df = new DataFormatter();
        String strStartDate = df.formatCellValue(row.getCell(9, Row.CREATE_NULL_AS_BLANK));
        String strEndDate = df.formatCellValue(row.getCell(10, Row.CREATE_NULL_AS_BLANK));

        // convert into sql.Date type
        Date startDate = null;
        Date endDate = null;

        if(strStartDate != "" && strEndDate != ""){
            startDate = new Date(new SimpleDateFormat("MM/dd/YYYY").parse(strStartDate).getTime());
            endDate = new Date(new SimpleDateFormat("MM/dd/YYYY").parse(strEndDate).getTime());
        }

        return new Price(
                row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // update action
                row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // partNumber
                row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // customerType
                row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // brand
                row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // series
                row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // modelTruck
                row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // currency
                row.getCell(7, Row.CREATE_NULL_AS_BLANK).getNumericCellValue(),         // price
                row.getCell(8, Row.CREATE_NULL_AS_BLANK).getNumericCellValue(),         // soldAlonePrice
                startDate,
                endDate,
                row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue()          // standard
        );
    }
    //TODO: change FileInputStream into Buffer
    // Writing UnitTest for each function
    public void importPriceBook() throws IOException, ParseException {

        File file = new File("importdata/masterdata/MockPriceBook.xlsx");
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 200);

        XSSFWorkbook workbook = new XSSFWorkbook(bufferedInputStream);

        for (int sheet = 0; sheet < 2 ; sheet++) {
            XSSFSheet workSheet = workbook.getSheetAt(sheet); /// sheet 0: USD Price, sheet 1: AUD Price

            //init iterator
            int i = 5;
            List<Price> priceList = new ArrayList<>();
            while (workSheet.getRow(i).getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue() != "") {

                XSSFRow row = workSheet.getRow(i);
                Price price = mapExcelDataToPrice(row);
                priceList.add(price);         // add into list

            /*
            If the List length is over NUM_OF_ROWS -> saveAll() then clear() the List
             */
                if(priceList.size() > NUM_OF_ROWS){
                    addListOfPrices(priceList);
                    priceList.clear();
                }
                //increase iterator
                i++;
            }
            addListOfPrices(priceList);   // save the remaining in the List
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
