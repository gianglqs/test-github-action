package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.List;


@Slf4j
public class BookingOrderServiceTest {
    @Resource
    @InjectMocks
    BookingOrderService bookingOrderService;
    @Resource
    @Mock
    BookingOrderRepository bookingOrderRepository;
    private AutoCloseable autoCloseable;
    List<BookingOrder> bookingOrderList = new ArrayList<>();

    @BeforeEach
    void setUp() throws FileNotFoundException, IllegalAccessException {
        autoCloseable = MockitoAnnotations.openMocks(this);
        createMockedBookingOrderList();
    }

    @AfterEach
    void tearDown() throws Exception{
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
    void testGetAllFilesInFolder() {
        // GIVEN
        String folderPath = "import_files/booking";
        int expectedListSize = 12;

        // WHEN
        //List<String> fileList = bookingOrderService.getAllFilesInFolder(folderPath);

        // THEN
        //Assertions.assertEquals(expectedListSize, fileList.size());
    }
    @Test
    void testGetAllBookingOrders() {

        // WHEN
        Mockito.when(bookingOrderRepository.findAll()).thenReturn(bookingOrderList);
        List<BookingOrder> result = bookingOrderService.getAllBookingOrders();

        // THEN
        Mockito.verify(bookingOrderRepository).findAll();
        Assertions.assertFalse(result.isEmpty());
    }
    @Test
    void testGetAPACColumnsName() throws FileNotFoundException {
        InputStream is = new FileInputStream("import_files/APAC/APAC Serial in NOVO master file.xlsx");
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        Sheet orderSheet = workbook.getSheet("Master Summary");
        HashMap<String, Integer> APAC_COLUMNS = new HashMap<>();
        for (Row row : orderSheet) {
            if (row.getRowNum() == 0)
            {
                for(int i = 0; i < 11; i++) {
                    String columnName = row.getCell(i).getStringCellValue();
                    if(APAC_COLUMNS.get(columnName) != null)
                        columnName += "_Yale";
                    APAC_COLUMNS.put(columnName, i);
                }
                log.info("APAC Columns: " + APAC_COLUMNS);
            }
        }
    }
}
