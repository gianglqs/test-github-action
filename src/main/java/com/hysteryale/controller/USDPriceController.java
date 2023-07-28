package com.hysteryale.controller;

import com.hysteryale.model.USDPrice;
import com.hysteryale.service.USDPriceService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
public class USDPriceController {
    @Autowired
    private USDPriceService usdPriceService;
    /** Define number of Excel file's rows can be saved at one time */
    private static final Integer NUM_OF_ROWS = 1000;
    private List<USDPrice> usdPriceList = new ArrayList<>();

    /**
     * Map each Excel rows into USDPrice
     * @param row Excel file's row
     * @return new USDPrice object
     */
    private USDPrice mapExcelDataToUSDPrice(XSSFRow row) {
        return new USDPrice(
                row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // update action
                row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // partNumber
                row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // customerType
                row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // brand
                row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // series
                row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // modelTruck
                row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),          // currency
                row.getCell(7, Row.CREATE_NULL_AS_BLANK).getRawValue(),                 // price
                row.getCell(8, Row.CREATE_NULL_AS_BLANK).getRawValue(),                 // soldAlonePrice
                new SimpleDateFormat("MM/dd/YYYY").format(row.getCell(9).getDateCellValue()),        // startDate
                new SimpleDateFormat("MM/dd/YYYY").format(row.getCell(10).getDateCellValue()),       // endDate
                row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue()          // standard
        );
    }

    @GetMapping(path = "/USDPrice")
    public List<USDPrice> getAllPriceBooks() {
        return usdPriceService.getAllUSDPrices();
    }

    @PostMapping(path = "/USDPrice/import")
    public void importUSDPrices() throws IOException {
        File file = new File("src/main/resources/masterdata/PriceBook.xlsx");
        InputStream inputStream = new FileInputStream(file);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workbook.getSheetAt(0); /// sheet of USD Price book

        //init iterator
        int i = 5;
        usdPriceList.clear();

        while (workSheet.getRow(i) != null) {

            XSSFRow row = workSheet.getRow(i);
            USDPrice usdPrice = mapExcelDataToUSDPrice(row);
            usdPriceList.add(usdPrice);         // add into list

            /*
            If the List length is over NUM_OF_ROWS -> saveAll() then clear() the List
             */
            if(usdPriceList.size() > NUM_OF_ROWS){
                usdPriceService.addListOfUSDPrices(usdPriceList);
                usdPriceList.clear();
            }
            //increase iterator
            i++;
        }
        usdPriceService.addListOfUSDPrices(usdPriceList);   // save the remaining in the List

    }
}
