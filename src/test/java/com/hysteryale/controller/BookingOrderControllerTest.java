package com.hysteryale.controller;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.service.BookingOrderService;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingOrderControllerTest {
    AutoCloseable autoCloseable;
    @Resource
    @Mock
    BookingOrderService bookingOrderService;
    @Resource
    @InjectMocks
    BookingOrderController bookingOrderController;

    List<BookingOrder> bookingOrderList = new ArrayList<>();

    @BeforeEach
    void setUp() throws FileNotFoundException, IllegalAccessException {
        autoCloseable = MockitoAnnotations.openMocks(this);
        createMockedBookingOrderList();
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    void createMockedBookingOrderList() throws IllegalAccessException, FileNotFoundException {
        InputStream is = new FileInputStream("import_files/booking/01. Bookings Register - Apr -2023 (Jason).xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        Sheet orderSheet = workbook.getSheet("Input - Bookings");
        for (Row row : orderSheet) {
            if(row.getRowNum() == 1)
                bookingOrderService.getOrderColumnsName(row);
            else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()
                    && row.getRowNum() > 1) {
                BookingOrder newBookingOrder = bookingOrderService.mapExcelDataIntoOrderObject(row);
                bookingOrderList.add(newBookingOrder);
            }
        }
    }

    @Test
    void testGetAllBookingOrder() {
        // WHEN
        Mockito.when(bookingOrderService.getAllBookingOrders()).thenReturn(bookingOrderList);
        Map<String, List<BookingOrder>> bookingOrders =  bookingOrderController.getAllOrder();

        // THEN
        Mockito.verify(bookingOrderService).getAllBookingOrders();
        Assertions.assertFalse(bookingOrders.get("bookingOrderList").isEmpty());
    }

}
