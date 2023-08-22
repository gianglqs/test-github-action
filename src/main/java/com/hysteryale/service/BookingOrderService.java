package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.repository.BookingOrderRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class BookingOrderService {
    @Autowired
    BookingOrderRepository bookingOrderRepository;
    private final HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();

    /**
     * Get Columns' name in Booking Excel file, then store them (columns' name) respectively with the index into HashMap
     * @param row which contains columns' name
     */
    private void getOrderColumnsName(Row row){
        for(int i = 0; i < 17; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            ORDER_COLUMNS_NAME.put(columnName, i);
        }
        log.info("Order Columns: " + ORDER_COLUMNS_NAME);
    }

    /**
     * Map data in Excel file into each Order object
     * @param row which is the row contains data
     * @return new Order object
     */
    private BookingOrder mapExcelDataIntoOrderObject(Row row) {
        //format Date from Excel
        String strDate = row.getCell(ORDER_COLUMNS_NAME.get("DATE")).getStringCellValue();
        int year = Integer.parseInt(strDate.substring(1, 3)) + 2000;
        int month = Integer.parseInt(strDate.substring(3, 5));
        int day = Integer.parseInt(strDate.substring(5, 7));

        GregorianCalendar orderDate = new GregorianCalendar();
        orderDate.set(year, month - 1, day, 0, 0, 0);

        return new BookingOrder(
                row.getCell(ORDER_COLUMNS_NAME.get("ORDERNO")).getStringCellValue(),
                orderDate,
                row.getCell(ORDER_COLUMNS_NAME.get("Currency")).getStringCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("ORDERTYPE")).getStringCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("REGION")).getStringCellValue(),
                (int) row.getCell(ORDER_COLUMNS_NAME.get("MKTGRP")).getNumericCellValue(),
                (int) row.getCell(ORDER_COLUMNS_NAME.get("BILLTO")).getNumericCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("DEALERNAME")).getStringCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("CTRYCODE")).getStringCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("DEALERPO")).getStringCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("SERIES")).getStringCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("MODEL")).getStringCellValue(),
                row.getCell(ORDER_COLUMNS_NAME.get("COMMENT"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue(),
                (int) row.getCell(ORDER_COLUMNS_NAME.get("TRUCKCLASS")).getNumericCellValue()
        );
    }
    public void importOrder() throws FileNotFoundException {
        InputStream is = new FileInputStream("importdata/masterdata/01. Bookings Register - Apr -2023 (Jason).xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        List<BookingOrder> bookingOrderList = new ArrayList<>();

        Sheet orderSheet = workbook.getSheet("Input - Bookings");
        for (Row row : orderSheet) {
            if(row.getRowNum() == 1)
                getOrderColumnsName(row);
            else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()
                        && row.getRowNum() > 1) {
                BookingOrder newBookingOrder = mapExcelDataIntoOrderObject(row);
                bookingOrderList.add(newBookingOrder);
            }
        }
        bookingOrderRepository.saveAll(bookingOrderList);
        log.info("New Orders saved: " + bookingOrderList.size());
    }
    public List<BookingOrder> getAllOrders() {
        return bookingOrderRepository.findAll();
    }
}
