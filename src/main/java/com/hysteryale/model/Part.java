package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Part {

    @Id
    private String id;
    @OneToOne
    private NovoQuote novoQuote;
    private String description;
    private int quantity;

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
}
