package com.hysteryale.model.marginAnalyst;

import com.hysteryale.model.Currency;

import java.util.Date;

public class MarginAnalystData {

    private String plant;
    private String modelCode;
    private String priceListRegion;
    private String class_;
    private String optionCode;
    private String std_opt;
    private String description;
    private double listPrice;
    private double margin_aop;
    private Date month_year; // we only needs to care month and year, so the day is always 1
    private Currency currency;

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getPriceListRegion() {
        return priceListRegion;
    }

    public void setPriceListRegion(String priceListRegion) {
        this.priceListRegion = priceListRegion;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }

    public String getStd_opt() {
        return std_opt;
    }

    public void setStd_opt(String std_opt) {
        this.std_opt = std_opt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public double getMargin_aop() {
        return margin_aop;
    }

    public void setMargin_aop(double margin_aop) {
        this.margin_aop = margin_aop;
    }

    public Date getMonth_year() {
        return month_year;
    }

    public void setMonth_year(Date month_year) {
        this.month_year = month_year;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
