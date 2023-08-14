package com.hysteryale.controller;

import com.hysteryale.model.CombinedPlantName;
import com.hysteryale.service.CombinedPlantNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
public class CombinedPlantNameController {
    @Autowired
    CombinedPlantNameService combinedPlantNameService;

    @PostMapping(path = "/combinedPlantName/import")
    private void importCombinedPlantName() throws FileNotFoundException {
        combinedPlantNameService.importCombinedPlantName();
    }
    @GetMapping(path = "/combinedPlantName")
    private List<CombinedPlantName> getAllCombinedPlantName() {
        return combinedPlantNameService.getAllCombinedPlantName();
    }
}
