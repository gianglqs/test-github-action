package com.hysteryale.model.marginAnalyst;

import com.hysteryale.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

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

    @Temporal(TemporalType.DATE)
    private Calendar monthYear; // we only needs to care month and year, so the day is always 1

    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;
    private double costRMB;
    private String dealer;  // equivalent with billTo in Part
}
