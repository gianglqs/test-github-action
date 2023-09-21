package com.hysteryale.service;

import com.hysteryale.repository.CurrenciesRepository;
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
    CurrenciesRepository currenciesRepository;
    @Resource
    @InjectMocks
    CurrenciesService currenciesService;
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
        currenciesService.importCurrencies();
    }

}
