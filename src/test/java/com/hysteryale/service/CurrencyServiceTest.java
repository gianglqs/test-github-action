package com.hysteryale.service;

import com.hysteryale.repository.CurrencyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

public class CurrencyServiceTest {
    @Resource
    @Mock
    CurrencyRepository currencyRepository;
    @Resource
    @InjectMocks
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
    void testImportCurrencies() throws FileNotFoundException {
        currencyService.importCurrencies();
    }

}
