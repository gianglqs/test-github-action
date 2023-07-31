package com.hysteryale.controller;

import com.hysteryale.model.Price;
import com.hysteryale.repository.PriceRepository;
import com.hysteryale.service.PriceService;
import io.swagger.models.Response;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PriceBookControllerTest {
    @Mock
    PriceRepository priceRepository;
    @Mock
    PriceService priceService;
    private AutoCloseable autoCloseable;
    @InjectMocks
    PriceBookController priceBookController;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        priceService = new PriceService(priceRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canGetAllPrices() {
        // GIVEN
        Price given1 = new Price("test1", "test1", "test1", "test1", "test1", "test1", "test1", 1.0, 1.0, new Date(2023, 7, 31), new Date(2023, 7, 31), "test1");
        Price given2 = new Price("test2", "test2", "test2", "test2", "test2", "test2", "test2", 2.0, 2.0, new Date(2023, 7, 31), new Date(2023, 7, 31), "test2");


        List<Price> priceList = new ArrayList<>();
        priceList.add(given1);
        priceList.add(given2);

        priceRepository.saveAll(priceList);

        // WHEN
        when(priceBookController.getAllPrices()).thenReturn(priceList);
        List<Price> result = priceBookController.getAllPrices();

        //THEN
        assertEquals(priceList.size(), result.size());
    }

}