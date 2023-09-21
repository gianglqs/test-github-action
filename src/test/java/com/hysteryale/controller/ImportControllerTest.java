package com.hysteryale.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import javax.annotation.Resource;

import java.io.FileNotFoundException;

class ImportControllerTest extends BasedControllerTest {

    @Resource
    @InjectMocks
    ImportController importController;

    @Test
    void importData() {
        try {
            importController.importData();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}