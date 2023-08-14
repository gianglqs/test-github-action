package com.hysteryale.controller;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.service.UnitFlagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin
public class UnitFlagsController {
    @Autowired
    UnitFlagsService unitFlagsService;


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
    @PostMapping(path = "/unitFlags/import")
    public void importUnitFlags() throws IOException, ParseException {
        unitFlagsService.mapDataExcelToDB();
    }
    @PostMapping(path = "/unitFlags/saveChanges")
    public void saveChanges() throws IOException, ParseException {
        unitFlagsService.importUnitFlagsChanges();
    }
}
