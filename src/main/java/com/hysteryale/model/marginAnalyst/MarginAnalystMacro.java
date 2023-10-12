package com.hysteryale.model.marginAnalyst;

import com.hysteryale.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarginAnalystMacro {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String clazz;
    private double costRMB;
    private String description;
    private String modelCode;
    private String partNumber;
    private String plant;
    private String priceListRegion;
    private String seriesCode;
    private String stdOpt;
    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;
    @Temporal(TemporalType.DATE)
    private Calendar monthYear;
}
