package com.hysteryale.service;

import com.hysteryale.model.USDPrice;
import com.hysteryale.repository.USDPriceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class USDPriceServiceTest {
    @Mock
    private USDPriceRepository usdPriceRepository;
    private USDPriceService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new USDPriceService(usdPriceRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canGetAllUSDPrices() {
        // WHEN
        underTest.getAllUSDPrices();
        //THEN
        verify(usdPriceRepository).findAll();
    }

    @Test
    void canAddListOfUSDPrices() {
        // GIVEN
        List<USDPrice> usdPriceList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            USDPrice tempUSDPrice = new USDPrice(
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
            usdPriceList.add(tempUSDPrice);
        }
        // WHEN
        underTest.addListOfUSDPrices(usdPriceList);

        //THEN
        verify(usdPriceRepository).saveAll(usdPriceList);
    }
}