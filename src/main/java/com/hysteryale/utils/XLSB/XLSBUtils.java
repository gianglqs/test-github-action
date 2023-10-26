package com.hysteryale.utils.XLSB;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.binary.XSSFBSharedStringsTable;
import org.apache.poi.xssf.binary.XSSFBSheetHandler;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.eventusermodel.XSSFBReader;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class XLSBUtils {
    XSSFBReader r;
    XSSFBSharedStringsTable sst;
    XSSFBStylesTable xssfbStylesTable;
    XSSFBReader.SheetIterator it;

    public void openFile(String xlsbFilePath) throws OpenXML4JException, IOException, SAXException {
        OPCPackage pkg = OPCPackage.open(xlsbFilePath, PackageAccess.READ_WRITE);

        r = new XSSFBReader(pkg);
        sst = new XSSFBSharedStringsTable(pkg);
        xssfbStylesTable = r.getXSSFBStylesTable();
        it = (XSSFBReader.SheetIterator) r.getSheetsData();
    }
    public void getSheet(String sheetName) throws IOException {
        TestSheetHandler testSheetHandler = new TestSheetHandler();
        while (it.hasNext()) {
            InputStream is = it.next();
            if(it.getSheetName().equals(sheetName))
            {
                testSheetHandler.startSheet();
                XSSFBSheetHandler sheetHandler = new XSSFBSheetHandler(
                        is,
                        xssfbStylesTable,
                        it.getXSSFBSheetComments(),
                        sst,
                        testSheetHandler,
                        new DataFormatter(),
                        false
                );
                sheetHandler.parse();
                testSheetHandler.endSheet(sheetName);

                Sheet sheet = testSheetHandler.getSheet();
                log.info("" + sheet.getRowList().get(0).getCellList().get(0).getValue());
            }
        }
    }
}
