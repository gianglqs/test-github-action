package com.hysteryale.utils.XLSB;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Row {
    private int rowNum;
    private List<Cell> cellList;

    public Cell getCell(int cellNum) {
        return cellList.get(cellNum);
    }
}
