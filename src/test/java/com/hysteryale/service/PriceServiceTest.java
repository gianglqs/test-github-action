package com.hysteryale.service;

import com.hysteryale.model.Price;
import com.hysteryale.repository.PriceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

public class PriceServiceTest {
    @Resource
    @Mock
    private PriceRepository priceRepository;
    @Resource @InjectMocks
    private PriceService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetAllPrices() {
        // WHEN
        underTest.getAllPrices();
        //THEN
        verify(priceRepository).findAll();
    }

    @Test
    void testAddListOfPriceBook() {
        // GIVEN
        List<Price> priceList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Price tempPrice = new Price(
                    "updateAction" + i,
                    "partNumber" + i,
                    "customerType" + i,
                    "brand" + i,
                    "series" + i,
                    "modelTruck" + i,
                    "currency" + i,
                    Double.longBitsToDouble(i),
                    Double.longBitsToDouble(i),
                    new Date(2023, 7, 31),
                    new Date(2023, 7, 31),
                    "standard" + i
            );
            priceList.add(tempPrice);
        }
        // WHEN
        underTest.addListOfPrices(priceList);

        //THEN
        verify(priceRepository).saveAll(priceList);
    }

    @Test
    void testGetListOfPricesBySeries() {
        // GIVEN
        String seriesNum = "C287";

        //WHEN
        underTest.getPricesBySeries(seriesNum);

        //THEN
        verify(priceRepository).getPricesListBySeries(seriesNum);
    }
}