package com.hysteryale.service;

import com.hysteryale.repository.BookingOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
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

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
    void testGetAllFilesInFolder() {
        // GIVEN
        String folderPath = "import_files/booking";
        int expectedListSize = 12;

        // WHEN
        List<String> fileList = bookingOrderService.getAllFilesInFolder(folderPath);

        // THEN
        Assertions.assertEquals(expectedListSize, fileList.size());
    }
    @Test
    void testImportBookingOrder() throws FileNotFoundException, IllegalAccessException {
        bookingOrderService.importOrder();
    }
}
