package com.hysteryale.controller;

import com.hysteryale.service.*;
import com.hysteryale.service.marginAnalyst.MarginAnalystMacroService;
import com.hysteryale.service.marginAnalyst.MarginAnalystService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
public class ImportController {

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
    ProductDimensionService productDimensionService;

    @Resource
    PartService partService;
    @Resource
    MarginAnalystMacroService marginAnalystMacroService;

    @PostMapping(path = "/import")
    void importData() throws IOException, IllegalAccessException {
//        metaSeriesService.importMetaSeries();
//        apicDealerService.importAPICDealer();
//        apacSerialService.importAPACSerial();
//        currencyService.importCurrencies(curencyFolder);
//        partService.importPart();
//        aopMarginService.importAOPMargin();
 //       productDimensionService.importProductDimension();
        bookingOrderService.importOrder();
//        exchangeRateService.importExchangeRate();
//        costUpliftService.importCostUplift();
//        marginAnalystMacroService.importMarginAnalystMacro();
//        marginAnalystService.importMarginAnalystData();
    }
}