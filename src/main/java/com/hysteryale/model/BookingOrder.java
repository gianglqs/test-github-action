package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingOrder implements Serializable {
    @Id
    private String orderNo;
    @Temporal(TemporalType.DATE)
    private Calendar date;
    private String currency;
    private String orderType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region")
    private Region region;
    private String ctryCode;
    private String dealerPO;
    private String dealerName;
    private String comment;
    private String series;
    private String billTo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "apac_serial")
    private APACSerial apacSerial;
    private String model;
    private String truckClass;


    //properties that we need to calculate based on raw data
    private int quantity = 1;
    private double totalCost;
    private double dealerNet;
    private double dealerNetAfterSurCharge;
    private double marginAfterSurCharge;
    private double marginPercentageAfterSurCharge;
    private double AOPMarginPercentage;

}
