package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.GregorianCalendar;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitFlags {
    @Id
    private String unit;
    private String description;
    private String uClass;
    private String readyForDistribution;
    private String enableGLReadiness;
    private String fullyAttributed;
    private String readyForPartsCosting;
    private GregorianCalendar createdDate;
    private String cancelled;

}
