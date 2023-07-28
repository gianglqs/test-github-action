package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class USDPrice {
    @Id
    @SequenceGenerator(name = "usdSequence", initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usdSequence")
    private Integer id;

    private String updateAction;
    private String partNumber;
    private String customerType;
    private String brand;
    private String series;
    private String modelTruck;
    private String currency;
    private String price;
    private String soldAlonePrice;
    private String startDate;
    private String endDate;
    private String standard;

    public USDPrice(String updateAction, String partNumber, String customerType, String brand, String series, String modelTruck, String currency, String price, String soldAlonePrice, String startDate, String endDate, String standard) {
        this.updateAction = updateAction;
        this.partNumber = partNumber;
        this.customerType = customerType;
        this.brand = brand;
        this.series = series;
        this.modelTruck = modelTruck;
        this.currency = currency;
        this.price = price;
        this.soldAlonePrice = soldAlonePrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.standard = standard;
    }
}
