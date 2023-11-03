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
import java.text.SimpleDateFormat;
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
                String columnName = row.getCell(i).getStringCellValue().trim();
                ORDER_COLUMNS_NAME.put(columnName, i);
            }
        }
        logInfo("Order Columns: " + ORDER_COLUMNS_NAME);
    }

    /**
     * Get all files having name starting with {01. Bookings Register} and ending with {.xlsx}
     *
     * @param folderPath path to folder contains Booking Order
     * @return list of files' name
     */
    public List<String> getAllFilesInFolder(String folderPath, int state) {
        Pattern pattern;

        switch (state) {
            case 1:
                pattern = Pattern.compile(".*Final.*(.xlsx)$");
                break;
            case 2:
                pattern = Pattern.compile("^01.*(.xlsx)$");
                break;
            default:
                pattern = Pattern.compile("^Cost_Data.*(.xlsx)$");
                break;
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
                    logError("Wrong formatted file's name: " + path.getFileName().toString());
            }
        } catch (Exception e) {
            logInfo(e.getMessage());

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

            // allow assigning value for object's fields
            field.setAccessible(true);
            if (field.getName().equals("productDimension")) {
                try {
                    ProductDimension productDimension = productDimensionService.getProductDimensionByMetaseries(row.getCell(ORDER_COLUMNS_NAME.get("SERIES"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    field.set(bookingOrder, productDimension);
                } catch (Exception e) {
                    rollbar.error(e.toString());
                    logError(e.toString());
                }
            } else if (field.getName().equals("billTo")) {
                try {
                    Cell cell = row.getCell(ORDER_COLUMNS_NAME.get("BILLTO"));
                    field.set(bookingOrder, cell.getStringCellValue());
                    // APICDealer apicDealer = apicDealerService.getAPICDealerByBillToCode(row.getCell(ORDER_COLUMNS_NAME.get("BILLTO"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    // field.set(bookingOrder, apicDealer);
                } catch (Exception e) {
                    logError(e.toString());
                }
            } else if (field.getName().equals("model")) {
                try {
                    Cell cell = row.getCell(ORDER_COLUMNS_NAME.get("MODEL"));
                    field.set(bookingOrder, cell.getStringCellValue());

                } catch (Exception e) {
                    logError(e.toString());
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
                    logError(e.toString());
                }
            } else if (field.getName().equals("currency")) {
                try {
                    Cell cell = row.getCell(ORDER_COLUMNS_NAME.get("Currency"));
                    field.set(bookingOrder, cell.getStringCellValue());
                } catch (Exception e) {
                    logError(e.toString());
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


    private Date extractDate(String fileName) {
        String dateRegex = "\\d{2}_\\d{2}_\\d{4}";
        Matcher m = Pattern.compile(dateRegex).matcher(fileName);
        Date date = null;
        try {
            if (m.find()) {
                date = new SimpleDateFormat("MM_dd_yyyy").parse(m.group());
            } else {
                logError("Can not extract Date from File name: " + fileName);
            }

        } catch (java.text.ParseException e) {
            logError("Can not extract Date from File name: " + fileName);
        }
        return date;
    }


    public void importOrder() throws IOException, IllegalAccessException {

        // Folder contains Excel file of Booking Order
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.booked-order");

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, 1);
        String[] monthArr = {"Apr", "Feb", "Jan", "May", "Aug", "Jul", "Jun", "Mar", "Sep", "Oct", "Nov", "Dec"};
        List<String> listMonth = Arrays.asList(monthArr);
        String month = "", year = "";

        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
//            if(isImported(pathFile)){
//                logWarning("file '"+fileName+"' has been imported");
//                continue;
//            }

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
            InputStream is = new FileInputStream(pathFile);
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
                    //  newBookingOrder = insertTotalCostOrMarginPercent(newBookingOrder, month, year);

                    boolean isOldData = checkOldData(month, year);
                    if (isOldData) {
                        newBookingOrder = insertMarginPercent(newBookingOrder, month, year);
                    } else {
                        newBookingOrder = insertTotalCost(newBookingOrder, month, year);
                    }

                    newBookingOrder = calculateOrderValues(newBookingOrder, isOldData);
                    bookingOrderList.add(newBookingOrder);
                }
            }

            bookingOrderRepository.saveAll(bookingOrderList);

            // logInfo("End importing file: '" + fileName + "'");
            //    updateStateImportFile(pathFile);
            logInfo(bookingOrderList.size() + " Booking Order updated or newly saved }");

            bookingOrderList.clear();
        }
    }

    /**
     * For New Data
     *
     * @param booking
     * @param month
     * @param year
     * @return Booking
     * @throws IOException
     */
    public BookingOrder insertTotalCost(BookingOrder booking, String month, String year) throws IOException {
        // Folder contains Excel file of Booking Order
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String targetFolder = "";
        String[] monthArr = {"Apr", "Feb", "Jan", "May", "Aug", "Jul", "Jun", "Mar", "Sep", "Oct", "Nov", "Dec"};
        List<String> listMonth = Arrays.asList(monthArr);
        // if old data -> collect from file booking, else -> collect from file total-cost
        String folderPath;
        List<String> fileList;

        targetFolder = EnvironmentUtils.getEnvironmentValue("import-files.total-cost");
        folderPath = baseFolder + targetFolder;
        // Get files in Folder Path
        fileList = getAllFilesInFolder(folderPath, 100);


        for (String fileName : fileList) {

            // if data is new extract file name Cost_Data_10_09_2023_11_01_37 -> Date -> month,year
            if (fileName.contains(year) && listMonth.get(extractDate(fileName).getMonth()).toLowerCase().contains(month.toLowerCase())) {
                InputStream is = new FileInputStream(folderPath + "/" + fileName);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                // if old data -> colect from sheet "Wk - Margins", else -> sheet "Cost Data"
                Sheet sheet = workbook.getSheet("Cost Data");

                HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) getOrderColumnsName(row, ORDER_COLUMNS_NAME);
                    else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 0) {

                        Cell OrderNOCell = row.getCell(ORDER_COLUMNS_NAME.get("Order"));

                        if (OrderNOCell.getStringCellValue().equals(booking.getOrderNo())) {

                            Cell totalCostCell = row.getCell(ORDER_COLUMNS_NAME.get("TOTAL MFG COST Going-To"));
                            if (totalCostCell.getCellType() == CellType.NUMERIC) {
                                booking.setTotalCost(totalCostCell.getNumericCellValue());
                            } else if (totalCostCell.getCellType() == CellType.STRING) {
                                booking.setTotalCost(Double.parseDouble(totalCostCell.getStringCellValue()));
                            } else {
                                logInfo("Not found");
                            }

                            break;
                        }

                    }
                }
            }
            if (booking.getTotalCost() == 0)
                logInfo("Total Cost not found " + booking.getOrderNo());
        }

        return booking;
    }


    /**
     * For Old Data
     *
     * @param booking
     * @param month
     * @param year
     * @return Booking
     * @throws IOException
     */
    private BookingOrder insertMarginPercent(BookingOrder booking, String month, String year) throws IOException {
        // Folder contains Excel file of Booking Order
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String targetFolder = "";
        String[] monthArr = {"Apr", "Feb", "Jan", "May", "Aug", "Jul", "Jun", "Mar", "Sep", "Oct", "Nov", "Dec"};
        List<String> listMonth = Arrays.asList(monthArr);
        // if old data -> collect from file booking, else -> collect from file total-cost
        String folderPath;
        List<String> fileList;


        targetFolder = EnvironmentUtils.getEnvironmentValue("import-files.booking");
        folderPath = baseFolder + targetFolder;


        // Get files in Folder Path
        fileList = getAllFilesInFolder(folderPath, 2);


        for (String fileName : fileList) {
            // Check year and month
            if (fileName.contains(year) && fileName.toLowerCase().contains(month.toLowerCase())) {
                InputStream is = new FileInputStream(folderPath + "/" + fileName);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                // if old data -> colect from sheet "Wk - Margins", else -> sheet "Cost Data"
                Sheet sheet = workbook.getSheet("Wk - Margins");

                HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();
                for (Row row : sheet) {
                    if (row.getRowNum() == 1) getOrderColumnsName(row, ORDER_COLUMNS_NAME);
                    else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 1) {

                        Cell OrderNOCell = row.getCell(ORDER_COLUMNS_NAME.get("Order #"));

                        if (OrderNOCell.getStringCellValue().equals(booking.getOrderNo())) {

                            Cell marginCell = row.getCell(ORDER_COLUMNS_NAME.get("Margin @ AOP Rate"));
                            if (marginCell.getCellType() == CellType.NUMERIC) {
                                booking.setMarginPercentageAfterSurCharge(marginCell.getNumericCellValue());
                            }

                            break;
                        }
                    }
                }
                if (booking.getMarginPercentageAfterSurCharge() == 0 && booking.getTotalCost() == 0)
                    logInfo("khong tim thay Margin%   " + booking.getOrderNo());
            }
        }
        return booking;
    }

    public boolean checkOldData(String month, String year) {
        return Integer.parseInt(year) < 2023 | (Integer.parseInt(year) == 2023 && !(month.equals("Sep") | month.equals("Oct") | month.equals("Nov") | month.equals("Dec")));
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
    private BookingOrder calculateOrderValues(BookingOrder bookingOrder, boolean isOldData) {
        //Get All Part
        Set<Part> newParts = partRepository.getPartByOrderNumber(bookingOrder.getOrderNo());

        //Get AOPMargin if Exist
        AOPMargin aopMargin = null;
        String series = bookingOrder.getSeries().substring(3);
        List<AOPMargin> aopMarginList = AOPMarginRepository.findByMetaSeries(series);
        if (aopMarginList.isEmpty()) {
            log.info("Metaseries khong co AOP" + series);
        } else {
            aopMargin = aopMarginList.get(0);
        }

        double marginPercent = 0;
        if (aopMargin != null) {
            marginPercent = aopMargin.getMarginSTD();
        }

        double dealerNet = 0;
        //dealnet after surcharge
        double dealerNetAfterSurchage;
        //margin $ after surcharge
        double marginAfterSurcharge = 0;
        //default   = 0
        double surchage = 0;
        double totalCost;
        double marginPercentageAfterSurcharge;

        for (Part part : newParts) {
            //dealer Net
            dealerNet = dealerNet + part.getNetPriceEach();
        }

        // Calculate DNAfterSurchage
        dealerNetAfterSurchage = dealerNet * (1 + surchage);

        if (isOldData) {
            marginPercentageAfterSurcharge = bookingOrder.getMarginPercentageAfterSurCharge();
            marginAfterSurcharge = dealerNetAfterSurchage * marginPercentageAfterSurcharge;
            totalCost = dealerNetAfterSurchage - marginAfterSurcharge;
        } else {
            totalCost = bookingOrder.getTotalCost();
            marginAfterSurcharge = dealerNetAfterSurchage - totalCost;
            marginPercentageAfterSurcharge = marginAfterSurcharge / dealerNetAfterSurchage;
        }
        bookingOrder.setDealerNet(dealerNet);
        bookingOrder.setDealerNetAfterSurCharge(dealerNetAfterSurchage);
        bookingOrder.setMarginAfterSurCharge(marginAfterSurcharge);
        bookingOrder.setMarginPercentageAfterSurCharge(marginPercentageAfterSurcharge);
        bookingOrder.setTotalCost(totalCost);

        bookingOrder.setAOPMarginPercentage(marginPercent);

        return bookingOrder;
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
        list.sort(String::compareTo);
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
        modelList.sort(String::compareTo);
        for (String model : modelList) {
            Map<String, String> map = new HashMap<>();
            map.put("value", model);
            result.add(map);
        }
        return result;
    }

    public List<BookingOrder> getDistinctBookingOrderByModelCode(String modelCode, int year, int month) {
        return bookingOrderRepository.getDistinctBookingOrderByModelCode(modelCode, year, month);
    }

}
