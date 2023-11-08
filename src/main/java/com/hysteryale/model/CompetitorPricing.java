package com.hysteryale.model;

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
    private String plant;
    private String competitorName;
    private String clazz;
    private String series;
    private Double AverageDN;
    @Column(nullable = true)
    private Double actual;
    @Column(nullable = true)
    private Double AOPF;
    @Column(nullable = true)
    private Double LRFF;
    private Double HYGLeadTime;
    private Double competitorLeadTime;
    private Double dealerStreetPricing;
    private Double dealerHandlingCost;
    private Double dealerPricingPremiumPerMarginPercent;
    private Double competitorPricing;
    // varian % (competitor - (Dealer street + premium))
    private Double varianPercent;

    public CompetitorPricing(String region, String clazz, Double HYGLeadTime, String series) {
        this.region = region;
        this.clazz = clazz;
        this.series = series;
        this.HYGLeadTime = HYGLeadTime;
    }

    public CompetitorPricing(String region, String clazz, Double HYGLeadTime) {
        this.region = region;
        this.clazz = clazz;
        this.HYGLeadTime = HYGLeadTime;
    }


}
