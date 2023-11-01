package com.hysteryale.model_h2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Getter
@Setter
public class IMMarginAnalystSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marginAnalystSummarySequence")
    private int id;

    private String modelCode;

    @Temporal(TemporalType.DATE)
    private Calendar monthYear;
    private String currency;

    private double manufacturingCostRMB;
    private double costUplift;
    private double addWarranty;
    private double surcharge;
    private double duty;
    private double freight;
    private boolean liIonIncluded;
    private double totalCostRMB;

    private double totalListPrice;
    private double blendedDiscountPercentage;
    private double dealerNet;
    private double margin;

    private double marginAopRate;
    private double fullCostAopRate;
    private double marginPercentAopRate;

    private double marginMonthlyRate;
    private double fullMonthlyRate;
    private double marginPercentMonthlyRate;

    private double manufacturingCost;
    private double warrantyCost;
    private double surchargeCost;
    private double dutyCost;
    private double totalCostWithoutFreight;
    private double totalCostWithFreight;

    private double manufacturingCostAop;
    private double manufacturingCostMonthly;

    private String orderNumber;

    private String fileUUID;
}
