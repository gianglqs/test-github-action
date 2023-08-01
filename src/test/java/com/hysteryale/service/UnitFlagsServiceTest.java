package com.hysteryale.service;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.repository.UnitFlagsRepository;
import lombok.ToString;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UnitFlagsServiceTest {
    @Mock
    private UnitFlagsRepository unitFlagsRepository;
    private UnitFlagsService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new UnitFlagsService(unitFlagsRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canAddFlags() {
        //GIVEN
        Timestamp timestamp = new Timestamp(2023, 8, 1, 0, 0, 0,0);
        UnitFlags givenUnitFlags = new UnitFlags(
                "abc",
                "abc",
                "abc",
                "abc",
                "abc",
                "abc",
                "abc",
                timestamp,
                "abc"
                );
        //WHEN
        underTest.addUnitFlags(givenUnitFlags);

        //THEN
        verify(unitFlagsRepository).save(givenUnitFlags);
    }
    @Test
    void canGetAllUnitFlags() {
        //WHEN
        underTest.getAllUnitFlags();
        //THEN
        verify(unitFlagsRepository).findAll();
    }
}