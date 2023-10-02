package com.hysteryale.model.filters;

import lombok.Getter;

import java.util.List;

@Getter
public class BookingOrderFilter {

    private String orderNo;
    private List<String> regions;
    private List<String> dealers;
    private List<String> plants;
    private List<String> metaSeries;
    private List<String> classes;
    private List<String> models;
    private List<String> segments;
    private String strFromDate;
    private String strToDate;

    public BookingOrderFilter(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate) {
        this.orderNo = orderNo;
        this.regions = regions;
        this.dealers = dealers;
        this.plants = plants;
        this.metaSeries = metaSeries;
        this.classes = classes;
        this.models = models;
        this.segments = segments;
        this.strFromDate = strFromDate;
        this.strToDate = strToDate;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public void setDealers(List<String> dealers) {
        this.dealers = dealers;
    }

    public void setPlants(List<String> plants) {
        this.plants = plants;
    }

    public void setMetaSeries(List<String> metaSeries) {
        this.metaSeries = metaSeries;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public void setSegments(List<String> segments) {
        this.segments = segments;
    }

    public void setStrFromDate(String strFromDate) {
        this.strFromDate = strFromDate;
    }

    public void setStrToDate(String strToDate) {
        this.strToDate = strToDate;
    }
}
