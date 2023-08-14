package com.hysteryale.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TM1ProductRange {
    @Id
    private String metaSeries;
    private String model;
    private String familyAtt;
    private String class_wBT;
    private String brand;
    private String description;
    private String familyName;
    private String plant;
    private String europePlant;
    private String plantAP;
    private String totalAllClasses;
    private String truckType;
    private String classTotals;
    private String vTtlByPL;
    private String vClassTotal2;
    private String vClassTotal3;
    private String vEUSeries;
    private String generalSeries;
    private String hysterSeries;
    private String yaleSeries;
    private String bwModelApAust;
    private String bwModelApAsia;
    private String bwModelApPacific;
    private String apModel;
    private String segmentFamily;
    private String segment;
    private String segmentConsolidation;
    private String engFamLvl2Descr;
    private String engFamLvl2;
    private int engFamLvl1;
    private String engConsolidation;
    private String notes;
    private int sharp; //TODO: need to be explained
    private int countIf;
    private String metaSeriesWithoutA;
    private String segFamPosition;
    private String segFamPositionException;
    private String segFamPositionFinal;
}
