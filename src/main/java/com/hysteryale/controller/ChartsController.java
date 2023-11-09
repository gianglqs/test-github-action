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
import java.util.List;

@RestController
@RequestMapping("charts")
public class ChartsController {

    @Resource
    ChartsService chartsService;

    @Resource
    FilterService filterService;


    @PostMapping("/lineChartRegion")
    public List<CompetitorPricing> getDataForLineChartRegion(@RequestBody IndicatorFilter filters)  {
        System.out.println("yeee");
        List<CompetitorPricing> listCompetitorPricingGroupByRegion = chartsService.getCompetitorPricingAfterFilterAndGroupByRegion(filters);
        return listCompetitorPricingGroupByRegion;
    }

//    @PostMapping("/lineChartPlant")
//    public List<Object> getDataForLineChartPLant() {
//        return null;
//    }
}
