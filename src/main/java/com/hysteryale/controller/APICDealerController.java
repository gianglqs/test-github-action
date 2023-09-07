package com.hysteryale.controller;

import com.hysteryale.service.APICDealerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@RestController
public class APICDealerController {
    @Resource
    APICDealerService apicDealerService;

    @PostMapping(path = "/apic/import")
    public void importAPICDealer() throws FileNotFoundException, IllegalAccessException {
        apicDealerService.importAPICDealer();
    }
}
