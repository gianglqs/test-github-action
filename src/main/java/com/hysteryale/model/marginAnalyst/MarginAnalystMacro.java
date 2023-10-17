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
@Table(name = "margin_analyst_macro")
public class MarginAnalystMacro {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String clazz;
    private double costRMB;
    private String description;

    @Column(name = "model_code")
    private String modelCode;

    @Column(name = "part_number")
    private String partNumber;
    private String plant;

    @Column(name = "price_list_region")
    private String priceListRegion;

    @Column(name = "series_code")
    private String seriesCode;

    @Column(name = "std_opt")
    private String stdOpt;
    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;

    @Column(name = "month_year")
    @Temporal(TemporalType.DATE)
    private Calendar monthYear;
}
