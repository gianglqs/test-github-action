package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateMaster {
    @Id
    private Date date;
    private int year;
    private int currYearOffset;
    private boolean yearCompleted;
    private int quarterNumber;
    private String quarter;
    private Date startOfQuarter;
    private Date endOfQuarter;
    private String quarterAndYear;
    private int quarterNYear;
    private int currQuarterOffset;
    private boolean quarterCompleted;
    private int month;
    private Date startOfMonth;
    private Date endOfMonth;
    private String monthAndYear;
    private int monthNYear;
    private int currMonthOffset;
    private boolean monthCompleted;
    private String monthName;
    private String monthShort;
    private String monthInitial;
    private int dayOfMonth;
    private int weekNumber;
    private Date startOfWeek;
    private Date endOfWeek;
    private String weekAndYear;
    private int weekNYear;
    private int currWeekOffset;
    private boolean weekCompleted;
    private int dayOfWeekNumber;
    private String dayOfWeekName;
    private String dayOfWeekInitial;
    private int dateInt;
    private int currDayOffset;
    private boolean isAfterToday;
    private boolean isWeekDay;
    private String isHoliday;
    private boolean isBusinessDay;
    private String dayType;
    private int isoYear;
    private int isoCurrYearOffset;
    private String isoQuarter;
    private String isoQuarterAndYear;
    private int isoQuarterNYear;
    private int isoCurrQuarterOffset;
    private String fiscalYear;
    private int fiscalCurrYearOffset;
    private String fiscalQuarter;
    private int fQuarterNYear;
    private String fiscalPeriod;
    private String fPeriodNYear;
    private String fiscalWeek;
    private int fWeekNYear;
    private boolean isCurrentFY;
    private boolean isCurrentFQ;
    private boolean isCurrentFP;
    private boolean isCurrentFW;
    private boolean isPYTD;
    private boolean isPFYTD;
}
