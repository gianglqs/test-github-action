package com.hysteryale.controller;

import com.hysteryale.model.AUDPrice;
import com.hysteryale.service.AUDPriceService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AUDPriceController {
    @Autowired
    private AUDPriceService audPriceService;
    /** Define number of Excel file's rows can be saved at one time */
    private static final Integer NUM_OF_ROWS = 1000;
    private List<AUDPrice> audPriceList = new ArrayList<>();

    /**
     * Map each Excel file's row into AUDPrice
     * @param row Excel file's row
     * @return new AUDPrice object
     */
    private AUDPrice mapExcelDataToAUDPrice(XSSFRow row) {
        return new AUDPrice(
                row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),        // update action
                row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),        // partNumber
                row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),        // customerType
                row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),        // brand
                row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),        // series
                row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),        // modelTruck
                row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),        // currency
                row.getCell(7, Row.CREATE_NULL_AS_BLANK).getRawValue(),               // price
                row.getCell(8, Row.CREATE_NULL_AS_BLANK).getRawValue(),        // soldAlonePrice
                new SimpleDateFormat("MM/dd/YYYY").format(row.getCell(9).getDateCellValue()),        // startDate
                new SimpleDateFormat("MM/dd/YYYY").format(row.getCell(10).getDateCellValue()),       // endDate
                row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue()        // standard
        );
    }

    @PostMapping(path = "/AUDPrice/import")
    public void importAUDPrices() throws IOException {
        File file = new File("src/main/resources/masterdata/PriceBook.xlsx");
        InputStream inputStream = new FileInputStream(file);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workbook.getSheetAt(1); /// sheet of AUD Price book

        //init iterator
        int i = 5;
        audPriceList.clear();

        while (workSheet.getRow(i) != null) {

            XSSFRow row = workSheet.getRow(i);
            AUDPrice audPrice = mapExcelDataToAUDPrice(row);
            audPriceList.add(audPrice);

            /*
            If the List length is over NUM_OF_ROWS -> saveAll() then clear() the List
             */
            if(audPriceList.size() > NUM_OF_ROWS){
                audPriceService.addListOfAUDPrices(audPriceList);
                audPriceList.clear();
            }
            //increase iterator
            i++;
        }
        audPriceService.addListOfAUDPrices(audPriceList);       //save the remaining in the List

    }
}
