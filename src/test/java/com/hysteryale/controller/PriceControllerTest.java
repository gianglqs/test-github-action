package com.hysteryale.controller;

import com.hysteryale.model.Price;
import com.hysteryale.service.PriceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

class PriceControllerTest {
    @Autowired @Mock
    PriceService priceService;
    AutoCloseable autoCloseable;
    @Autowired @InjectMocks
    PriceController priceController;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    void testGetAllPrices() {
        // GIVEN
        Price given1 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        Price given2 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        List<Price> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);
        priceService.addListOfPrices(givenList);

        // WHEN
        when(priceService.getAllPrices()).thenReturn(givenList);
        List<Price> result = priceController.getAllPrices();

        // THEN
        Assertions.assertEquals(givenList.size(), result.size());
    }

    @Test
    void testThrowNotFoundIfNoPriceBySeries() {
        // GIVEN
        String series = "not found";

        // WHEN
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, ()-> priceController.getPricesBySeries(series));

        // THEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetPricesBySeries() {
        // GIVEN
        Price given1 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        Price given2 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");

        List<Price> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);
        priceService.addListOfPrices(givenList);

        // WHEN
        String series = "price1";
        when(priceService.getPricesBySeries(series)).thenReturn(givenList);
        List<Price> priceList = priceController.getPricesBySeries(series);

        // THEN
        Assertions.assertEquals(givenList.size(), priceList.size());
    }
}