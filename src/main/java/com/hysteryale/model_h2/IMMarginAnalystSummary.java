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

    private double totalManufacturingCost;
    private double costUplift;
    private double addWarranty;
    private double surcharge;
    private double duty;
    private double freight;
    private boolean liIonIncluded;
    private double totalCost;

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

    private double manufacturingCostUSD;
    private double warrantyCost;
    private double surchargeCost;
    private double dutyCost;
    private double totalCostWithoutFreight;
    private double totalCostWithFreight;

    private double manufacturingCostAop;
    private double manufacturingCostMonthly;

    private String fileUUID;
}
