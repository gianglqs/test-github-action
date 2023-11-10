package com.hysteryale.service;

import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.filters.IndicatorFilter;
import com.hysteryale.repository.CompetitorPricingRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChartsService {
    @Resource
    CompetitorPricingRepository competitorPricingRepository;




    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByRegion(IndicatorFilter indicatorFilter) {
        System.out.println(indicatorFilter.toString());
        List<CompetitorPricing> result = competitorPricingRepository.findCompetitorByFilterForLineChartRegion(
                indicatorFilter.getRegions() == null || indicatorFilter.getRegions().isEmpty() ? null : indicatorFilter.getRegions(),
                indicatorFilter.getPlants() == null || indicatorFilter.getPlants().isEmpty() ? null : indicatorFilter.getPlants(),
                indicatorFilter.getMetaSeries() == null || indicatorFilter.getMetaSeries().isEmpty() ? null : indicatorFilter.getMetaSeries(),
                indicatorFilter.getClasses() == null || indicatorFilter.getClasses().isEmpty() ? null : indicatorFilter.getClasses(),
                indicatorFilter.getModels() == null || indicatorFilter.getModels().isEmpty() ? null : indicatorFilter.getModels(),
                indicatorFilter.getIsChinese() == null ? null : (indicatorFilter.getIsChinese().equals("Chinese Brand")));
        return result;
    }

    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByPlant(IndicatorFilter indicatorFilter) {
        System.out.println(indicatorFilter.toString());
        List<CompetitorPricing> result = competitorPricingRepository.findCompetitorByFilterForLineChartPlant(
                indicatorFilter.getRegions() == null || indicatorFilter.getRegions().isEmpty() ? null : indicatorFilter.getRegions(),
                indicatorFilter.getPlants() == null || indicatorFilter.getPlants().isEmpty() ? null : indicatorFilter.getPlants(),
                indicatorFilter.getMetaSeries() == null || indicatorFilter.getMetaSeries().isEmpty() ? null : indicatorFilter.getMetaSeries(),
                indicatorFilter.getClasses() == null || indicatorFilter.getClasses().isEmpty() ? null : indicatorFilter.getClasses(),
                indicatorFilter.getModels() == null || indicatorFilter.getModels().isEmpty() ? null : indicatorFilter.getModels(),
                indicatorFilter.getIsChinese() == null ? null : (indicatorFilter.getIsChinese().equals("Chinese Brand")));
        return result;
    }


}
