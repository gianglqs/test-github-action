package com.hysteryale.model_h2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Getter
@Setter
@DiscriminatorValue("in_memory")
public class IMMarginAnalystData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MarginAnalystSeq")
    private int id;
    private String plant;

    @Column(name = "model_code")
    private String modelCode;

    @Column(name = "price_list_region")
    private String priceListRegion;
    private String class_;

    @Column(name = "option_code")
    private String optionCode;
    private String std_opt;
    private String description;

    @Column(name = "list_price")
    private double listPrice;
    private double margin_aop;

    @Temporal(TemporalType.DATE)
    @Column(name = "month_year")
    private Calendar monthYear; // we only needs to care month and year, so the day is always 1

    private String currency;
    private double costRMB;
    private String dealer;  // equivalent with billTo in Part

    @Column(name = "dealer_net")
    private double dealerNet;

    private String fileUUID;
}
