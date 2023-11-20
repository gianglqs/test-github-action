package com.hysteryale.utils;

import com.hysteryale.model.filters.FilterModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertDataFilterUtil {

    public static Map<String, Object> loadDataFilterIntoMap(FilterModel filterModel) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        String orderNoFilter = checkStringData(filterModel.getOrderNo());
        List<String> regionFilter = checkListData(filterModel.getRegions());
        List<String> plantFilter = checkListData(filterModel.getPlants());
        List<String> metaSeriesFilter = checkListData(filterModel.getMetaSeries());
        List<String> classFilter = checkListData(filterModel.getClasses());
        List<String> modelFilter = checkListData(filterModel.getModels());
        List<String> dealerNameFilter = checkListData(filterModel.getDealers());
        List<String> segmentFilter = checkListData(filterModel.getSegment());
        Boolean ChineseBrandFilter = checkBooleanData(filterModel.getChineseBrand());
        String aopMarginPercentageFilter = checkStringData(filterModel.getAopMarginPercentageGroup());
        String marginPercentageFilter = checkStringData(filterModel.getMarginPercentage());
        Date fromDateFilter = checkDateData(filterModel.getFromDate());
        Date toDateFilter = checkDateData(filterModel.getToDate());
        Pageable pageable = PageRequest.of(filterModel.getPageNo() == 0 ? filterModel.getPageNo() : filterModel.getPageNo() - 1, filterModel.getPerPage() == 0 ? 100 : filterModel.getPerPage());

        result.put("orderNoFilter", orderNoFilter);
        result.put("regionFilter", regionFilter);
        result.put("plantFilter", plantFilter);
        result.put("dealerNameFilter", dealerNameFilter);
        result.put("metaSeriesFilter", metaSeriesFilter);
        result.put("classFilter", classFilter);
        result.put("modelFilter", modelFilter);
        result.put("segmentFilter", segmentFilter);
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

    private static Date checkDateData(String data) throws ParseException {
        if(data == null|| data.isEmpty())
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return  formatter.parse(data);
    }
    
}
