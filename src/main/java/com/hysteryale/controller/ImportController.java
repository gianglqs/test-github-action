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

    @Resource
    ImportService importService;

    @PostMapping(path = "/importAllData")
    void importAllData() throws IOException, IllegalAccessException {
       // importApicDealer();
      //  importAPACSerial();
        //importCurrencies();
        importPart();
        importAOPMargin();
        importProductDimension();
        importOrder();
        importExchangeRate();
        importMarginAnalystMacro();
        importMarginAnalystData();
        importCompetitorPricing();
    }

    @PostMapping(path = "/importApicDealer")
    void importApicDealer() throws IOException, IllegalAccessException {
        apicDealerService.importAPICDealer();
    }

    @PostMapping(path = "/importAPACSerial")
    void importAPACSerial() throws IOException, IllegalAccessException {
        apacSerialService.importAPACSerial();
    }

    @PostMapping(path = "/importCurrencies")
    void importCurrencies() throws IOException, IllegalAccessException {
        currencyService.importCurrencies();
    }

    @PostMapping(path = "/importPart")
    void importPart() throws IOException, IllegalAccessException {
        partService.importPart();
    }

    @PostMapping(path = "/importAOPMargin")
    void importAOPMargin() throws IOException, IllegalAccessException {
        aopMarginService.importAOPMargin();
    }

    @PostMapping(path = "/importProductDimension")
    void importProductDimension() throws IOException, IllegalAccessException {
        productDimensionService.importProductDimension();
    }

    @PostMapping(path = "/importOrder")
    void importOrder() throws IOException, IllegalAccessException {
        bookingOrderService.importOrder();
    }

    @PostMapping(path = "/importExchangeRate")
    void importExchangeRate() throws IOException, IllegalAccessException {
        exchangeRateService.importExchangeRate();
    }

    @PostMapping(path = "/importCostUplift")
    void importCostUplift() throws IOException, IllegalAccessException {
        costUpliftService.importCostUplift();
    }

    @PostMapping(path = "/importMarginAnalystMacro")
    void importMarginAnalystMacro() throws IOException, IllegalAccessException {
        marginAnalystMacroService.importMarginAnalystMacro();
    }

    @PostMapping(path = "/importMarginAnalystData")
    void importMarginAnalystData() throws IOException, IllegalAccessException {
        marginAnalystService.importMarginAnalystData();
    }

    @PostMapping(path = "/importCompetitorPricing")
    void importCompetitorPricing() throws IOException, IllegalAccessException {
        importService.importCompetitorPricing();
    }

    @PostMapping(path = "/importShipment")
    void importShipment() throws IOException{
        importService.importShipment();
    }


}