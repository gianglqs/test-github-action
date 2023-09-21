package com.hysteryale.service.impl;


import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import com.hysteryale.service.MarginAnalystService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MarginAnalystServiceImpl implements MarginAnalystService {

    /**
     * @param modelCode
     * @param currency
     * @return
     */
    @Override
    public Map<String, List<MarginAnalystData>> getMarginAnalystData(String modelCode, String currency) {
        return null;
    }

    /**
     * @param modelCode 
     * @param currency
     * @return
     */
    @Override
    public Map<String, MarginAnalystSummary> getMarginAnalystSummary(String modelCode, String currency) {
        // Based on the model Code, we can query all the parts related to it
        // At first, Yurini said that we can find all the parts in the "Novo Quotation Download", after that she said if we do this way
        // then they will have to download for each model code
        // So she wants to use the Power BI file, we can find the Model (AI) then we can get part Number (AT) then we can get the List price (AZ)

        //for the cost RMB, use Margin Analysis, model code + part -> Cost RMB

        //then find the dealer net

        //Calculate margin
        // if there is Cost RMB
        //      margin @ aop USD = Dealer net (net price) * 0.1,
        //      else
        //         margin @ aop USD = (Dealer Net - Cost RMB *(1+ CostUpLift) * (1+ Warranty + Surcharge + Duty)* Margin Analysis @ AOP Rate)- Freight)


        return null;
    }
}
