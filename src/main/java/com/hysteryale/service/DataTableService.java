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
public class DataTableService {
    @Resource
    CompetitorPricingRepository competitorPricingRepository;

    public Map<String, Object> getCompetitorPriceForTableByFilter(IndicatorFilter indicatorFilter) {
        System.out.println(indicatorFilter.toString());
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(indicatorFilter.getPageNo(), indicatorFilter.getPerPage());
        List<CompetitorPricing> competitorPricingList = competitorPricingRepository.findCompetitorByFilterForTable(
                indicatorFilter.getRegions() == null || indicatorFilter.getRegions().isEmpty() ? null : indicatorFilter.getRegions(),
                indicatorFilter.getPlants() == null || indicatorFilter.getPlants().isEmpty() ? null : indicatorFilter.getPlants(),
                indicatorFilter.getMetaSeries() == null || indicatorFilter.getMetaSeries().isEmpty() ? null : indicatorFilter.getMetaSeries(),
                indicatorFilter.getClasses() == null || indicatorFilter.getClasses().isEmpty() ? null : indicatorFilter.getClasses(),
                indicatorFilter.getModels() == null || indicatorFilter.getModels().isEmpty() ? null : indicatorFilter.getModels(),
                indicatorFilter.getIsChinese() == null ? null : (indicatorFilter.getIsChinese().equals("Chinese Brand")), pageable
        );
        result.put("listCompetitor", competitorPricingList);
        //get total Recode
        int totalCompetitor = competitorPricingRepository.getCountAll();
        result.put("totalItems", totalCompetitor);
        return result;
    }
}
