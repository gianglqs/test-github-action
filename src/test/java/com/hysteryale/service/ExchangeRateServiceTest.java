package com.hysteryale.service;

import com.hysteryale.repository.CurrencyRepository;
import com.hysteryale.repository.ExchangeRateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExchangeRateServiceTest {
    @Resource
    @Mock
    ExchangeRateRepository exchangeRateRepository;
    @Resource
    @InjectMocks
    ExchangeRateService exchangeRateService;
    @Resource
    @Mock
    CurrencyRepository currencyRepository;
    @Resource
    @Mock
    CurrencyService currencyService;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testImportExchangeRate() throws IOException {
//        exchangeRateService.importExchangeRate();
    }

}
