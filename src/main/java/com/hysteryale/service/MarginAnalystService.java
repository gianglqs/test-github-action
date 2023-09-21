package com.hysteryale.service;

import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import java.util.List;

import java.util.Map;

public interface MarginAnalystService {

    /**
     * To query the margin analyst data of a model code
     *
     * @param modelCode
     * @param currency
     * @return
     */
    Map<String, List<MarginAnalystData>> getMarginAnalystData(String modelCode, String currency);

    /**
     * To query the margin analyst summarized of a model code
     * @param modelCode
     * @param currency
     * @return
     */
    Map<String, MarginAnalystSummary> getMarginAnalystSummary(String modelCode, String currency);

}
