package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.ProductDimension;
import com.hysteryale.model.Region;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.utils.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImportOldCostDataService extends BasedService {

    @Resource
    private BookingOrderRepository bookingOrderRepository;

    public void getOrderColumnsName(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) {
        for (int i = 0; i < 50; i++) {
            if (row.getCell(i) != null) {
                String columnName = row.getCell(i).getStringCellValue().trim();
                if (ORDER_COLUMNS_NAME.containsKey(columnName))
                    continue;
                ORDER_COLUMNS_NAME.put(columnName, i);
            }
        }
    }

    /**
     * Get all files having name starting with {01. Bookings Register} and ending with {.xlsx}
     *
     * @param folderPath path to folder contains Booking Order
     * @return list of files' name
     */
    public List<String> getAllFilesInFolder(String folderPath) {
        Pattern pattern = Pattern.compile("^Cost_Data.*(.xlsx)$");

        List<String> fileList = new ArrayList<>();
        Matcher matcher;
        try {
            DirectoryStream<Path> folder = Files.newDirectoryStream(Paths.get(folderPath));
            for (Path path : folder) {
                matcher = pattern.matcher(path.getFileName().toString());
                if (matcher.matches())
                    fileList.add(path.getFileName().toString());
                else
                    logError("Wrong formatted file's name: " + path.getFileName().toString());
            }
        } catch (Exception e) {
            logInfo(e.getMessage());

        }
        return fileList;
    }


    public void importOldCost() throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.total-cost");

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath);
        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }

            log.info("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(pathFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);

            HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();

            Sheet orderSheet = workbook.getSheet("Cost Data");

            for (Row row : orderSheet) {
                if (row.getRowNum() == 0) getOrderColumnsName(row, ORDER_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 0) {
                    Cell orderNoCell = row.getCell(ORDER_COLUMNS_NAME.get("Order"));
                    if (orderNoCell != null && orderNoCell.getCellType() == CellType.STRING) {
                        Optional<BookingOrder> optionalBookingOrder = bookingOrderRepository.findById(orderNoCell.getStringCellValue());
                        if (optionalBookingOrder.isPresent()) {
                            Cell totalCostCell = row.getCell(ORDER_COLUMNS_NAME.get("TOTAL MFG COST Going-To"));
                            BookingOrder bookingOrder = optionalBookingOrder.get();
                            if (totalCostCell.getCellType() == CellType.NUMERIC) {
                                bookingOrder.setTotalCost(totalCostCell.getNumericCellValue());
                            } else {
                                bookingOrder.setTotalCost(Double.parseDouble(totalCostCell.getStringCellValue()));
                            }
                            // caculate
                            //MarginAfterSurchage
                            double marginAfterSurcharge = bookingOrder.getDealerNetAfterSurCharge() - bookingOrder.getTotalCost();
                            // marginPercentAfterSurcharge
                            double marginPercentAfterSurcharge = marginAfterSurcharge / bookingOrder.getDealerNetAfterSurCharge();
                            bookingOrder.setMarginAfterSurCharge(marginAfterSurcharge);
                            bookingOrder.setMarginPercentageAfterSurCharge(marginPercentAfterSurcharge);
                            bookingOrderRepository.save(bookingOrder);
                        }
                    }
                }
            }



            // logInfo("End importing file: '" + fileName + "'");
            //    updateStateImportFile(pathFile);
            updateStateImportFile(pathFile);
        }

    }
}
