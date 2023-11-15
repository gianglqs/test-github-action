package com.hysteryale.model.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class OrderFilter {

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
    private String AOPMarginPercetage;
    private String MarginPercetage;
    private int pageNo;
    private int perPage;

    public OrderFilter(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate) {
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

    public OrderFilter(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String marginPercetage) {
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
        this.AOPMarginPercetage = AOPMarginPercetage;
        MarginPercetage = marginPercetage;
    }

}
