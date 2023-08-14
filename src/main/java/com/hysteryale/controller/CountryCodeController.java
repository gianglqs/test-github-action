package com.hysteryale.controller;

import com.hysteryale.service.CountryCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
public class CountryCodeController {
    @Autowired
    CountryCodeService countryCodeService;

    @PostMapping(path = "/countryCode/import")
    public void importCountryCode() throws FileNotFoundException {
        countryCodeService.importCountryCode();
    }
}
