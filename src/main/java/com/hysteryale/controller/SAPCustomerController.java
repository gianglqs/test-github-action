package com.hysteryale.controller;

import com.hysteryale.service.SAPCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
public class SAPCustomerController {
    @Autowired
    SAPCustomerService sapCustomerService;

    @PostMapping(path = "/SAPCustomer/import")
    public void importSAPCustomer() throws FileNotFoundException {
        sapCustomerService.importSAPCustomer();
    }
}
