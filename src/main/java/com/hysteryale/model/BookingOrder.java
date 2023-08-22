package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.GregorianCalendar;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingOrder {
    @Id
    private String orderNumber;
    private GregorianCalendar orderDate;
    private String currency;
    private String orderType;
    private String region;
    private int mktGroup;
    private int billTo;
    private String dealerName;
    private String countryCode;
    private String dealerPO;
    private String series;
    private String model;
    private String comment;
    private int truckClass;
}
