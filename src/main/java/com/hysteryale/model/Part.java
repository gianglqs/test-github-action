package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partSequence")
    private int id;

    //TODO this one is temporarily replaced by quoteId
//    @OneToOne
//    private NovoQuote novoQuote;
    private String quoteId;

    private String description;
    private int quantity;

    private String modelCode;

    //TODO this can be grouped ?
    private String series;
    private String partNumber;
    private double listPrice;

    private double discount;
    private double discountPercentage;
    private String billTo;
    private double netPriceEach;
    private double discountToCustomerPercentage;
    private double customerPrice;
    private double extendedCustomerPrice;
    private String optionType;
    private Date orderBookedDate;
    private Date orderRequestDate;
    @Temporal(TemporalType.DATE)
    private Calendar recordedTime;
    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;

    public Part(String quoteId, int quantity, String modelCode, String series, String partNumber, double listPrice, double discount, double discountPercentage, String billTo, double netPriceEach, double customerPrice, double extendedCustomerPrice, Currency currency) {
        this.quoteId = quoteId;
        this.quantity = quantity;
        this.modelCode = modelCode;
        this.series = series;
        this.partNumber = partNumber;
        this.listPrice = listPrice;
        this.discount = discount;
        this.discountPercentage = discountPercentage;
        this.billTo = billTo;
        this.netPriceEach = netPriceEach;
        this.customerPrice = customerPrice;
        this.extendedCustomerPrice = extendedCustomerPrice;
        this.currency = currency;
    }
}
