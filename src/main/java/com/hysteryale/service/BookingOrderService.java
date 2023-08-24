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
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void getOrderColumnsName(Row row){
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
    private BookingOrder mapExcelDataIntoOrderObject(Row row) throws IllegalAccessException {
        BookingOrder bookingOrder = new BookingOrder();
        Class<? extends BookingOrder> bookingOrderClass = bookingOrder.getClass();
        Field[] fields = bookingOrderClass.getDeclaredFields();

        for (Field field: fields) {
            // String key of column's name
            String hashMapKey = field.getName().toUpperCase();
            // Get the data type of the field
            String fieldType = field.getType().getName();

            // Currency column is the only one which is not uppercase all character
            if(field.getName().equals("currency"))
                hashMapKey = "Currency";

            // allow assigning value for object's fields
            field.setAccessible(true);
            switch (fieldType) {
                case "java.lang.String":
                    field.set(bookingOrder, row.getCell(ORDER_COLUMNS_NAME.get(hashMapKey), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    break;
                case "int":
                    field.set(bookingOrder, (int) row.getCell(ORDER_COLUMNS_NAME.get(hashMapKey)).getNumericCellValue());
                    break;
                case "java.util.GregorianCalendar":
                    String strDate = row.getCell(ORDER_COLUMNS_NAME.get("DATE")).getStringCellValue();

                    // Cast into GregorianCalendar
                        // Create matcher with pattern {(1)_year(2)_month(2)_day(2)} as 1230404
                    Pattern pattern = Pattern.compile("^\\d(\\d\\d)(\\d\\d)(\\d\\d)");
                    Matcher matcher = pattern.matcher(strDate);
                    int year, month, day;

                    if (matcher.find()) {
                        year = Integer.parseInt(matcher.group(1)) + 2000;
                        month = Integer.parseInt(matcher.group(2));
                        day = Integer.parseInt(matcher.group(3));
                        log.info(year + " " + month + " " + day);

                        GregorianCalendar orderDate = new GregorianCalendar();

                        // {month - 1} is the index to get value in List of month {Jan, Feb, March, April, May, ...}
                        orderDate.set(year, month - 1, day, 0, 0, 0);
                        field.set(bookingOrder, orderDate);
                    }
                    break;
            }
        }
        return bookingOrder;
    }
    public void importOrder() throws FileNotFoundException, IllegalAccessException {
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
