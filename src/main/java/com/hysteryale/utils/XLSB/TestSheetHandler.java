package com.hysteryale.utils.XLSB;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    @Getter
    private final Sheet sheet  = new Sheet();
    private List<Row> rowList;
    private List<Cell> cellList;

    public void startSheet() {
        rowList = new ArrayList<>();
    }
    public void endSheet(String sheetName) {
        sheet.setSheetName(sheetName);
        sheet.setRowList(rowList);
    }

    @Override
    public void startRow(int rowNum) {
        cellList = new ArrayList<>();
    }

    @Override
    public void endRow(int rowNum) {
        Row row = new Row(cellList);
        rowList.add(row);
    }

    @Override
    public void cell(String cell, String formattedValue, XSSFComment comment) {
        formattedValue = (formattedValue == null) ? "" : formattedValue;
        Cell xlsbCell = new Cell(formattedValue);
        cellList.add(xlsbCell);
    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {

    }
}
