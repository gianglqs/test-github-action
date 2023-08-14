package com.hysteryale.controller;

import com.hysteryale.service.TM1ProductRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
public class TM1ProductRangeController {
    @Autowired
    TM1ProductRangeService tm1ProductRangeService;
    @PostMapping(path = "/TM1ProductRange/import")
    public void importTM1ProductRange() throws FileNotFoundException {
        tm1ProductRangeService.importProductRange();
    }
}
