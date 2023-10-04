package com.hysteryale.service;

import com.hysteryale.repository.APICDealerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;

public class APICDealerServiceTest {
    @Resource
    @InjectMocks
    APICDealerService apicDealerService;

    @Resource
    @Mock
    APICDealerRepository apicDealerRepository;

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
    void testImportAPICDealer() throws FileNotFoundException, IllegalAccessException {
//        apicDealerService.importAPICDealer();
    }
}
