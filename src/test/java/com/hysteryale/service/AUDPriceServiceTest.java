package com.hysteryale.service;

import com.hysteryale.model.AUDPrice;
import com.hysteryale.repository.AUDPriceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

class AUDPriceServiceTest {
    @Mock
    private AUDPriceRepository audPriceRepository;
    private AUDPriceService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new AUDPriceService(audPriceRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canAddListOfAUDPrices() {
        // GIVEN
        List<AUDPrice> audPriceList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AUDPrice tempAUDPrice = new AUDPrice(
                    "updateAction" + i,
                    "partNumber" + i,
                    "customerType" + i,
                    "brand" + i,
                    "series" + i,
                    "modelTruck" + i,
                    "currency" + i,
                    "price" + i,
                    "soldAlonePrice" + i,
                    "startDate" + i,
                    "endDate" + i,
                    "standard" + i
            );
            audPriceList.add(tempAUDPrice);
        }
        // WHEN
        underTest.addListOfAUDPrices(audPriceList);

        //THEN
        verify(audPriceRepository).saveAll(audPriceList);
    }
}