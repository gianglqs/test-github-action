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
public class CostUplift {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "costUpliftSeq")
    private int id;
    private String plant;
    private double costUplift;
    @Temporal(TemporalType.DATE)
    private Calendar date;

}
