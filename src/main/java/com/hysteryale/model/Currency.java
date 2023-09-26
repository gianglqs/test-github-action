package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    @Id
    @SequenceGenerator(name = "currencySeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currencySeq")
    private int id;
    private String currency;
    private String currencyName;

    public Currency(String currency,String currencyName){
        this.currency = currency;
        this.currencyName = currencyName;
    }

}
