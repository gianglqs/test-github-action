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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cost_uplift")
public class CostUplift {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "costUpliftSeq")
    private int id;
    private String plant;

    @Column(name = "cost_uplift")
    private double costUplift;
    @Temporal(TemporalType.DATE)
    private Calendar date;

}
