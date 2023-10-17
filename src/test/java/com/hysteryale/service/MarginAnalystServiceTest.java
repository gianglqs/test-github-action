package com.hysteryale.service;

import com.hysteryale.repository.marginAnalyst.MarginAnalystDataRepository;
import com.hysteryale.service.impl.MarginAnalystServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
public class MarginAnalystServiceTest {
    @Resource
    @Mock
    MarginAnalystDataRepository marginAnalystDataRepository;
    @Resource
    @InjectMocks
    MarginAnalystServiceImpl marginAnalystService;
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
    void testImportMarginAnalysis() throws IOException {
//        marginAnalystService.importMarginAnalystData();
    }

}
