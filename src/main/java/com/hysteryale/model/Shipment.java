package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shipment")
public class Shipment {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "order_no")
    private String orderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region")
    private Region region;

    @Column(name = "dealer_name")
    private String dealerName;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency")
    private Currency currency;

    @Column(name = "ctry_code")
    private String ctryCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_dimension")
    private ProductDimension productDimension;

    private String series;

    //properties that we need to calculate based on raw data
    private int quantity = 1;

    private double netRevenue;

    @Column(name = "total_cost")
    private double totalCost;

    @Column(name = "dealer_net")
    private double dealerNet;

    @Column(name = "dealer_net_after_sur_charge")
    private double dealerNetAfterSurCharge;

    @Column(name = "margin_after_sur_charge")
    private double marginAfterSurCharge;

    @Column(name = "margin_percentage_after_sur_charge")
    private double marginPercentageAfterSurCharge;

    @Column(name = "aopmargin_percentage")
    private double AOPMarginPercentage;

}
