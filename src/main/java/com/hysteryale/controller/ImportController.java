package com.hysteryale.controller;

import com.hysteryale.service.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;

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
    @Resource
    CurrencyService currencyService;
    @Resource
    ExchangeRateService exchangeRateService;
    @Resource
    CostUpliftService costUpliftService;

    @Resource
    MarginAnalystService marginAnalystService;

    @Resource
    BookingOrderPartService bookingOrderPartService;

    @Resource
    PartService partService;

    String curencyFolder = "import_files/currency_exchangerate";

    @PostMapping(path = "/import")
    void importData() throws IOException, IllegalAccessException {
//        metaSeriesService.importMetaSeries();
//        apicDealerService.importAPICDealer();
        apacSerialService.importAPACSerial();
//        currencyService.importCurrencies(curencyFolder);
//        partService.importPart();
//        bookingOrderPartService.importBookingOrderPart();
        aopMarginService.importAOPMargin();
        bookingOrderService.importOrder();
//        exchangeRateService.importExchangeRate();
//        costUpliftService.importCostUplift();
        //  marginAnalystService.importMarginAnalystData();
    }
}
