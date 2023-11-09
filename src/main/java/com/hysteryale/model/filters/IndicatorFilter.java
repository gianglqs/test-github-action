package com.hysteryale.model.filters;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IndicatorFilter {

    private List<String> regions;
    private List<String> dealers;
    private List<String> plants;
    private List<String> metaSeries;
    private List<String> classes;
    private List<String> models;
    private Boolean isChinese;
    private String AOPMarginPercentageGroup;
    private String limit;
    private int perPage;
    private int pageNo;

}
