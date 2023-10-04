package com.hysteryale.service;

import com.hysteryale.repository.AOPMarginRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

public class AOPMarginServiceTest {
    @Resource
    @InjectMocks
    AOPMarginService aopMarginService;
    @Resource
    @Mock
    AOPMarginRepository aopMarginRepository;
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
    void testImportAOPMargin() throws FileNotFoundException, IllegalAccessException {
      //  aopMarginService.importAOPMargin();
    }
}
