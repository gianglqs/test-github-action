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
    @ManyToOne(fetch = FetchType.EAGER)
    private APICDealer billTo;
    private String ctryCode;
    private String dealerPO;
    @ManyToOne(fetch = FetchType.EAGER)
    private APACSerial apacSerial;
    private String comment;
    private int truckClass;

    //properties that we need to calculate based on raw data
    private int quantity;
    private double totalCost;
    private double dealerNet;
    private double dealerNetAfterSurCharge;
    private double marginAfterSurCharge;
    private double marginPercentageAfterSurCharge;
    private double AOPMarginPercentage;

    @OneToOne
    private NovoQuote novoQuote;

    @Override
    public boolean equals(Object object){
        return this.orderNo == ((BookingOrder)object).getOrderNo();
    }

}
