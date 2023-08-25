package com.hysteryale.service;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.repository.UnitFlagsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.Mockito.verify;

public class UnitFlagsServiceTest {
    @Mock @Resource
    private UnitFlagsRepository unitFlagsRepository;
    @InjectMocks @Resource
    private UnitFlagsService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canAddFlags() {
        //GIVEN
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

    @Test
    void canAddListOfUnitFlags() {
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
        underTest.addListOfUnitFlags(saveList);

        // THEN
        verify(unitFlagsRepository).saveAll(saveList);
    }

    @Test
    void canGetUnitFlagsByReadyState() {
        // WHEN
        underTest.getUnitFlagsByReadyState("y");

        // THEN
        verify(unitFlagsRepository).getUnitFlagsByReadyState("y");
    }
}