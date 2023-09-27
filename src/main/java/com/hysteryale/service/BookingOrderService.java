package com.hysteryale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hysteryale.model.APACSerial;
import com.hysteryale.model.APICDealer;
import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.filters.BookingOrderFilter;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.repository.bookingorder.CustomBookingOrderRepository;
import com.hysteryale.utils.FileUtils;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BookingOrderService {
    private final HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();
    @Resource
    BookingOrderRepository bookingOrderRepository;
    @Resource
    APACSerialService apacSerialService;
    @Resource
    APICDealerService apicDealerService;
    @Resource
    MetaSeriesService metaSeriesService;
    @Resource
    CustomBookingOrderRepository customBookingOrderRepository;

    /**
     * Get Columns' name in Booking Excel file, then store them (columns' name) respectively with the index into HashMap
     *
     * @param row which contains columns' name
     */
    public void getOrderColumnsName(Row row) {
        for (int i = 0; i < 17; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            ORDER_COLUMNS_NAME.put(columnName, i);
        }
        log.info("Order Columns: " + ORDER_COLUMNS_NAME);
    }


    /**
     * Map data in Excel file into each Order object
     *
     * @param row which is the row contains data
     * @return new Order object
     */
    public BookingOrder mapExcelDataIntoOrderObject(Row row) throws IllegalAccessException {
        BookingOrder bookingOrder = new BookingOrder();
        Class<? extends BookingOrder> bookingOrderClass = bookingOrder.getClass();
        Field[] fields = bookingOrderClass.getDeclaredFields();

        for (Field field : fields) {
            // String key of column's name
            String hashMapKey = field.getName().toUpperCase();
            // Get the data type of the field
            String fieldType = field.getType().getName();

            // Currency column is the only one which is not uppercase all character
            if (field.getName().equals("currency"))
                hashMapKey = "Currency";

            // allow assigning value for object's fields
            field.setAccessible(true);
            if (field.getName().equals("apacSerial")) {
                try {
                    field.setAccessible(true);
                    APACSerial apacSerial =
                            apacSerialService.getAPACSerialByModel(row.getCell(ORDER_COLUMNS_NAME.get("MODEL"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    field.set(bookingOrder, apacSerial);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            } else if (field.getName().equals("billTo")) {
                try {
                    field.setAccessible(true);
                    APICDealer apicDealer =
                            apicDealerService.getAPICDealerByBillToCode(row.getCell(ORDER_COLUMNS_NAME.get("BILLTO"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    field.set(bookingOrder, apicDealer);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            } else {
                Object index = ORDER_COLUMNS_NAME.get(hashMapKey);

                if (index != null) {  // cell will be null when the properties are not mapped with the excel files, they are used to calculate values

                    Cell cell = row.getCell((Integer) index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);


                    if (cell != null) {
                        switch (fieldType) {
                            case "java.lang.String":
                                log.info("Cell column " + cell.getColumnIndex() + " row " + cell.getRowIndex() + " " + cell.getStringCellValue());
                                field.set(bookingOrder, cell.getStringCellValue());
                                break;
                            case "int":
                                if (cell.getCellType().equals(CellType.STRING.getCode())) {
                                    log.info("Cell column " + cell.getColumnIndex() + " row " + cell.getRowIndex() + " " + cell.getStringCellValue());
                                    field.set(bookingOrder, Integer.parseInt(cell.getStringCellValue()));
                                } else if (cell.getCellType().equals(CellType.NUMERIC.getCode())) {
                                    log.info("Cell column " + cell.getColumnIndex() + " row " + cell.getRowIndex() + " " + cell.getStringCellValue());
                                    field.set(bookingOrder, (int) cell.getNumericCellValue());
                                }

                                break;
                            case "java.util.Calendar":
                                String strDate = String.valueOf(row.getCell(ORDER_COLUMNS_NAME.get("DATE")).getNumericCellValue());

                                // Cast into GregorianCalendar
                                // Create matcher with pattern {(1)_year(2)_month(2)_day(2)} as 1230404
                                Pattern pattern = Pattern.compile("^\\d(\\d\\d)(\\d\\d)(\\d\\d)");
                                Matcher matcher = pattern.matcher(strDate);
                                int year, month, day;

                                if (matcher.find()) {
                                    year = Integer.parseInt(matcher.group(1)) + 2000;
                                    month = Integer.parseInt(matcher.group(2));
                                    day = Integer.parseInt(matcher.group(3));

                                    GregorianCalendar orderDate = new GregorianCalendar();

                                    // {month - 1} is the index to get value in List of month {Jan, Feb, March, April, May, ...}
                                    orderDate.set(year, month - 1, day);
                                    field.set(bookingOrder, orderDate);
                                }
                                break;
                        }
                    }
                }
            }
        }
        return bookingOrder;
    }

    /**
     * Read booking data in Excel files then import to the database
     *
     * @throws FileNotFoundException
     * @throws IllegalAccessException
     */
    public void importOrder() throws IOException, IllegalAccessException, java.text.ParseException {

        // Folder contains Excel file of Booking Order
        String folderPath = "import_files/booking";
        // Get files in Folder Path
        List<String> fileList = FileUtils.getAllFilesInFolderWithPattern(folderPath, Pattern.compile("^(01. Bookings Register).*(.xlsx)$"));

        for (String fileName : fileList) {
            log.info("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(folderPath + "/" + fileName);

            XSSFWorkbook workbook = new XSSFWorkbook(is);

            List<BookingOrder> bookingOrderList = new ArrayList<>();

            Sheet orderSheet = workbook.getSheet("Input - Bookings");
            for (Row row : orderSheet) {
                if (row.getRowNum() == 1)
                    getOrderColumnsName(row);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()
                        && row.getRowNum() > 1) {
                    BookingOrder bookingOrder = mapExcelDataIntoOrderObject(row);
                    bookingOrderList.add(bookingOrder);
                }
            }

            bookingOrderRepository.saveAll(bookingOrderList);
            log.info("End importing file: '" + fileName + "'");
            log.info(bookingOrderList.size() + " Booking Order updated or newly saved }");
            bookingOrderList.clear();
        }
    }

    public List<BookingOrder> getAllBookingOrders() {
        return bookingOrderRepository.findAll();
    }

    /**
     * Get BookingOrder based to filters
     */
    public Map<String, Object> getBookingOrdersByFilters(BookingOrderFilter bookingOrderFilter, int pageNo, int perPage) throws ParseException, JsonProcessingException, java.text.ParseException {

        // Use ObjectMapper to Map JSONObject value into List<String
        ObjectMapper mapper = new ObjectMapper();
        String orderNo = bookingOrderFilter.getOrderNo();


        // Parse all filters into ArrayList<String>
        List<String> regions = bookingOrderFilter.getRegions();
        List<String> dealers = bookingOrderFilter.getDealers();
        List<String> plants = bookingOrderFilter.getPlants();
        List<String> metaSeries = bookingOrderFilter.getMetaSeries();
        List<String> classes = bookingOrderFilter.getClasses();
        List<String> models = bookingOrderFilter.getModels();
        List<String> segments = bookingOrderFilter.getSegments();

        // Get from DATE to DATE
        String strFromDate = bookingOrderFilter.getStrFromDate();
        String strToDate = bookingOrderFilter.getStrToDate();

        // offSet for pagination
        int offSet = pageNo * perPage;

        // Create Map of BookingOrders based on filters and pagination
        // And totalItems without paging
        Map<String, Object> bookingOrdersPage = new HashMap<>();
        bookingOrdersPage.put("bookingOrdersList", customBookingOrderRepository.getBookingOrdersByFiltersByPage(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, perPage, offSet));
        bookingOrdersPage.put("totalItems", getNumberOfBookingOrderByFilters(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate));

        return bookingOrdersPage;
    }

    /**
     * Get number of BookingOrders returned by filters
     */
    public long getNumberOfBookingOrderByFilters(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate) throws java.text.ParseException {
        return customBookingOrderRepository.getNumberOfBookingOrderByFilters(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate);
    }
}
