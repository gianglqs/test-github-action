package com.hysteryale.controller;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.service.UnitFlagsService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(path = "/unitFlags")
    public List<UnitFlags> getAllUnitFlags() {
        return unitFlagsService.getAllUnitFlags();
    }
    @GetMapping(path = "/unitFlags/readyForDistribution/{readyState}")
    public List<UnitFlags> getUnitFlagsByReadyState(@PathVariable String readyState) {
        return unitFlagsService.getUnitFlagsByReadyState(readyState);
    }
}
