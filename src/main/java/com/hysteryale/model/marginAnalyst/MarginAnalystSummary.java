package com.hysteryale.model.marginAnalyst;

import com.hysteryale.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MarginAnalystSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marginAnalystSummarySequence")
    private int id;

    private String modelCode;

    @Temporal(TemporalType.DATE)
    private Calendar monthYear;
    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;

    private double manufacturingCostRMB;
    private double costUplift;
    private double addWarranty;
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
    private double warranty;
    private double surcharge;
    private double totalCost;

    private double manufacturingCostAop;
    private double manufacturingCostMonthly;
}
