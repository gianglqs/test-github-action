package com.hysteryale.model_h2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Getter
@Setter
public class IMMarginAnalystData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MarginAnalystSeq")
    private int id;
    private String plant;

    @Column(name = "model_code")
    private String modelCode;

    @Column(name = "price_list_region")
    private String priceListRegion;
    private String clazz;

    @Column(name = "option_code")
    private String optionCode;

    @Column(name = "std_otp")
    private String std_opt;
    private String description;

    @Column(name = "list_price")
    private double listPrice;
    @Column(name = "margin_aop")
    private double margin_aop;

    @Temporal(TemporalType.DATE)
    @Column(name = "month_year")
    private Calendar monthYear; // we only needs to care month and year, so the day is always 1

    private String currency;
    private double manufacturingCost;
    private String dealer;  // equivalent with billTo in Part

    @Column(name = "dealer_net")
    private double dealerNet;

    private String fileUUID;
    private String orderNumber;
    private boolean isSPED;
}
