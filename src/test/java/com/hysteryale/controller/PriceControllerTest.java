package com.hysteryale.controller;

import com.hysteryale.model.Price;
import com.hysteryale.service.PriceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class PriceControllerTest extends BasedControllerTest{
    @InjectMocks
    PriceController priceController;

    @Test
    void testGetAllPrices() {

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
    }
}