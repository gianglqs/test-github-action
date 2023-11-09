package com.hysteryale.model.competitor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
    private String country;
    private String plant;
    private String competitorName;
    private String clazz;
    private String category;
    private String series;
    private Double averageDN;
    private int actual;
    private int AOPF;
    private int LRFF;
    private Double HYGLeadTime;
    private Double competitorLeadTime;
    private Double competitorPricing;
    private Double dealerPremiumPercentage;
    private Double dealerStreetPricing;
    private Double dealerHandlingCost;
    private Double dealerPricingPremiumPercentage;
    private Double dealerPricingPremium;

    // variance % (competitor - (Dealer street + premium))
    private Double variancePercentage;
    private boolean isChineseBrand;
    private double dealerNet;

    public CompetitorPricing(String region, String clazz, Double competitorLeadTime, String series) {
        this.region = region;
        this.clazz = clazz;
        this.series = series;
        this.competitorLeadTime = competitorLeadTime;
    }

    public CompetitorPricing(String region, String clazz, Double competitorLeadTime) {
        this.region = region;
        this.clazz = clazz;
        this.competitorLeadTime = competitorLeadTime;
    }


}
