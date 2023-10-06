package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AOPMargin {
    @Id
    private String regionSeriesPlant;
    private String description;
    private double dnUSD;
    private double marginSTD;
    private int year;
    private String plant;
    private String series;
    private String region;

}
