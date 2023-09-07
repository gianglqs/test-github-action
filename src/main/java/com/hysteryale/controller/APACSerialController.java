package com.hysteryale.controller;

import com.hysteryale.service.APACSerialService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@RestController
public class APACSerialController {
    @Resource
    APACSerialService apacSerialService;

    /**
     * for testing only, will be removed later
     */
    @PostMapping(path = "/apac/import")
    public void apacImport() throws FileNotFoundException, IllegalAccessException {
        apacSerialService.importAPACSerial();
    }
}
