package com.hysteryale.controller;

import com.hysteryale.model.UnitFlags;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UnitFlagsControllerTest {
    @Resource
    @Mock
    private UnitFlagsService unitFlagsService;
    @InjectMocks
    private UnitFlagsController unitFlagsController;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetAllUnitFlags() {
        // GIVEN
        List<UnitFlags> saveList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GregorianCalendar createdDate = new GregorianCalendar();
            UnitFlags givenUnitFlags = new UnitFlags(
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    createdDate,
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
    void testGetUnitFlagsByReadyState() {
        // GIVEN
        List<UnitFlags> saveList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GregorianCalendar createdDate = new GregorianCalendar();
            UnitFlags givenUnitFlags = new UnitFlags(
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    "abc",
                    createdDate,
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
    void testThrowNotFoundExceptionIfNoUnitFlagsFound() {
        // GIVEN
        String readyState = "not found";

        // WHEN
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> unitFlagsController.getUnitFlagsByReadyState(readyState));

        // THEN
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;

        Assertions.assertEquals(expectedStatus, exception.getStatus());
    }
}