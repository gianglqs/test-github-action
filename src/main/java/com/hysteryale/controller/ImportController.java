package com.hysteryale.controller;

import com.hysteryale.service.*;
import com.hysteryale.service.impl.MarginAnalystServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
    @Resource
    CurrencyService currencyService;
    @Resource
    ExchangeRateService exchangeRateService;
    @Resource
    CostUpliftService costUpliftService;

    @Resource
    MarginAnalystService marginAnalystService;

    @PostMapping(path = "/import")
    void importData() throws FileNotFoundException, IllegalAccessException {
        metaSeriesService.importMetaSeries();
        apicDealerService.importAPICDealer();
        apacSerialService.importAPACSerial();
        aopMarginService.importAOPMargin();
        bookingOrderService.importOrder();
        currencyService.importCurrencies();
        exchangeRateService.importExchangeRate();
        costUpliftService.importCostUplift();
        marginAnalystService.importMarginAnalystData();
    }
}
