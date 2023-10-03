package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingOrder {
    @Id
    private String orderNo;
    @Temporal(TemporalType.DATE)
    private Calendar date;
    private String currency;
    private String orderType;
    private String region;
    private String ctryCode;
    private String dealerPO;
    private String comment;
    private int truckClass;
    private String Series;

    //properties that we need to calculate based on raw data
    private int quantity;
    private double totalCost;
    private double dealerNet;
    private double dealerNetAfterSurCharge;
    private double marginAfterSurCharge;
    private double marginPercentageAfterSurCharge;
    private double AOPMarginPercentage;

}
