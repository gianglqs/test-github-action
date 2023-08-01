package com.hysteryale.controller;

import com.hysteryale.configuration.JpaConfig;
import com.hysteryale.model.Price;
import com.hysteryale.repository.PriceRepository;
import com.hysteryale.service.PriceService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ContextConfiguration(
        classes = {JpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
class PriceBookControllerTest {
    @Mock
    PriceRepository priceRepository;
    @Mock
    PriceService priceService;
    AutoCloseable autoCloseable;
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
        Price given1 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        Price given2 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        List<Price> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);

        // WHEN
        when(priceBookController.getAllPrices()).thenReturn(givenList);
        List<Price> priceList = priceBookController.getAllPrices();

        // THEN
        Assertions.assertEquals(givenList.size(), priceList.size());
    }

    @Test
    void throwNotFoundIfNoPriceBySeries() {
        // GIVEN
        String series = "not found";

        // WHEN
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, ()->{
            priceBookController.getPricesBySeries(series);
        });

        // THEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("No Price found with series: " + series));
    }

    @Test
    void canGetPricesBySeries() {
        // GIVEN
        Price given1 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        Price given2 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");

        List<Price> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);

        priceRepository.save(given1);
        priceRepository.save(given2);

        priceService.addListOfPrices(givenList);

        String series = "price1";

        // WHEN
        when(priceBookController.getPricesBySeries(series)).thenReturn(givenList);
        List<Price> priceList = priceBookController.getPricesBySeries(series);

        // THEN
        Assertions.assertEquals(givenList.size(), priceList.size());
    }
}