package com.hysteryale.controller;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.service.UnitFlagsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

class UnitFlagsControllerTest extends BasedControllerTest {

    @InjectMocks
    private UnitFlagsController unitFlagsController;

    @Test
    void testGetAllUnitFlags() {
    }

    @Test
    void testGetUnitFlagsByReadyState() {
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