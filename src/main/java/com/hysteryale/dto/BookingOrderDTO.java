package com.hysteryale.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;

@Getter
@Setter
@AllArgsConstructor
public class BookingOrderDTO {
    private String orderNo;
    private Calendar date;
    private String currency;
    private String orderType;
    private String region;
    private String ctryCode;
    private String dealerPO;
    private String dealerName;
    private String comment;
    private String Series;
    private String billTo;
    private String model;
    private String truckClass;
    private String clazz;

    //properties that we need to calculate based on raw data
    private int quantity = 1;
    private double totalCost;
    private double dealerNet;
    private double dealerNetAfterSurCharge;
    private double marginAfterSurCharge;
    private double marginPercentageAfterSurCharge;
    private double AOPMarginPercentage;
}
