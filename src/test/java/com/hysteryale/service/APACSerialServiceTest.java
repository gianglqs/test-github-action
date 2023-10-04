package com.hysteryale.service;

import com.hysteryale.repository.APACSerialRepository;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

public class APACSerialServiceTest {
    AutoCloseable autoCloseable;
    @Resource
    @InjectMocks
    APACSerialService apacSerialService;
    @Resource
    @Mock
    APACSerialRepository apacSerialRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testImportAPACSerial() throws FileNotFoundException, IllegalAccessException {
//        apacSerialService.importAPACSerial();
    }
}
