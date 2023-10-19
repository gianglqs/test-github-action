package com.hysteryale.controller;

import com.hysteryale.service.*;
import com.hysteryale.service.marginAnalyst.MarginAnalystService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Resource;

public abstract class BasedControllerTest {

    protected AutoCloseable autoCloseable;

//    @Resource @Mock
//    protected  MetaSeriesService metaSeriesService;
    @Resource @Mock
    protected APICDealerService apicDealerService;
    @Resource @Mock
    protected APACSerialService apacSerialService;
    @Resource @Mock
    protected BookingOrderService bookingOrderService;
    @Resource @Mock
    protected AOPMarginService aopMarginService;
    @Resource @Mock
    protected CurrencyService currencyService;
    @Resource @Mock
    protected ExchangeRateService exchangeRateService;
    @Resource @Mock
    protected CostUpliftService costUpliftService;

    @Mock
    protected MarginAnalystService marginAnalystService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown(){
        try {
            autoCloseable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
