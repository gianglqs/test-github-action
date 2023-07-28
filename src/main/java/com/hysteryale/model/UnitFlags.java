package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private String createdDate;
    private String cancelled;


}
