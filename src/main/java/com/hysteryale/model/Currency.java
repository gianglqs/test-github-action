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
    @SequenceGenerator(name = "currencySequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currencySequence")
    private int id;
    private String currency;
}
