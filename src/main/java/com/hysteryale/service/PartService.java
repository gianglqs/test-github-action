package com.hysteryale.service;

import com.hysteryale.model.Currency;
import com.hysteryale.model.Part;
import com.hysteryale.repository.PartRepository;
import com.hysteryale.utils.FileUtils;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.aspectj.weaver.ast.Literal;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PartService {
    @Resource
    PartRepository partRepository;
    @Resource
    CurrencyService currencyService;
    public static HashMap<String, Integer> monthMap = new HashMap<>();

    /**
     * Initialize HashMap for getting month -> Calendar[index]
     */
    public void initMonthMap() {
        monthMap.put("Jan", 0);
        monthMap.put("Feb", 1);
        monthMap.put("Mar", 2);
        monthMap.put("Apr", 3);
        monthMap.put("May", 4);
        monthMap.put("Jun", 5);
        monthMap.put("Jul", 6);
        monthMap.put("Aug", 7);
        monthMap.put("Sep", 8);
        monthMap.put("Oct", 9);
        monthMap.put("Nov", 10);
        monthMap.put("Dec", 11);
    }


    private static final HashMap<String, Integer> powerBIExportColumns = new HashMap<>();

    public void getPowerBiColumnsName(Row row) {
        for(Cell cell : row) {
            String columnsName = cell.getStringCellValue();
            powerBIExportColumns.put(columnsName, cell.getColumnIndex());
        }
        log.info("PowerBI Export columns: " + powerBIExportColumns);
    }

    public Part mapExcelDataToPart(Row row) {
        String strCurrency = row.getCell(powerBIExportColumns.get("Currency")).getStringCellValue().strip();
        Currency currency = currencyService.getCurrenciesByName(strCurrency);

        String quoteId = row.getCell(powerBIExportColumns.get("Quote Number")).getStringCellValue();
        int quantity = (int) row.getCell(powerBIExportColumns.get("Quoted Quantity")).getNumericCellValue();

        String series = row.getCell(powerBIExportColumns.get("Series")).getStringCellValue();
        String partNumber = row.getCell(powerBIExportColumns.get("Part Number")).getStringCellValue();
        double listPrice = row.getCell(powerBIExportColumns.get("ListPrice")).getNumericCellValue();
        String modelCode = row.getCell(powerBIExportColumns.get("Model")).getStringCellValue();

        double discountPercentage = row.getCell(powerBIExportColumns.get("Discount")).getNumericCellValue();
        double discount = listPrice * (1 - (discountPercentage / 100));
        String billTo = row.getCell(powerBIExportColumns.get("Dealer")).getStringCellValue();

        double netPriceEach = row.getCell(powerBIExportColumns.get("Net Price")).getNumericCellValue();
        // double discountToCustomerPercentage
        double customerPrice = row.getCell(powerBIExportColumns.get("Customer Price")).getNumericCellValue();
        double extendedCustomerPrice = row.getCell(powerBIExportColumns.get("Ext Customer Price")).getNumericCellValue();

        //optionType, orderBookedDate, orderRequestDate

        return new Part(quoteId, quantity, modelCode, series, partNumber, listPrice, discount, discountPercentage, billTo, netPriceEach, customerPrice, extendedCustomerPrice, currency);
    }

    /**
     * Verify if Part is existed or not
     */
    public boolean isPartExisted(Part part) {
        Optional<Part> optionalPart = partRepository.getPartForCheckingExisted(part.getModelCode(), part.getPartNumber(), part.getQuoteId(), part.getRecordedTime(), part.getCurrency().getCurrency());
        return optionalPart.isPresent();
    }

    public void importPart() throws FileNotFoundException {
        String folderPath = "import_files/bi_download";
        List<String> files = FileUtils.getAllFilesInFolder(folderPath);

        log.info("Files: " + files);

        initMonthMap();
        for(String fileName : files) {
            log.info("==== Importing " + fileName + " ====");
            InputStream is = new FileInputStream(folderPath + "/" + fileName);
            Workbook workbook = StreamingReader
                    .builder()              //setting Buffer
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(is);


            Sheet sheet = workbook.getSheet("Export");
            List<Part> partList = new ArrayList<>();

            // get recordedTime for Part
            // fileName pattern: power bi Aug 23
            Pattern pattern = Pattern.compile("\\w{4} \\w{2} (\\w{3}) (\\d{2}).xlsx");
            Matcher matcher = pattern.matcher(fileName);
            String month = "Jan";
            int year = 2023;
            if(matcher.find()) {
                month = matcher.group(1);
                year = 2000 + Integer.parseInt(matcher.group(2));

                log.info(year + " " + month);
            }
            Calendar recordedTime = Calendar.getInstance();
            recordedTime.set(year, monthMap.get(month), 1);

            for(Row row : sheet) {
                if(row.getRowNum() == 0)
                    getPowerBiColumnsName(row);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {
                    Part part = mapExcelDataToPart(row);
                    part.setRecordedTime(recordedTime);

                    // If Part have not been imported -> then add into list
                    if(!isPartExisted(part))
                        partList.add(part);
                }
            }

            partRepository.saveAll(partList);
            log.info("New parts: " + partList.size());
            log.info("End importing");
        }
    }

    public List<Part> getPartForMarginAnalysis(String modelCode, String currency, Calendar monthYear) {
        return partRepository.getPartForMarginAnalysis(modelCode, currency, monthYear);
    }
}
