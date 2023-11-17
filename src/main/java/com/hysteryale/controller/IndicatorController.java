package com.hysteryale.controller;

import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.service.IndicatorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IndicatorController {


    @Resource
    IndicatorService indicatorService;


    @PostMapping("/getCompetitorData")
    public Map<String, Object> getCompetitorData(@RequestBody FilterModel filters,
                                                 @RequestParam(defaultValue = "0") int pageNo,
                                                 @RequestParam(defaultValue = "100") int perPage) {
        filters.setPageNo(pageNo);
        filters.setPerPage(perPage);

        return indicatorService.getCompetitorPriceForTableByFilter(filters);
    }

    @PostMapping(value = "/chart/getDataForCompetitorBubbleChart", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getCompetitorPricing(@RequestBody CompetitorPricing competitorPricing) {
        List<CompetitorPricing> competitorPricingList =
                indicatorService.getCompetitiveLandscape(
                        competitorPricing.getCountry(),
                        competitorPricing.getClazz(),
                        competitorPricing.getCategory(),
                        competitorPricing.getSeries());
        return Map.of(
                "competitiveLandscape", competitorPricingList
        );
    }


    @PostMapping("/chart/getDataForRegionLineChart")
    public Map<String, List<CompetitorPricing>> getDataForLineChartRegion(@RequestBody FilterModel filters) {
        Map<String, List<CompetitorPricing>> result = new HashMap<>();
        List<CompetitorPricing> listCompetitorPricingGroupByRegion = indicatorService.getCompetitorPricingAfterFilterAndGroupByRegion(filters);
        result.put("lineChartRegion", listCompetitorPricingGroupByRegion);
        return result;
    }

    @PostMapping("/chart/getDataForPlantLineChart")
    public Map<String, List<CompetitorPricing>> getDataForLineChartPlant(@RequestBody FilterModel filters) {
        Map<String, List<CompetitorPricing>> result = new HashMap<>();
        List<CompetitorPricing> listCompetitorPricingGroupByRegion = indicatorService.getCompetitorPricingAfterFilterAndGroupByPlant(filters);
        result.put("lineChartPlant", listCompetitorPricingGroupByRegion);
        return result;
    }


}
