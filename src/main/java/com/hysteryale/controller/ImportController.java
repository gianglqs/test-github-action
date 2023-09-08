package com.hysteryale.controller;

import com.hysteryale.service.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@RestController
public class ImportController {
    @Resource
    MetaSeriesService metaSeriesService;
    @Resource
    APICDealerService apicDealerService;
    @Resource
    APACSerialService apacSerialService;
    @Resource
    BookingOrderService bookingOrderService;
    @Resource
    AOPMarginService aopMarginService;

    @PostMapping(path = "/import")
    void importData() throws FileNotFoundException, IllegalAccessException {
        metaSeriesService.importMetaSeries();
        apicDealerService.importAPICDealer();
        apacSerialService.importAPACSerial();
        aopMarginService.importAOPMargin();
        bookingOrderService.importOrder();
    }
}
