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
    private String orderNo;
    private GregorianCalendar date;
    private String currency;
    private String orderType;
    private String region;
    private int mktGrp;
    private int billTo;
    private String dealerName;
    private String ctryCode;
    private String dealerPO;
    private String series;
    private String model;
    private String comment;
    private int truckClass;
}
