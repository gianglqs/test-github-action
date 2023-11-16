package com.hysteryale.service;

import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.repository.CompetitorPricingRepository;
import com.hysteryale.utils.ConvertDataFilterUtil;
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


    public Map<String, Object> getCompetitorPriceForTableByFilter(FilterModel filterModel) {
        logInfo(filterModel.toString());
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        List<CompetitorPricing> competitorPricingList = competitorPricingRepository.findCompetitorByFilterForTable(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                filterMap.get("aopMarginPercentageFilter"), (Pageable) filterMap.get("pageable"));
        result.put("listCompetitor", competitorPricingList);

        //get total Recode
        int totalCompetitor = competitorPricingRepository.getCountAll(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                filterMap.get("aopMarginPercentageFilter"));
        result.put("totalItems", totalCompetitor);
        return result;
    }


    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByRegion(FilterModel filterModel) {
        logInfo(filterModel.toString());
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        return competitorPricingRepository.findCompetitorByFilterForLineChartRegion(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                filterMap.get("aopMarginPercentageFilter"));
    }


    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByPlant(FilterModel filterModel) {
        logInfo(filterModel.toString());
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        return competitorPricingRepository.findCompetitorByFilterForLineChartPlant(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                filterMap.get("aopMarginPercentageFilter"));
    }

    public List<CompetitorPricing> getCompetitiveLandscape(String country, String clazz, String category, String series) {
        return competitorPricingRepository.getListOfCompetitorInGroup(country, clazz, category, series);

    }
}
