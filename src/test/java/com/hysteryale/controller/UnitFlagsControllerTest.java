package com.hysteryale.controller;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.repository.UnitFlagsRepository;
import com.hysteryale.service.UnitFlagsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UnitFlagsControllerTest {
    @Mock
    private UnitFlagsRepository unitFlagsRepository;
    @Mock
    private UnitFlagsService unitFlagsService;
    @InjectMocks
    private UnitFlagsController unitFlagsController;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        unitFlagsService = new UnitFlagsService(unitFlagsRepository);
        unitFlagsController = new UnitFlagsController(unitFlagsService);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canGetAllUnitFlags() {
        // GIVEN
        List<UnitFlags> saveList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
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
            saveList.add(givenUnitFlags);
        }

        // WHEN
        when(unitFlagsService.getAllUnitFlags()).thenReturn(saveList);
        List<UnitFlags> result = unitFlagsController.getAllUnitFlags();

        // THEN
        assertEquals(saveList.size(), result.size());
    }

    @Test
    void canGetUnitFlagsByReadyState() {
        // GIVEN
        List<UnitFlags> saveList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
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
            saveList.add(givenUnitFlags);
        }

        // WHEN
        when(unitFlagsService.getUnitFlagsByReadyState("abc")).thenReturn(saveList);
        List<UnitFlags> result = unitFlagsController.getUnitFlagsByReadyState("abc");

        // THEN
        assertEquals(saveList.size(), result.size());
    }
    @Test
    void throwNotFoundExceptionIfNoUnitFlagsFound() {
        // GIVEN
        String readyState = "not found";

        // WHEN
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            unitFlagsController.getUnitFlagsByReadyState(readyState);
        });

        // THEN
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "No UnitFlags found with Ready for Distribution: " + readyState;

        Assertions.assertEquals(expectedStatus, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains(expectedMessage));
    }
}