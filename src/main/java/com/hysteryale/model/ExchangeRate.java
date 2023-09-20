package com.hysteryale.model;

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
public class ExchangeRate {
    @Id
    @SequenceGenerator(name = "exchangeRateSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchangeRateSeq")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Currencies from;
    @ManyToOne(fetch = FetchType.EAGER)
    private Currencies to;
    private double rate;
    @Temporal(TemporalType.DATE)
    private Calendar date;

}
