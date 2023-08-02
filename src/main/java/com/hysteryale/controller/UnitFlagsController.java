package com.hysteryale.controller;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.service.UnitFlagsService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UnitFlagsController {
    @Autowired
    UnitFlagsService unitFlagsService;

    public UnitFlagsController(UnitFlagsService unitFlagsService) {
        this.unitFlagsService = unitFlagsService;
    }

    @GetMapping(path = "/unitFlags")
    public List<UnitFlags> getAllUnitFlags() {
        return unitFlagsService.getAllUnitFlags();
    }
    @GetMapping(path = "/unitFlags/readyForDistribution/{readyState}")
    public List<UnitFlags> getUnitFlagsByReadyState(@PathVariable String readyState) {
        List<UnitFlags> unitFlagsList = unitFlagsService.getUnitFlagsByReadyState(readyState);
        if(unitFlagsList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No UnitFlags found with Ready for Distribution: " + readyState);
        return unitFlagsList;
    }
    @PostMapping(path = "/unitFlags/saveChanges")
    public void saveChanges() throws IOException, ParseException {
        unitFlagsService.importUnitFlagsChanges();
    }
}
