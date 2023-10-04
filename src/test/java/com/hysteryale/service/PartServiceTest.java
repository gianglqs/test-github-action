package com.hysteryale.service;

import com.hysteryale.repository.PartRepository;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PartServiceTest {
    @Resource @Mock
    PartRepository partRepository;
    @Resource @InjectMocks
    PartService partService;
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
    void testImportPart() throws IOException {
//        partService.importPart();
    }
}
