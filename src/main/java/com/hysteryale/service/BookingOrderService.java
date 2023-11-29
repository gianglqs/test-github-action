package com.hysteryale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hysteryale.model.*;
import com.hysteryale.model.Currency;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.model.filters.OrderFilter;
import com.hysteryale.model.marginAnalyst.MarginAnalystMacro;
import com.hysteryale.repository.*;
import com.hysteryale.repository.bookingorder.BookingOrderRepository;
import com.hysteryale.repository.bookingorder.CustomBookingOrderRepository;
import com.hysteryale.service.marginAnalyst.MarginAnalystMacroService;
import com.hysteryale.utils.ConvertDataFilterUtil;
import com.hysteryale.utils.DateUtils;
import com.hysteryale.utils.EnvironmentUtils;
import com.hysteryale.utils.PlantUtil;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    MarginAnalystMacroService marginAnalystMacroService;

    @Resource
    PartService partService;

    @Resource
    ExchangeRateService exchangeRateService;

    @Resource
    RegionService regionService;

    /**
     * Get Columns' name in Booking Excel file, then store them (columns' name) respectively with the index into HashMap
     *
     * @param row which contains columns' name
     */
    public void getOrderColumnsName(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) {
        for (int i = 0; i < 50; i++) {
            if (row.getCell(i) != null) {
                String columnName = row.getCell(i).getStringCellValue().trim();
                if (ORDER_COLUMNS_NAME.containsKey(columnName)) continue;
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
                pattern = Pattern.compile(".*(.xlsx)$");
                break;
        }

        List<String> fileList = new ArrayList<>();
        Matcher matcher;
        try {
            DirectoryStream<Path> folder = Files.newDirectoryStream(Paths.get(folderPath));
            for (Path path : folder) {
                matcher = pattern.matcher(path.getFileName().toString());
                if (matcher.matches()) fileList.add(path.getFileName().toString());
                else logError("Wrong formatted file's name: " + path.getFileName().toString());
            }
        } catch (Exception e) {
            logInfo(e.getMessage());

        }
        return fileList;
    }


    BookingOrder mapExcelDataIntoOrderObject(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) {
        BookingOrder bookingOrder = new BookingOrder();

        //set OrderNo
        Cell orderNoCell = row.getCell(ORDER_COLUMNS_NAME.get("ORDERNO"));
        bookingOrder.setOrderNo(orderNoCell.getStringCellValue());

        //set ProductDimension
        ProductDimension productDimension = productDimensionService.getProductDimensionByMetaseries(row.getCell(ORDER_COLUMNS_NAME.get("SERIES"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
        if (productDimension != null) {
            bookingOrder.setProductDimension(productDimension);
        } else {
            logWarning("Not found ProductDimension with OrderNo" + bookingOrder.getOrderNo());
        }

        // set billToCost
        Cell billtoCell = row.getCell(ORDER_COLUMNS_NAME.get("BILLTO"));
        bookingOrder.setBillTo(billtoCell.getStringCellValue());

        //set model
        Cell modelCell = row.getCell(ORDER_COLUMNS_NAME.get("MODEL"));
        bookingOrder.setModel(modelCell.getStringCellValue());

        //set region
        Cell regionCell = row.getCell(ORDER_COLUMNS_NAME.get("REGION"));
        Region region = regionService.getRegionByShortName(regionCell.getStringCellValue());
        if (region != null) {
            bookingOrder.setRegion(region);
        } else {
            logWarning("Not found Region with OrderNo" + bookingOrder.getOrderNo());
        }

        //set date
        String strDate = String.valueOf(row.getCell(ORDER_COLUMNS_NAME.get("DATE")).getNumericCellValue());
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
            bookingOrder.setDate(orderDate);
        }

        // dealerName
        Cell dealerNameCell = row.getCell(ORDER_COLUMNS_NAME.get("DEALERNAME"));
        bookingOrder.setDealerName(dealerNameCell.getStringCellValue());

        // country code
        Cell ctryCodeCell = row.getCell(ORDER_COLUMNS_NAME.get("CTRYCODE"));
        bookingOrder.setCtryCode(ctryCodeCell.getStringCellValue());

        // Series
        Cell seriesCell = row.getCell(ORDER_COLUMNS_NAME.get("SERIES"));
        bookingOrder.setSeries(seriesCell.getStringCellValue());

        return bookingOrder;
    }

    private Date extractDate(String fileName) {
        String dateRegex = "\\d{2}_\\d{2}_\\d{4}";
        Matcher m = Pattern.compile(dateRegex).matcher(fileName);
        Date date = null;
        try {
            if (m.find()) {
                date = new SimpleDateFormat("MM_dd_yyyy").parse(m.group());
                date.setMonth(date.getMonth() - 1); //TODO recheck month of file
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
        List<String> listMonth = DateUtils.monthList();

        String month = "", year = "";
        List<String> USPlant = PlantUtil.getUSPlant();

        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }

            logInfo("{ Start importing file: '" + fileName + "'");
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
                    newBookingOrder = importDealerNet(newBookingOrder);
                    boolean isOldData = checkOldData(month, year);
                    if (isOldData) {
                        newBookingOrder = insertMarginPercent(newBookingOrder, month, year);
                    } else {
                        if (USPlant.contains(newBookingOrder.getProductDimension().getPlant())) {
                            newBookingOrder = importTotalCostFromCostData(newBookingOrder, month, year);
                            logInfo("US Plant");
                        } else {
                            newBookingOrder = importCostRMBOfEachParts(newBookingOrder);
                        }
                    }

                    newBookingOrder = calculateOrderValues(newBookingOrder, isOldData);
                    bookingOrderList.add(newBookingOrder);
                }
            }

            bookingOrderRepository.saveAll(bookingOrderList);

            logInfo(bookingOrderList.size() + " Booking Order updated or newly saved }");
            updateStateImportFile(pathFile);
            bookingOrderList.clear();
        }
    }

    public void importNewBookingFileByFile(InputStream is) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        List<BookingOrder> bookingOrderList = new LinkedList<>();
        HashMap<String, Integer> ORDER_COLUMNS_NAME = new HashMap<>();
        List<String> USPlant = PlantUtil.getUSPlant();

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

                newBookingOrder = importDealerNet(newBookingOrder);

                if (USPlant.contains(newBookingOrder.getProductDimension().getPlant())) {
                    newBookingOrder = importTotalCostFromCostData(newBookingOrder, month, year);
                    logInfo("US Plant");
                } else {
                    newBookingOrder = importCostRMBOfEachParts(newBookingOrder);
                }


                newBookingOrder = calculateOrderValues(newBookingOrder, isOldData);
                bookingOrderList.add(newBookingOrder);
            }
        }

        bookingOrderRepository.saveAll(bookingOrderList);

    }


    public BookingOrder importCostRMBOfEachParts(BookingOrder bookingOrder) {
        List<String> listPartNumber = partService.getPartNumberByOrderNo(bookingOrder.getOrderNo());
        Currency currency = partService.getCurrencyByOrderNo(bookingOrder.getOrderNo());


        Calendar date = bookingOrder.getDate();
        date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1);

        if (currency == null)
            return bookingOrder;
        bookingOrder.setCurrency(currency);
        logInfo(bookingOrder.getOrderNo() + "   " + currency.getCurrency());
        double totalCost = 0;
        if (!bookingOrder.getProductDimension().getPlant().equals("SN")) {
            List<MarginAnalystMacro> marginAnalystMacroList = marginAnalystMacroService.getMarginAnalystMacroByHYMPlantAndListPartNumber(
                    bookingOrder.getModel(), listPartNumber, bookingOrder.getCurrency().getCurrency(), date);
            for (MarginAnalystMacro marginAnalystMacro : marginAnalystMacroList) {
                totalCost += marginAnalystMacro.getCostRMB();
            }
        } else {
            List<MarginAnalystMacro> marginAnalystMacroList = marginAnalystMacroService.getMarginAnalystMacroByPlantAndListPartNumber(
                    bookingOrder.getModel(), listPartNumber, bookingOrder.getCurrency().getCurrency(),
                    bookingOrder.getProductDimension().getPlant(), date);

            for (MarginAnalystMacro marginAnalystMacro : marginAnalystMacroList) {
                totalCost += marginAnalystMacro.getCostRMB();
            }
        }

        // exchange rate
        ExchangeRate exchangeRate = exchangeRateService.getExchangeRate("CNY", bookingOrder.getCurrency().getCurrency(), date);
        if (exchangeRate != null)
            totalCost *= exchangeRate.getRate();
        bookingOrder.setTotalCost(totalCost);
        logInfo(bookingOrder.getTotalCost() + "");
        return bookingOrder;
    }


    //TODO determine 10_9_2023 is SEPT or OCT
    public BookingOrder importTotalCostFromCostData(BookingOrder booking, String month, String year) throws IOException {
        // Folder contains Excel file of Booking Order
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");

        List<String> listMonth = DateUtils.monthList();

        String folderPath;
        List<String> fileList;


        String targetFolder = EnvironmentUtils.getEnvironmentValue("import-files.total-cost");
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
                            // get TotalCost
                            Cell totalCostCell = row.getCell(ORDER_COLUMNS_NAME.get("TOTAL MFG COST Going-To"));
                            if (totalCostCell.getCellType() == CellType.NUMERIC) {
                                booking.setTotalCost(totalCostCell.getNumericCellValue());
                            } else if (totalCostCell.getCellType() == CellType.STRING) {
                                booking.setTotalCost(Double.parseDouble(totalCostCell.getStringCellValue()));
                            } else {
                                logInfo("Not found");
                            }

                            //get Currency
                            Cell currencyCell = row.getCell(ORDER_COLUMNS_NAME.get("Curr"));
                            Optional<Currency> currency = currencyRepository.findById(currencyCell.getStringCellValue());
                            if (currency.isPresent()) {
                                booking.setCurrency(currency.get());
                            } else {
                                logError("NOT FOUND Currency '" + currencyCell.getStringCellValue() + "' with OrderNo: " + booking.getOrderNo());
                            }

                            break;
                        }
                    }
                }
            }

            if (booking.getTotalCost() == 0) logInfo("Total Cost not found " + booking.getOrderNo());
        }

        return booking;
    }

    private List<CostDataFile> getListCostDataByMonthAndYear(String month, String year) throws IOException {
        List<CostDataFile> result = new ArrayList<>();
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");

        List<String> listMonth = DateUtils.monthList();

        String folderPath;

        String targetFolder = EnvironmentUtils.getEnvironmentValue("import-files.total-cost");
        folderPath = baseFolder + targetFolder;
        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, 100);
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

                        // create CostDataFile
                        CostDataFile costDataFile = new CostDataFile();
                        Cell orderNOCell = row.getCell(ORDER_COLUMNS_NAME.get("Order"));
                        costDataFile.orderNo = orderNOCell.getStringCellValue();

                        // get TotalCost
                        Cell totalCostCell = row.getCell(ORDER_COLUMNS_NAME.get("TOTAL MFG COST Going-To"));
                        if (totalCostCell.getCellType() == CellType.NUMERIC) {
                            costDataFile.totalCost = totalCostCell.getNumericCellValue();
                        } else if (totalCostCell.getCellType() == CellType.STRING) {
                            costDataFile.totalCost = Double.parseDouble(totalCostCell.getStringCellValue());
                        }

                        //get Currency
                        Cell currencyCell = row.getCell(ORDER_COLUMNS_NAME.get("Curr"));
                        costDataFile.currency = currencyCell.getStringCellValue();

                        result.add(costDataFile);
                    }
                }
            }
        }
        return result;
    }


    private static class CostDataFile {
        String orderNo;
        double totalCost;
        String currency;
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

        List<String> listMonth = DateUtils.monthList();
        // if old data -> collect from file booking, else -> collect from file total-cost
        String folderPath;
        List<String> fileList;

        String targetFolder = EnvironmentUtils.getEnvironmentValue("import-files.booking");
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
                            //get Margin%
                            Cell marginCell = row.getCell(ORDER_COLUMNS_NAME.get("Margin @ AOP Rate"));
                            if (marginCell.getCellType() == CellType.NUMERIC) {
                                booking.setMarginPercentageAfterSurCharge(marginCell.getNumericCellValue());
                            }

                            // get Currency
                            Cell currencyCell = row.getCell(ORDER_COLUMNS_NAME.get("Currency"));

                            // if cell is FOMULA -> evaluate it
                            if (currencyCell.getCellType() == CellType.FORMULA) {
                                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                CellType cellValue = evaluator.evaluateFormulaCell(currencyCell);

                            }
                            String currencyValue = currencyCell.getStringCellValue();
                            Optional<Currency> currency = currencyRepository.findById(currencyValue);

                            if (currency.isPresent()) {
                                booking.setCurrency(currency.get());
                            } else {
                                logError("currency value " + currencyValue);
                                logError("Not Found currency with ORDERNO: " + booking.getOrderNo());
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
    public Map<String, Object> getBookingOrdersByFilters(OrderFilter orderFilter, int pageNo, int perPage) throws
            ParseException, JsonProcessingException, java.text.ParseException {

        // Use ObjectMapper to Map JSONObject value into List<String
        ObjectMapper mapper = new ObjectMapper();
        String orderNo = orderFilter.getOrderNo();


        // Parse all filters into ArrayList<String>
        List<String> regions = orderFilter.getRegions();
        List<String> dealers = orderFilter.getDealers();
        List<String> plants = orderFilter.getPlants();
        List<String> metaSeries = orderFilter.getMetaSeries();
        List<String> classes = orderFilter.getClasses();
        List<String> models = orderFilter.getModels();
        List<String> segments = orderFilter.getSegments();

        String AOPMarginPercetage = orderFilter.getAOPMarginPercetage();
        String MarginPercetage = orderFilter.getMarginPercetage();

        // Get from DATE to DATE
        String strFromDate = orderFilter.getStrFromDate();
        String strToDate = orderFilter.getStrToDate();

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
    public long getNumberOfBookingOrderByFilters(String
                                                         orderNo, List<String> regions, List<String> dealers, List<String> plants, List<String> metaSeries, List<String> classes, List<String> models, List<String> segments, String
                                                         strFromDate, String strToDate, String AOPMarginPercetage, String MarginPercetage) throws
            java.text.ParseException {
        return customBookingOrderRepository.getNumberOfBookingOrderByFilters(orderNo, regions, dealers, plants, metaSeries, classes, models, segments, strFromDate, strToDate, AOPMarginPercetage, MarginPercetage);
    }

    public BookingOrder importDealerNet(BookingOrder booking) {
        Set<Part> newParts = partRepository.getPartByOrderNumber(booking.getOrderNo());
        double dealerNet = 0;
        for (Part part : newParts) {
            dealerNet += part.getNetPriceEach();
        }
        booking.setDealerNet(dealerNet);
        return booking;
    }

    /**
     * To calculate extra values of an order
     */
    private BookingOrder calculateOrderValues(BookingOrder bookingOrder, boolean isOldData) {
        //Get All Part

        //Get AOPMargin if Exist
        AOPMargin aopMargin = null;
        String series = bookingOrder.getSeries().substring(1);
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

        double dealerNet = bookingOrder.getDealerNet();
        //dealnet after surcharge
        double dealerNetAfterSurchage;
        //margin $ after surcharge
        double marginAfterSurcharge = 0;
        //default   = 0
        double surcharge = 0;
        double totalCost;
        double marginPercentageAfterSurcharge;


        // Calculate DNAfterSurchage
        dealerNetAfterSurchage = dealerNet * (1 + surcharge);

        if (isOldData) {
            marginPercentageAfterSurcharge = bookingOrder.getMarginPercentageAfterSurCharge();
            marginAfterSurcharge = dealerNetAfterSurchage * marginPercentageAfterSurcharge;
            totalCost = dealerNetAfterSurchage - marginAfterSurcharge;
        } else {
            totalCost = bookingOrder.getTotalCost();
            marginAfterSurcharge = dealerNetAfterSurchage - totalCost;
            marginPercentageAfterSurcharge = marginAfterSurcharge / dealerNetAfterSurchage;
        }
        bookingOrder.setDealerNetAfterSurCharge(dealerNetAfterSurchage);
        bookingOrder.setMarginAfterSurCharge(marginAfterSurcharge);
        bookingOrder.setMarginPercentageAfterSurCharge(marginPercentageAfterSurcharge);
        bookingOrder.setTotalCost(totalCost);

        bookingOrder.setAOPMarginPercentage(marginPercent);

        return bookingOrder;
    }


    /**
     * Get AOP margin Percentage
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

    /**
     * Get margin Percentage
     */
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

        return result;
    }


    /**
     * Get all dealer names
     *
     * @return
     */
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

    public Optional<BookingOrder> getDistinctBookingOrderByModelCode(String modelCode) {
        return bookingOrderRepository.getDistinctBookingOrderByModelCode(modelCode);
    }

    public Optional<BookingOrder> getBookingOrderByOrderNumber(String orderNumber) {
        return bookingOrderRepository.findById(orderNumber);
    }

    public Map<String, Object> getBookingByFilter(FilterModel filterModel) throws java.text.ParseException {
        Map<String, Object> result = new HashMap<>();
        //Get FilterData
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        logInfo(filterMap.toString());

        List<BookingOrder> bookingOrderList = bookingOrderRepository.selectAllForBookingOrder(
                filterMap.get("orderNoFilter"), filterMap.get("regionFilter"), filterMap.get("plantFilter"),
                filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"),
                (Pageable) filterMap.get("pageable"));
        result.put("listBookingOrder", bookingOrderList);
        //get total Recode
        int totalCompetitor = bookingOrderRepository.getCount(filterMap.get("orderNoFilter"), filterMap.get("regionFilter"), filterMap.get("plantFilter"),
                filterMap.get("metaSeriesFilter"), filterMap.get("classFilter"), filterMap.get("modelFilter"),
                filterMap.get("segmentFilter"), filterMap.get("dealerNameFilter"), filterMap.get("aopMarginPercentageFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null : ((List) filterMap.get("marginPercentageFilter")).get(1),
                (Calendar) filterMap.get("fromDateFilter"), (Calendar) filterMap.get("toDateFilter"));
        result.put("totalItems", totalCompetitor);
        return result;
    }
}
