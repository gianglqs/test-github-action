package com.hysteryale.controller;

import com.hysteryale.model.DateMaster;
import com.hysteryale.service.DateMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
public class DateMasterController {
    @Autowired
    DateMasterService dateMasterService;
    @PostMapping(path = "/dateMaster/import")
    public void importDateMaster() throws FileNotFoundException {
        dateMasterService.importDateMaster();
    }
    @GetMapping(path = "/dateMaster")
    public List<DateMaster> getAllDateMaster() {
        return dateMasterService.getAllDateMaster();
    }
}
