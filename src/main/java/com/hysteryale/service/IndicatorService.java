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
        logInfo(indicatorFilter.toString());
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> filterMap = loadDataFilterIntoMap(indicatorFilter);
        List<CompetitorPricing> competitorPricingList = competitorPricingRepository.findCompetitorByFilterForTable(
                (List<String>) filterMap.get("regionFilter"), (List<String>) filterMap.get("plantFilter"), (List<String>) filterMap.get("metaSeriesFilter"),
                (List<String>) filterMap.get("classFilter"), (List<String>) filterMap.get("modelFilter"), (Boolean) filterMap.get("ChineseBrandFilter"),
                (String) filterMap.get("aopMarginPercentageFilter"), (Pageable) filterMap.get("pageable"));
        result.put("listCompetitor", competitorPricingList);

        //get total Recode
        int totalCompetitor = competitorPricingRepository.getCountAll(
                (List<String>) filterMap.get("regionFilter"), (List<String>) filterMap.get("plantFilter"), (List<String>) filterMap.get("metaSeriesFilter"),
                (List<String>) filterMap.get("classFilter"), (List<String>) filterMap.get("modelFilter"), (Boolean) filterMap.get("ChineseBrandFilter"),
                (String) filterMap.get("aopMarginPercentageFilter"));
        result.put("totalItems", totalCompetitor);
        return result;
    }

    private Map<String, Object> loadDataFilterIntoMap(IndicatorFilter indicatorFilter) {
        Map<String, Object> result = new HashMap<>();
        List<String> regionFilter = checkListData(indicatorFilter.getRegions());
        List<String> plantFilter = checkListData(indicatorFilter.getPlants());
        List<String> metaSeriesFilter = checkListData(indicatorFilter.getMetaSeries());
        List<String> classFilter = checkListData(indicatorFilter.getClasses());
        List<String> modelFilter = checkListData(indicatorFilter.getModels());
        Boolean ChineseBrandFilter = checkBooleanData(indicatorFilter.getChineseBrand());
        String aopMarginPercentageFilter = checkStringData(indicatorFilter.getAopMarginPercentageGroup());
        Pageable pageable = PageRequest.of(indicatorFilter.getPageNo() == 0 ? indicatorFilter.getPageNo() : indicatorFilter.getPageNo() - 1, indicatorFilter.getPerPage() == 0 ? 100 : indicatorFilter.getPerPage());
        result.put("regionFilter", regionFilter);
        result.put("plantFilter", plantFilter);
        result.put("metaSeriesFilter", metaSeriesFilter);
        result.put("classFilter", classFilter);
        result.put("modelFilter", modelFilter);
        result.put("ChineseBrandFilter", ChineseBrandFilter);
        result.put("aopMarginPercentageFilter", aopMarginPercentageFilter);
        result.put("pageable", pageable);
        return result;
    }

    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByRegion(IndicatorFilter indicatorFilter) {
        logInfo(indicatorFilter.toString());
        Map<String, Object> filterMap = loadDataFilterIntoMap(indicatorFilter);
        List<CompetitorPricing> result = competitorPricingRepository.findCompetitorByFilterForLineChartRegion(
                (List<String>) filterMap.get("regionFilter"), (List<String>) filterMap.get("plantFilter"), (List<String>) filterMap.get("metaSeriesFilter"),
                (List<String>) filterMap.get("classFilter"), (List<String>) filterMap.get("modelFilter"), (Boolean) filterMap.get("ChineseBrandFilter"),
                (String) filterMap.get("aopMarginPercentageFilter"));
        return result;
    }

    private List<String> checkListData(List<String> data) {
        return data == null || data.isEmpty() ? null : data;
    }

    private String checkStringData(String data) {
        return data == null || data.isEmpty() ? null : data;
    }

    private Boolean checkBooleanData(String data) {
        if (data == null)
            return null;
        return data.equals("Chinese Brand");
    }

    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByPlant(IndicatorFilter indicatorFilter) {
        logInfo(indicatorFilter.toString());
        Map<String, Object> filterMap = loadDataFilterIntoMap(indicatorFilter);
        List<CompetitorPricing> result = competitorPricingRepository.findCompetitorByFilterForLineChartPlant(
                (List<String>) filterMap.get("regionFilter"), (List<String>) filterMap.get("plantFilter"), (List<String>) filterMap.get("metaSeriesFilter"),
                (List<String>) filterMap.get("classFilter"), (List<String>) filterMap.get("modelFilter"), (Boolean) filterMap.get("ChineseBrandFilter"),
                (String) filterMap.get("aopMarginPercentageFilter"));
        return result;
    }

    public List<CompetitorPricing> getCompetitiveLandscape(String country, String clazz, String category, String series) {
        return competitorPricingRepository.getListOfCompetitorInGroup(country, clazz, category, series);

    }
}
