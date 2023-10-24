package com.hysteryale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hysteryale.model.*;
import com.hysteryale.model.filters.BookingOrderFilter;
import com.hysteryale.repository.*;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.repository.bookingorder.CustomBookingOrderRepository;
import com.hysteryale.utils.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.apache.poi.ss.usermodel.*;
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
public class BookingOrderService extends BasedService {
    @Resource
    BookingOrderRepository bookingOrderRepository;
    @Resource
    ProductDimensionService productDimensionService;
    @Resource
    APICDealerService apicDealerService;
    @Resource
    CustomBookingOrderRepository customBookingOrderRepository;

    @Resource
    AOPMarginRepository AOPMarginRepository;


    @Resource
    PartRepository partRepository;

    @Resource
    RegionRepository regionRepository;

    @Resource
    CurrencyRepository currencyRepository;

    @Resource
    ProductDimensionRepository productDimensionRepository;

    //  private final HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();

    /**
     * Get Columns' name in Booking Excel file, then store them (columns' name) respectively with the index into HashMap
     *
     * @param row which contains columns' name
     */
    public void getOrderColumnsName(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) {
        for (int i = 0; i < 50; i++) {
            if (row.getCell(i) != null) {
                String columnName = row.getCell(i).getStringCellValue();
                ORDER_COLUMNS_NAME.put(columnName, i);
            }
        }
        log.info("Order Columns: " + ORDER_COLUMNS_NAME);
    }

    /**
     * Get all files having name starting with {01. Bookings Register} and ending with {.xlsx}
     *
     * @param folderPath path to folder contains Booking Order
     * @return list of files' name
     */
    public List<String> getAllFilesInFolder(String folderPath, boolean isBooking) {
        Pattern pattern;
        if (!isBooking) {
            pattern = Pattern.compile(".*Final.*(.xlsx)$");
        } else {
            pattern = Pattern.compile("^01.*(.xlsx)$");
        }
        List<String> fileList = new ArrayList<>();
        Matcher matcher;
        try {
            DirectoryStream<Path> folder = Files.newDirectoryStream(Paths.get(folderPath));
            for (Path path : folder) {
                matcher = pattern.matcher(path.getFileName().toString());
                if (matcher.matches())
                    fileList.add(path.getFileName().toString());
                else
                    log.error("Wrong formatted file's name: " + path.getFileName().toString());
            }
        } catch (Exception e) {
            log.info(e.getMessage());

        }
        log.info("File list: " + fileList);
        return fileList;
    }

    /**
     * Map data in Excel file into each Order object
     *
     * @param row which is the row contains data
     * @return new Order object
     */
    public BookingOrder mapExcelDataIntoOrderObject(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) throws IllegalAccessException {
        BookingOrder bookingOrder = new BookingOrder();
        Class<? extends BookingOrder> bookingOrderClass = bookingOrder.getClass();
        Field[] fields = bookingOrderClass.getDeclaredFields();

        for (Field field : fields) {
            // String key of column's name
            String hashMapKey = field.getName().toUpperCase();
            // Get the data type of the field
            String fieldType = field.getType().getName();

            // Currency column is the only one which is not uppercase all character
//            if (field.getName().equals("currency")) {
//                hashMapKey = "Currency";
//                Cell cell = row.getCell(ORDER_COLUMNS_NAME.get("Currency"));
//                if (cell.getCellType() == CellType.FORMULA) {
//                    // get formula
//                    String formula = cell.getCellFormula();
//
//                    //  create evaluator formula
//                    FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
//
//                    // evaluator formula
//                    CellValue cellValue = evaluator.evaluate(cell);
//                    String result = cellValue.getStringValue();
//                    field.setAccessible(true);
//                    field.set(bookingOrder, result);
//                }
//            }

            // allow assigning value for object's fields
            field.setAccessible(true);
            if (field.getName().equals("productDimension")) {
                try {
                    ProductDimension productDimension = productDimensionService.getProductDimensionByMetaseries(row.getCell(ORDER_COLUMNS_NAME.get("SERIES"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    field.set(bookingOrder, productDimension);
                } catch (Exception e) {
                    rollbar.error(e.toString());
                    log.error(e.toString());
                }
            } else if (field.getName().equals("billTo")) {
                try {
                    Cell cell = row.getCell(ORDER_COLUMNS_NAME.get("BILLTO"));
                    field.set(bookingOrder, cell.getStringCellValue());
                    // APICDealer apicDealer = apicDealerService.getAPICDealerByBillToCode(row.getCell(ORDER_COLUMNS_NAME.get("BILLTO"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    // field.set(bookingOrder, apicDealer);
                } catch (Exception e) {
                    rollbar.error(e.toString());
                    log.error(e.toString());
                }
            } else if (field.getName().equals("model")) {
                try {
                    Cell cell = row.getCell(ORDER_COLUMNS_NAME.get("MODEL"));
                    field.set(bookingOrder, cell.getStringCellValue());

                } catch (Exception e) {
                    rollbar.error(e.toString());
                    log.error(e.toString());
                }
            } else if (field.getName().equals("region")) {
                try {
                    Cell cell = row.getCell(ORDER_COLUMNS_NAME.get("REGION"));
                    Optional<Region> region = regionRepository.findByRegionId(cell.getStringCellValue());
                    if (region.isPresent()) {
                        field.set(bookingOrder, region.get()/*.getRegion()*/);
                    } else {
                        throw new Exception("Not match Region with region_Id: " + cell.getStringCellValue());
                    }

                } catch (Exception e) {
                    rollbar.error(e.toString());
                    log.error(e.toString());
                }
            } else {
                Object index = ORDER_COLUMNS_NAME.get(hashMapKey);

                if (index != null) {  // cell will be null when the properties are not mapped with the excel files, they are used to calculate values

                    Cell cell = row.getCell((Integer) index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);


                    if (cell != null) {
                        switch (fieldType) {
                            case "java.lang.String":
                                //log.info("Cell column " + cell.getColumnIndex() + " row " + cell.getRowIndex() + " " + cell.getStringCellValue());
                                if (cell.getCellType() == CellType.STRING) {
                                    field.set(bookingOrder, cell.getStringCellValue());
                                } else if (cell.getCellType() == CellType.NUMERIC) {
                                    field.set(bookingOrder, cell.getNumericCellValue() + "");
                                }
                                break;
                            case "int":
                                if (cell.getCellType().equals(CellType.STRING.getCode())) {
                                    //   log.info("Cell column " + cell.getColumnIndex() + " row " + cell.getRowIndex() + " " + cell.getStringCellValue());
                                    field.set(bookingOrder, Integer.parseInt(cell.getStringCellValue()));
                                } else if (cell.getCellType().equals(CellType.NUMERIC.getCode())) {
                                    //  log.info("Cell column " + cell.getColumnIndex() + " row " + cell.getRowIndex() + " " + cell.getStringCellValue());
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
    public void importOrder() throws IOException, IllegalAccessException {

        // Folder contains Excel file of Booking Order
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.booked-order");

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, false);
        String[] monthArr = {"Apr", "Feb", "Jan", "May", "Aug", "Jul", "Jun", "Mar", "Sep", "Oct", "Nov", "Dec"};
        List<String> listMonth = Arrays.asList(monthArr);
        String month = "", year = "";

        for (String fileName : fileList) {
            log.info("{ Start importing file: '" + fileName + "'");
            for (String shortMonth : listMonth) {
                String yearRegex = "\\b\\d{4}\\b";
                Pattern pattern = Pattern.compile(yearRegex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    year = matcher.group();
                }
                if (fileName.toLowerCase().contains(shortMonth.toLowerCase())) {
                    month = shortMonth;
                }
            }


            InputStream is = new FileInputStream(folderPath + "/" + fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            List<BookingOrder> bookingOrderList = new LinkedList<>();
            HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();

            Sheet orderSheet = workbook.getSheet("NOPLDTA.NOPORDP,NOPLDTA.>Sheet1");
            int numRowName = 0;
            if (orderSheet == null) {
                orderSheet = workbook.getSheet("Input - Bookings");
                numRowName = 1;
            }
            for (Row row : orderSheet) {
                if (row.getRowNum() == numRowName) getOrderColumnsName(row, ORDER_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 1) {
                    BookingOrder newBookingOrder = mapExcelDataIntoOrderObject(row, ORDER_COLUMNS_NAME);
                    //  if (newBookingOrder.getMetaSeries() != null)
                    //             newBookingOrder = importPlant(newBookingOrder);
                    newBookingOrder = insertMargin(newBookingOrder, month, year);

                    newBookingOrder = calculateOrderValues(newBookingOrder);
                    bookingOrderList.add(newBookingOrder);
                }
            }

            bookingOrderRepository.saveAll(bookingOrderList);
            log.info("End importing file: '" + fileName + "'");
            log.info(bookingOrderList.size() + " Booking Order updated or newly saved }");
            bookingOrderList.clear();
        }
    }

    private BookingOrder insertMargin(BookingOrder booking, String month, String year) throws IOException, IllegalAccessException {
        // Folder contains Excel file of Booking Order
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.booking");

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, true);


        for (String fileName : fileList) {
            // Check year and month
            if (fileName.contains(year) && fileName.toLowerCase().contains(month.toLowerCase())) {
                InputStream is = new FileInputStream(folderPath + "/" + fileName);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheet("Wk - Margins");
                HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();
                for (Row row : sheet) {
                    if (row.getRowNum() == 1) getOrderColumnsName(row, ORDER_COLUMNS_NAME);
                    else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 1) {

                        Cell OrderNOCell = row.getCell(ORDER_COLUMNS_NAME.get("Order #"));
                        if (OrderNOCell.getStringCellValue().equals(booking.getOrderNo())) {

                            Cell marginCell = row.getCell(ORDER_COLUMNS_NAME.get("Margin %"));
                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                            switch (evaluator.evaluateFormulaCell(marginCell)) {
                                case NUMERIC:
                                    booking.setMarginPercentageAfterSurCharge(marginCell.getNumericCellValue());
                                    System.err.println("Margin %     " + marginCell.getNumericCellValue());
                                    break;
                                case STRING:
                                    booking.setMarginPercentageAfterSurCharge(Double.parseDouble(marginCell.getStringCellValue()));
                                    System.err.println("Margin %     " + marginCell.getNumericCellValue());
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return booking;
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

        String AOPMarginPercetage = bookingOrderFilter.getAOPMarginPercetage();
        String MarginPercetage = bookingOrderFilter.getMarginPercetage();

        // Get from DATE to DATE
        String strFromDate = bookingOrderFilter.getStrFromDate();
        String strToDate = bookingOrderFilter.getStrToDate();

        // offSet for pagination
        int offSet = pageNo * perPage;

        // Create Map of BookingOrders based on filters and pagination
        // And totalItems without paging
        Map<String, Object> bookingOrdersPage = new HashMap<>();
        bookingOrdersPage.put("bookingOrdersList", customBookingOrderRepository.getBookingOrdersByFiltersByPage(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage, perPage, offSet));
        bookingOrdersPage.put("totalItems", getNumberOfBookingOrderByFilters(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage));

        return bookingOrdersPage;
    }


    /**
     * Get number of BookingOrders returned by filters
     */
    public long getNumberOfBookingOrderByFilters(String orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage) throws java.text.ParseException {
        return customBookingOrderRepository.getNumberOfBookingOrderByFilters(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage);
    }

    /**
     * To calculate extra values of an order
     */
    private BookingOrder calculateOrderValues(BookingOrder bookingOrder) {
        //from orderId get Series
        String series = "";
        if (bookingOrder.getSeries() != null)
            series = bookingOrder.getSeries().substring(1);

        // quantity is always 1
        bookingOrder.setQuantity(1);


        Set<Part> newParts = partRepository.getPartByOrderNumber(bookingOrder.getOrderNo());

        //from orderId + Series + Part we can calculate the following
        //      total cost
        //      dealerNet
        //      dealerNetAfterSurCharge
        //      marginAfterSurCharge
        //      marginPercentageAfterSurCharge
        //      AOPMarginPercentage

        //double dealerNetAfterSurCharge = 0;

        //get margin
        Map<String, AOPMargin> aopMarginByYear = convertSetToMap(AOPMarginRepository.findByYear(bookingOrder.getDate().get(Calendar.YEAR)));

        //get AOPMargin by series
        AOPMargin aopMargin = getAOPMargin(series, aopMarginByYear);
        double marginPercent = 0;
        if (aopMargin != null) {
            marginPercent = aopMargin.getMarginSTD();
        }
        double totalCost = 0;
        double dealerNet = 0;
        //dealnet after surcharge
        double dealerNetAfterSurchage = 0;
        //margin $ after surcharge
        double marginAfterSurcharge = 0;
        double marginPercentageAfterSurcharge = bookingOrder.getMarginPercentageAfterSurCharge();


        for (Part part : newParts) {

            //dealer Net
            dealerNet = dealerNet + part.getNetPriceEach();

            //dealerNetAfterSurCharge = (part.getNetPriceEach() - part.getDiscount());


            // marginPercent = aopMargin.getMarginSTD();
            // bookingOrder.setAOPMarginPercentage(marginPercent);


//            //dealnet after surcharge
//            double dealerNetAfterSurchage = dealerNet - (dealerNet * marginPercent);
//            bookingOrder.setDealerNetAfterSurCharge(dealerNetAfterSurchage);
//
//            //margin $ after surcharge
//            double marginAfterSurcharge =totalCost- dealerNetAfterSurchage  ;
//            bookingOrder.setMarginAfterSurCharge(marginAfterSurcharge);
//
//            //margin % after surcharge
//            double marginPercentageAfterSurcharge = marginAfterSurcharge / totalCost;
//            bookingOrder.setMarginPercentageAfterSurCharge(marginPercentageAfterSurcharge);


        }

        dealerNetAfterSurchage = dealerNet;
        marginAfterSurcharge = dealerNetAfterSurchage * marginPercentageAfterSurcharge;
        totalCost = dealerNetAfterSurchage - marginAfterSurcharge;

        bookingOrder.setDealerNet(dealerNet);
        bookingOrder.setTotalCost(totalCost);
        bookingOrder.setDealerNetAfterSurCharge(dealerNetAfterSurchage);
        bookingOrder.setMarginAfterSurCharge(marginAfterSurcharge);

        bookingOrder.setAOPMarginPercentage(marginPercent);


        return bookingOrder;
    }


    private AOPMargin getAOPMargin(String series, Map<String, AOPMargin> aopMarginByYear) {
        Set<String> setAOPMargins = aopMarginByYear.keySet();
        for (String AOPMargin : setAOPMargins) {
            if (AOPMargin.contains(series)) return aopMarginByYear.get(AOPMargin);
        }
        return null;
    }

    private Map<String, AOPMargin> convertSetToMap(Set<AOPMargin> setMargin) {
        Map<String, AOPMargin> result = new HashMap<>();
        for (AOPMargin margin : setMargin) {
            result.put(margin.getRegionSeriesPlant(), margin);
        }
        return result;
    }

    private String detachSeries(String regionSeriesPlant) {
        String[] arrRegion = {"ISC", "Australia", "Pacific", "Asia", "Pacific excl AUS", "India"};
        List<String> listRegion = Arrays.asList(arrRegion);
        for (String region : listRegion) {
            if (regionSeriesPlant.startsWith(region)) {
                return regionSeriesPlant.substring(region.length(), region.length() + 3);
            }
        }
        return "";
    }


    /**
     * To create a new table OrderPart
     */


    public List<Map<String, String>> getAPOMarginPercentageForFilter() {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> APOMarginPercentageMapAbove = new HashMap<>();
        APOMarginPercentageMapAbove.put("value", "Above AOP Margin %");
        result.add(APOMarginPercentageMapAbove);

        Map<String, String> APOMarginPercentageMapBelow = new HashMap<>();
        APOMarginPercentageMapBelow.put("value", "Below AOP Margin %");
        result.add(APOMarginPercentageMapBelow);

        return result;
    }

    public List<Map<String, String>> getMarginPercentageForFilter() {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> MarginBelow10 = new HashMap<>();
        MarginBelow10.put("value", "<10% Margin");
        result.add(MarginBelow10);

        Map<String, String> MarginBelow20 = new HashMap<>();
        MarginBelow20.put("value", "<20% Margin");
        result.add(MarginBelow20);

        Map<String, String> MarginBelow30 = new HashMap<>();
        MarginBelow30.put("value", "<30% Margin");
        result.add(MarginBelow30);

        Map<String, String> MarginAbove30 = new HashMap<>();
        MarginAbove30.put("value", ">=30% Margin");
        result.add(MarginAbove30);

//        Map<String, String> MarginVe = new HashMap<>();
//        MarginBelow10.put("value", "<10% Margin>");
//        result.add(MarginBelow10);
        return result;
    }


    public List<Map<String, String>> getAllDealerName() {
        List<Map<String, String>> result = new ArrayList<>();
        List<String> list = bookingOrderRepository.getAllDealerName();
        for (String dealerName : list) {
            Map<String, String> map = new HashMap<>();
            map.put("value", dealerName);
            result.add(map);
        }
        return result;
    }


    public List<Map<String, String>> getAllModel() {
        List<Map<String, String>> result = new ArrayList<>();
        List<String> modelList = bookingOrderRepository.getAllModel();
        for (String model : modelList) {
            Map<String, String> map = new HashMap<>();
            map.put("value", model);
            result.add(map);
        }
        return result;
    }

    /**
     * Return a collection of <Series, AOP Margin> by Year
     *
     * @return
     */
//    private Map<String, AOPMargin> getMarginPercentageSTD(int year) {
//        return AOPMarginRepository.findByYear(year);
//    }
}
