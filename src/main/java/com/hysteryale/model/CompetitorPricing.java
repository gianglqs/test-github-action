package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitorPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String region;
    private String plant;
    private String competitorName;
    private String clazz;
    private String series;
    private double AverageDN;
    private double actual;
    private double AOPF;
    private double LRFF;
    private double HYGLeadTime;
    private double competitorLeadTime;
    private double dealerStreetPricing;
    private double dealerHandlingCost;
    private double dealerPricingPremiumPerMarginPercent;
    private double competitorPricing;
    // varian % (competitor - (Dealer street + premium))
    private double varianPercent;

    public CompetitorPricing(String region, String clazz, double HYGLeadTime, String series, double actual, double AOPF, double LRFF) {
        this.region = region;
        this.clazz = clazz;
        this.series = series;
        this.actual = actual;
        this.AOPF = AOPF;
        this.LRFF = LRFF;
        this.HYGLeadTime = HYGLeadTime;
    }

    public CompetitorPricing(String region, String clazz, double HYGLeadTime, double actual, double AOPF, double LRFF) {
        this.region = region;
        this.clazz = clazz;
        this.actual = actual;
        this.AOPF = AOPF;
        this.LRFF = LRFF;
        this.HYGLeadTime = HYGLeadTime;
    }
}
