package com.hysteryale.utils;

import com.hysteryale.model.filters.IndicatorFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertDataFilterUtil {

    public static Map<String, Object> loadDataFilterIntoMap(IndicatorFilter indicatorFilter) {
        Map<String, Object> result = new HashMap<>();
        String orderNoFilter = checkStringData(indicatorFilter.getOrderNo());
        List<String> regionFilter = checkListData(indicatorFilter.getRegions());
        List<String> plantFilter = checkListData(indicatorFilter.getPlants());
        List<String> metaSeriesFilter = checkListData(indicatorFilter.getMetaSeries());
        List<String> classFilter = checkListData(indicatorFilter.getClasses());
        List<String> modelFilter = checkListData(indicatorFilter.getModels());
        Boolean ChineseBrandFilter = checkBooleanData(indicatorFilter.getChineseBrand());
        String aopMarginPercentageFilter = checkStringData(indicatorFilter.getAopMarginPercentageGroup());
        String marginPercentageFilter = checkStringData(indicatorFilter.getMarginPercentage());
        String fromDateFilter = checkStringData(indicatorFilter.getFromDate());
        String toDateFilter = checkStringData(indicatorFilter.getToDate());
        Pageable pageable = PageRequest.of(indicatorFilter.getPageNo() == 0 ? indicatorFilter.getPageNo() : indicatorFilter.getPageNo() - 1, indicatorFilter.getPerPage() == 0 ? 100 : indicatorFilter.getPerPage());

        result.put("orderNoFilter", orderNoFilter);
        result.put("regionFilter", regionFilter);
        result.put("plantFilter", plantFilter);
        result.put("metaSeriesFilter", metaSeriesFilter);
        result.put("classFilter", classFilter);
        result.put("modelFilter", modelFilter);
        result.put("ChineseBrandFilter", ChineseBrandFilter);
        result.put("aopMarginPercentageFilter", aopMarginPercentageFilter);
        result.put("marginPercentageFilter", marginPercentageFilter);
        result.put("fromDateFilter", fromDateFilter);
        result.put("toDateFilter", toDateFilter);
        result.put("pageable", pageable);

        return result;
    }

    private static List<String> checkListData(List<String> data) {
        return data == null || data.isEmpty() ? null : data;
    }

    private static String checkStringData(String data) {
        return data == null || data.isEmpty() ? null : data;
    }

    private static Boolean checkBooleanData(String data) {
        if (data == null)
            return null;
        return data.equals("Chinese Brand");
    }
    
}
