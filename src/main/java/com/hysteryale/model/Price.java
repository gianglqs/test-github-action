package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @Id
    @SequenceGenerator(name = "priceSequence", initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priceSequence")
    private Integer id;

    private String updateAction;
    private String partNumber;
    private String customerType;
    private String brand;
    private String series;
    private String modelTruck;
    private String currency;                // USD Price or AUD Price
    private Double price;
    private Double soldAlonePrice;
    private Date startDate;
    private Date endDate;
    private String standard;

    public Price(String updateAction, String partNumber, String customerType, String brand, String series, String modelTruck, String currency, Double price, Double soldAlonePrice, Date startDate, Date endDate, String standard) {
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
