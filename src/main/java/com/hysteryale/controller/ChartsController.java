package com.hysteryale.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.filters.IndicatorFilter;
import com.hysteryale.service.ChartsService;
import com.hysteryale.service.FilterService;
import net.minidev.json.parser.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("charts")
public class ChartsController {

    @Resource
    ChartsService chartsService;

    @Resource
    FilterService filterService;


    @PostMapping("/lineChartRegion")
    public Map<String, List<CompetitorPricing>> getDataForLineChartRegion(@RequestBody IndicatorFilter filters) {
        Map<String, List<CompetitorPricing>> result = new HashMap<>();
        List<CompetitorPricing> listCompetitorPricingGroupByRegion = chartsService.getCompetitorPricingAfterFilterAndGroupByRegion(filters);
        result.put("lineChartRegion", listCompetitorPricingGroupByRegion);
        return result;
    }

    @PostMapping("/lineChartPlant")
    public Map<String, List<CompetitorPricing>> getDataForLineChartPlant(@RequestBody IndicatorFilter filters) {
        Map<String, List<CompetitorPricing>> result = new HashMap<>();
        List<CompetitorPricing> listCompetitorPricingGroupByRegion = chartsService.getCompetitorPricingAfterFilterAndGroupByPlant(filters);
        result.put("lineChartPlant", listCompetitorPricingGroupByRegion);
        return result;
    }
}
