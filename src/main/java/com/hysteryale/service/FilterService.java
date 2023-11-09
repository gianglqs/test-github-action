package com.hysteryale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hysteryale.model.filters.BookingOrderFilter;
import com.hysteryale.model.filters.IndicatorFilter;
import com.hysteryale.repository.CompetitorPricingRepository;
import com.hysteryale.repository.ProductDimensionRepository;
import com.hysteryale.repository.RegionRepository;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FilterService {

    @Resource
    CompetitorPricingRepository competitorPricingRepository;

    @Resource
    ProductDimensionRepository productDimensionRepository;

    @Resource
    RegionRepository regionRepository;

    public Map<String, Object> getCompetitorPricingFilter() {

        Map<String, Object> filters = new HashMap<>();
        filters.put("classes", getAllClasses());
        filters.put("plants", getAllPlants());
        filters.put("metaSeries", getAllMetaSeries());
        filters.put("models", getAllModels());
        filters.put("chineseBrands", getChineseBrandFilter());
        filters.put("marginPercentageGrouping", getMarginPercentageForCompetitorPricing());
        //  filters.put("T&C", getTCForCompetitorPricing());
        filters.put("regions", getAllRegions());
        filters.put("dealers", null);

        return filters;
    }


    private List<Map<String, String>> getChineseBrandFilter() {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> nonChinese = new HashMap<>();
        nonChinese.put("value", "Non Chinese Brand");
        result.add(nonChinese);

        Map<String, String> Chinese = new HashMap<>();
        Chinese.put("value", "Chinese Brand");
        result.add(Chinese);

        return result;
    }

    private List<Map<String, String>> getAllClasses() {
        List<Map<String, String>> classMap = new ArrayList<>();
        List<String> classes = productDimensionRepository.getAllClass();
        classes.sort(String::compareTo);
        for (String m : classes) {
            Map<String, String> mMap = new HashMap<>();
            mMap.put("value", m);
            classMap.add(mMap);
        }
        return classMap;
    }

    private List<Map<String, String>> getAllPlants() {
        List<Map<String, String>> plantListMap = new ArrayList<>();
        List<String> plants = productDimensionRepository.getPlants();
        plants.sort(String::compareTo);
        for (String p : plants) {
            Map<String, String> pMap = new HashMap<>();
            pMap.put("value", p);

            plantListMap.add(pMap);
        }
        return plantListMap;
    }

    private List<Map<String, String>> getAllMetaSeries() {
        List<Map<String, String>> metaSeriesMap = new ArrayList<>();
        List<String> metaSeries = productDimensionRepository.getAllMetaSeries();
        metaSeries.sort(String::compareTo);
        for (String m : metaSeries) {
            Map<String, String> mMap = new HashMap<>();
            mMap.put("value", m);
            metaSeriesMap.add(mMap);
        }
        return metaSeriesMap;
    }

    private List<Map<String, String>> getAllModels() {
        List<Map<String, String>> result = new ArrayList<>();
        List<String> modelList = productDimensionRepository.getAllModel();
        modelList.sort(String::compareTo);
        for (String model : modelList) {
            Map<String, String> map = new HashMap<>();
            map.put("value", model);
            result.add(map);
        }
        return result;
    }

    private List<Map<String, String>> getAllSegments() {
        List<Map<String, String>> segmentMap = new ArrayList<>();
        List<String> segments = productDimensionRepository.getAllSegments();
        segments.sort(String::compareTo);
        for (String m : segments) {
            Map<String, String> mMap = new HashMap<>();
            mMap.put("value", m);
            segmentMap.add(mMap);
        }
        return segmentMap;
    }

    /**
     * Get margin Percentage
     */
    private List<Map<String, String>> getMarginPercentageForCompetitorPricing() {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> MarginBelow10 = new HashMap<>();
        MarginBelow10.put("value", "<10% Margin");
        result.add(MarginBelow10);

        Map<String, String> MarginBelow20 = new HashMap<>();
        MarginBelow20.put("value", "<20% Margin");
        result.add(MarginBelow20);

        Map<String, String> MarginBelow30 = new HashMap<>();
        MarginBelow30.put("value", "<30% Margin");
        result.add(MarginBelow30);

        Map<String, String> MarginAbove30 = new HashMap<>();
        MarginAbove30.put("value", ">=30% Margin");
        result.add(MarginAbove30);

        Map<String, String> MarginVE = new HashMap<>();
        MarginVE.put("value", "-ve Margin %");
        result.add(MarginVE);

        return result;
    }

    private List<Map<String, String>> getTCForCompetitorPricing() {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> on = new HashMap<>();
        on.put("value", "On");
        result.add(on);

        Map<String, String> off = new HashMap<>();
        off.put("value", "Off");
        result.add(off);

        return result;
    }


    private List<Map<String, String>> getAllRegions() {
        List<Map<String, String>> listRegion = new ArrayList<>();
        List<String> regions = regionRepository.findAllRegion();
        regions.sort(String::compareTo);
        for (String region : regions) {
            Map<String, String> mapRegion = new HashMap<>();
            mapRegion.put("value", region);
            listRegion.add(mapRegion);
        }
        return listRegion;
    }


}
