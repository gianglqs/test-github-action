package com.hysteryale.model.filters;

import java.util.List;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<String> getDealers() {
        return dealers;
    }

    public void setDealers(List<String> dealers) {
        this.dealers = dealers;
    }

    public List<String> getPlants() {
        return plants;
    }

    public void setPlants(List<String> plants) {
        this.plants = plants;
    }

    public List<String> getMetaSeries() {
        return metaSeries;
    }

    public void setMetaSeries(List<String> metaSeries) {
        this.metaSeries = metaSeries;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public List<String> getSegments() {
        return segments;
    }

    public void setSegments(List<String> segments) {
        this.segments = segments;
    }

    public String getStrFromDate() {
        return strFromDate;
    }

    public void setStrFromDate(String strFromDate) {
        this.strFromDate = strFromDate;
    }

    public String getStrToDate() {
        return strToDate;
    }

    public void setStrToDate(String strToDate) {
        this.strToDate = strToDate;
    }
}
