package com.hysteryale.service;

import com.hysteryale.repository.MetaSeriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@Slf4j
public class MetaSeriesServiceTest {
    @Resource
    @InjectMocks
    MetaSeriesService metaSeriesService;
    @Resource
    @Mock
    MetaSeriesRepository metaSeriesRepository;

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
    void testMetaSeriesImport() throws FileNotFoundException, IllegalAccessException {
        metaSeriesService.importMetaSeries();
    }
}
