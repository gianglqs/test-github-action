package com.hysteryale.service;

import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.filters.IndicatorFilter;
import com.hysteryale.repository.CompetitorPricingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndicatorService extends BasedService {
    @Resource
    CompetitorPricingRepository competitorPricingRepository;



    public Map<String, Object> getCompetitorPriceForTableByFilter(IndicatorFilter indicatorFilter) {
        logInfo(indicatorFilter.getAopMarginPercentageGroup());
        Map<String, Object> result = new HashMap<>();
        // String AOPMarginFilter = convertAOPMarginFilterToData(indicatorFilter.getMarginPercentageGrouping());
        Pageable pageable = PageRequest.of(indicatorFilter.getPageNo(), indicatorFilter.getPerPage());
        List<CompetitorPricing> competitorPricingList = competitorPricingRepository.findCompetitorByFilterForTable(
                indicatorFilter.getRegions() == null || indicatorFilter.getRegions().isEmpty() ? null : indicatorFilter.getRegions(),
                indicatorFilter.getPlants() == null || indicatorFilter.getPlants().isEmpty() ? null : indicatorFilter.getPlants(),
                indicatorFilter.getMetaSeries() == null || indicatorFilter.getMetaSeries().isEmpty() ? null : indicatorFilter.getMetaSeries(),
                indicatorFilter.getClasses() == null || indicatorFilter.getClasses().isEmpty() ? null : indicatorFilter.getClasses(),
                indicatorFilter.getModels() == null || indicatorFilter.getModels().isEmpty() ? null : indicatorFilter.getModels(),
                indicatorFilter.getChineseBrand() == null ? null : (indicatorFilter.getChineseBrand().equals("Chinese Brand")),
                indicatorFilter.getAopMarginPercentageGroup() == null || indicatorFilter.getAopMarginPercentageGroup().isEmpty() ? null : indicatorFilter.getAopMarginPercentageGroup()
                , pageable);
        result.put("listCompetitor", competitorPricingList);

        //get total Recode
        int totalCompetitor = competitorPricingRepository.getCountAll(
                indicatorFilter.getRegions() == null || indicatorFilter.getRegions().isEmpty() ? null : indicatorFilter.getRegions(),
                indicatorFilter.getPlants() == null || indicatorFilter.getPlants().isEmpty() ? null : indicatorFilter.getPlants(),
                indicatorFilter.getMetaSeries() == null || indicatorFilter.getMetaSeries().isEmpty() ? null : indicatorFilter.getMetaSeries(),
                indicatorFilter.getClasses() == null || indicatorFilter.getClasses().isEmpty() ? null : indicatorFilter.getClasses(),
                indicatorFilter.getModels() == null || indicatorFilter.getModels().isEmpty() ? null : indicatorFilter.getModels(),
                indicatorFilter.getChineseBrand() == null ? null : (indicatorFilter.getChineseBrand().equals("Chinese Brand")),
                indicatorFilter.getAopMarginPercentageGroup() == null || indicatorFilter.getAopMarginPercentageGroup().isEmpty() ? null : indicatorFilter.getAopMarginPercentageGroup());
        result.put("totalItems", totalCompetitor);
        return result;
    }

    private String convertAOPMarginFilterToData(String AOPMarginFilter) {
        switch (AOPMarginFilter) {
            case "<10% Margin":
                return "< 0.1";
            case "<20% Margin":
                return "< 0.2";
            case "<30% Margin":
                return "< 0.3";
            case ">=30% Margin":
                return ">= 0.3";
            case "-ve Margin %":
                return "< 0.1";
        }
        return null;
    }




    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByRegion(IndicatorFilter indicatorFilter) {
        logInfo(indicatorFilter.toString());
        List<CompetitorPricing> result = competitorPricingRepository.findCompetitorByFilterForLineChartRegion(
                indicatorFilter.getRegions() == null || indicatorFilter.getRegions().isEmpty() ? null : indicatorFilter.getRegions(),
                indicatorFilter.getPlants() == null || indicatorFilter.getPlants().isEmpty() ? null : indicatorFilter.getPlants(),
                indicatorFilter.getMetaSeries() == null || indicatorFilter.getMetaSeries().isEmpty() ? null : indicatorFilter.getMetaSeries(),
                indicatorFilter.getClasses() == null || indicatorFilter.getClasses().isEmpty() ? null : indicatorFilter.getClasses(),
                indicatorFilter.getModels() == null || indicatorFilter.getModels().isEmpty() ? null : indicatorFilter.getModels(),
                indicatorFilter.getChineseBrand() == null ? null : (indicatorFilter.getChineseBrand().equals("Chinese Brand")));
        return result;
    }

    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByPlant(IndicatorFilter indicatorFilter) {
        logInfo(indicatorFilter.toString());
        List<CompetitorPricing> result = competitorPricingRepository.findCompetitorByFilterForLineChartPlant(
                indicatorFilter.getRegions() == null || indicatorFilter.getRegions().isEmpty() ? null : indicatorFilter.getRegions(),
                indicatorFilter.getPlants() == null || indicatorFilter.getPlants().isEmpty() ? null : indicatorFilter.getPlants(),
                indicatorFilter.getMetaSeries() == null || indicatorFilter.getMetaSeries().isEmpty() ? null : indicatorFilter.getMetaSeries(),
                indicatorFilter.getClasses() == null || indicatorFilter.getClasses().isEmpty() ? null : indicatorFilter.getClasses(),
                indicatorFilter.getModels() == null || indicatorFilter.getModels().isEmpty() ? null : indicatorFilter.getModels(),
                indicatorFilter.getChineseBrand() == null ? null : (indicatorFilter.getChineseBrand().equals("Chinese Brand")));
        return result;
    }

    public List<CompetitorPricing> getCompetitiveLandscape(String country, String clazz, String category, String series) {
        return competitorPricingRepository.getListOfCompetitorInGroup(country, clazz, category, series);

}}
