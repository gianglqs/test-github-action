package com.hysteryale.controller;

import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.filters.IndicatorFilter;
import com.hysteryale.service.ChartsService;
import com.hysteryale.service.FilterService;
import com.hysteryale.service.IndicatorsService;
import org.springframework.http.MediaType;
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
    @Resource
    IndicatorsService indicatorsService;

    //TODO  Please rename the apis to /charts/getDataForCompetitorBubbleChart
    @PostMapping(value = "/competitiveLandscape", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getCompetitorPricing(@RequestBody CompetitorPricing competitorPricing) {
        List<CompetitorPricing> competitorPricingList =
                indicatorsService.getCompetitiveLandscape(
                        competitorPricing.getCountry(),
                        competitorPricing.getClazz(),
                        competitorPricing.getCategory(),
                        competitorPricing.getSeries());
        return Map.of(
                "competitiveLandscape", competitorPricingList
        );
    }


    //TODO  Please rename the apis to /charts/getDataForRegionLineChart
    @PostMapping("/lineChartRegion")
    public Map<String, List<CompetitorPricing>> getDataForLineChartRegion(@RequestBody IndicatorFilter filters) {
        Map<String, List<CompetitorPricing>> result = new HashMap<>();
        List<CompetitorPricing> listCompetitorPricingGroupByRegion = chartsService.getCompetitorPricingAfterFilterAndGroupByRegion(filters);
        result.put("lineChartRegion", listCompetitorPricingGroupByRegion);
        return result;
    }

    //TODO  Please rename the apis to /charts/getDataForPlantLineChart
    @PostMapping("/lineChartPlant")
    public Map<String, List<CompetitorPricing>> getDataForLineChartPlant(@RequestBody IndicatorFilter filters) {
        Map<String, List<CompetitorPricing>> result = new HashMap<>();
        List<CompetitorPricing> listCompetitorPricingGroupByRegion = chartsService.getCompetitorPricingAfterFilterAndGroupByPlant(filters);
        result.put("lineChartPlant", listCompetitorPricingGroupByRegion);
        return result;
    }
}
