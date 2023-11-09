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
    private Boolean chineseBrand;
    private String model;
    private long actual;
    private long AOPF;
    private long LRFF;
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


    public CompetitorPricing(String region, long actual, long AOPF, long LRFF) {
        this.region = region;
        this.actual = actual;
        this.AOPF = AOPF;
        this.LRFF = LRFF;
    }
}
