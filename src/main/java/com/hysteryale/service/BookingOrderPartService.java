package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.BookingOrderPart;
import com.hysteryale.repository.BookingOrderPartRepository;
import com.hysteryale.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BookingOrderPartService {
    @Resource
    BookingOrderPartRepository bookingOrderPartRepository;

    private void getOrderColumnsName(HashMap<String, Integer> ORDER_COLUMNS_NAME, Row row) {
        for (int i = 0; i < 50; i++) {
            if (row.getCell(i) != null) {
                String columnName = row.getCell(i).getStringCellValue();
                ORDER_COLUMNS_NAME.put(columnName, i);
            }
        }
        log.info("Order Columns: " + ORDER_COLUMNS_NAME);
    }


    private BookingOrderPart mapExcelDataIntoOrderObject(HashMap<String, Integer> ORDER_COLUMNS_NAME, Row row) throws IllegalAccessException {
        BookingOrderPart bookingOrderPart = new BookingOrderPart();
        log.info(String.valueOf(row.getRowNum()));
        //get cell Order Number
        Cell cellOrderNo = row.getCell(ORDER_COLUMNS_NAME.get("Order Number"));

        bookingOrderPart.setOrderNo(cellOrderNo.getStringCellValue());

        //get cell Part Number
        Cell cellPart = row.getCell(ORDER_COLUMNS_NAME.get("Part Number"));
        bookingOrderPart.setPart(cellPart.getStringCellValue());

        return bookingOrderPart;
    }


    /**
     * Read booking data in Excel files then import to the database
     *
     * @throws FileNotFoundException
     * @throws IllegalAccessException
     */
    public void importBookingOrderPart() throws IOException, IllegalAccessException, java.text.ParseException {

        HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();

        // Folder contains Excel file of Booking Order
        String folderPath = "import_files/BI download history";
        // Get files in Folder Path
        List<String> fileList = FileUtils.getAllFilesInFolderWithPattern(folderPath, Pattern.compile("^(power bi).*(.xlsx)$"));

        for (String fileName : fileList) {
            log.info("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(folderPath + "/" + fileName);

            XSSFWorkbook workbook = new XSSFWorkbook(is);

            List<BookingOrderPart> bookingOrderList = new ArrayList<>();

            Sheet orderSheet = workbook.getSheet("Export");
            for (Row row : orderSheet) {
                if (row.getRowNum() == 0)
                    getOrderColumnsName(ORDER_COLUMNS_NAME, row);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()
                        && row.getRowNum() > 0) {
                    BookingOrderPart bookingOrderPart = mapExcelDataIntoOrderObject(ORDER_COLUMNS_NAME, row);
                    bookingOrderList.add(bookingOrderPart);
                }
            }

            bookingOrderPartRepository.saveAll(bookingOrderList);
            log.info("End importing file: '" + fileName + "'");
            log.info(bookingOrderList.size() + " Booking Order updated or newly saved }");
            bookingOrderList.clear();
        }
    }


}
