package com.hysteryale.controller;

import com.hysteryale.service.APACSerialService;
import com.hysteryale.service.APICDealerService;
import com.hysteryale.service.BookingOrderService;
import com.hysteryale.service.MetaSeriesService;
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

    @PostMapping(path = "/import")
    void importData() throws FileNotFoundException, IllegalAccessException {
        metaSeriesService.importMetaSeries();
        apicDealerService.importAPICDealer();
        apacSerialService.importAPACSerial();
        bookingOrderService.importOrder();
    }
}
