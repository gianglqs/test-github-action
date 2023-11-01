package com.hysteryale.utils.XLSB;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private String value;

    public double getNumericCellValue() {
        double number;
        String modifiedValue;
        if(value.contains("%")) {
            modifiedValue = value.replaceAll("[,$%]", "");
            number = Double.parseDouble(modifiedValue) / 100;
        }
        else {
            modifiedValue = value.replaceAll("[,$]", "");
            number = Double.parseDouble(modifiedValue);
        }
        return number;
    }
}
