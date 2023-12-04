package com.hysteryale.utils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
    public static HashMap<String, Integer> monthMap = new HashMap<>() {{
        put("Jan", 0);
        put("Feb", 1);
        put("Mar", 2);
        put("Apr", 3);
        put("May", 4);
        put("Jun", 5);
        put("Jul", 6);
        put("Aug", 7);
        put("Sep", 8);
        put("Oct", 9);
        put("Nov", 10);
        put("Dec", 11);
    }};

    public static List<String> monthList(){
        String[] monthArr = {"Apr", "Feb", "Jan", "May", "Aug", "Jul", "Jun", "Mar", "Sep", "Oct", "Nov", "Dec"};
        List<String> listMonth = Arrays.asList(monthArr);
        return listMonth;
    }

    public static Date extractDate(String fileName) {
        String dateRegex = "\\d{2}_\\d{2}_\\d{4}";
        Matcher m = Pattern.compile(dateRegex).matcher(fileName);
        Date date = null;
        try {
            if (m.find()) {
                date = new SimpleDateFormat("MM_dd_yyyy").parse(m.group());
                date.setMonth(date.getMonth() - 1); //TODO recheck month of file
            } else {
                dateRegex = "\\d{4}";
                m = Pattern.compile(dateRegex).matcher(fileName);
                if(m.find()){
                    int year = Integer.parseInt(m.group());
                    int month = 0;
                    for(String mon : monthList()){
                        if(fileName.contains(mon)){
                            month = monthList().indexOf(mon);
                        }
                    }
                    date = new Date(year, month, 1);
                }
            }

        } catch (java.text.ParseException e) {

        }
        return date;
    }
}
