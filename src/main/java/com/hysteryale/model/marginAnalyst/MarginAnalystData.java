package com.hysteryale.model.marginAnalyst;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarginAnalystData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MarginAnalystSeq")
    private int id;
    private String plant;
    private String modelCode;
    private String priceListRegion;
    private String class_;
    private String optionCode;
    private String std_opt;
    private String description;
    private double listPrice;
    private double margin_aop;
    private Date month_year; // we only needs to care month and year, so the day is always 1
    private String currency;
}
