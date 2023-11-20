package com.hysteryale.service;

import com.hysteryale.model.BookingOrder;
import com.hysteryale.model.ProductDimension;
import com.hysteryale.model.Region;
import com.hysteryale.model.Shipment;
import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.competitor.ForeCastValue;
import com.hysteryale.repository.AOPMarginRepository;
import com.hysteryale.repository.CompetitorPricingRepository;
import com.hysteryale.repository.ShipmentRepository;
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
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImportService extends BasedService {

    @Resource
    CompetitorPricingRepository competitorPricingRepository;
    @Resource
    RegionService regionService;
    @Resource
    PartService partService;

    @Resource
    ShipmentRepository shipmentRepository;

    @Resource
    ProductDimensionService productDimensionService;

    @Resource
    BookingOrderRepository bookingOrderRepository;

    @Resource
    AOPMarginService aopMarginService;

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

    public List<String> getAllFilesInFolder(String folderPath, int state) {
        Pattern pattern;

        switch (state) {
            case 1:
                pattern = Pattern.compile(".*Final.*(.xlsx)$");
                break;
            case 2:
                pattern = Pattern.compile("^01.*(.xlsx)$");
                break;
            case 3:
                pattern = Pattern.compile("^Competitor.*(.xlsx)$");
                break;
            case 4:
                pattern = Pattern.compile("^SAP.*(.xlsx)$");
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

    public List<CompetitorPricing> mapExcelDataIntoCompetitorObject(Row row, HashMap<String, Integer> ORDER_COLUMNS_NAME) {
        List<CompetitorPricing> competitorPricingList = new ArrayList<>();

        Cell cellRegion = row.getCell(ORDER_COLUMNS_NAME.get("Region"));
        String region = cellRegion.getStringCellValue();

        Cell cellCompetitorName = row.getCell(ORDER_COLUMNS_NAME.get("Brand"));
        String competitorName = cellCompetitorName.getStringCellValue();

        boolean isChineseBrand = competitorName.contains("Heli") || competitorName.contains("HeLi") || competitorName.contains("Hangcha") || competitorName.contains("Hang Cha");
        Cell cellClass = row.getCell(ORDER_COLUMNS_NAME.get("Class"));
        String clazz = cellClass.getStringCellValue();

        Double leadTime = null;
        Cell cellLeadTime = row.getCell(ORDER_COLUMNS_NAME.get("Lead Time"));
        if (cellLeadTime != null && cellLeadTime.getCellType() == CellType.NUMERIC) {
            leadTime = cellLeadTime.getNumericCellValue();
        }

        double competitorPricing = row.getCell(ORDER_COLUMNS_NAME.get("Price (USD)")).getNumericCellValue();
        double marketShare = row.getCell(ORDER_COLUMNS_NAME.get("Market Share")).getNumericCellValue();

        // 2 fields below are hard-coded, will be modified later
        double percentageDealerPremium = 0.1;
        double dealerNet = 10000;

        String category = row.getCell(ORDER_COLUMNS_NAME.get("Category")).getStringCellValue();
        String country = row.getCell(ORDER_COLUMNS_NAME.get("Country"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

        // assigning values for CompetitorPricing
        CompetitorPricing cp = new CompetitorPricing();
        cp.setCompetitorName(competitorName);
        cp.setCategory(category);
        cp.setCountry(country);
        cp.setRegion(region);
        cp.setClazz(clazz);
        cp.setCompetitorLeadTime(leadTime);
        cp.setDealerNet(dealerNet);
        cp.setChineseBrand(isChineseBrand);

        cp.setCompetitorPricing(competitorPricing);
        cp.setDealerPremiumPercentage(percentageDealerPremium);
        cp.setSeries("");
        cp.setMarketShare(marketShare);

        // separate seriesString (for instances: A3C4/A7S4)
        String seriesString;
        Cell cellSeries = row.getCell(ORDER_COLUMNS_NAME.get("HYG Series"));
        if (cellSeries != null && cellSeries.getCellType() == CellType.STRING) {
            seriesString = cellSeries.getStringCellValue();
            StringTokenizer stk = new StringTokenizer(seriesString, "/");
            while (stk.hasMoreTokens()) {
                String series = stk.nextToken();
                CompetitorPricing cp1 = new CompetitorPricing();
                cp1.setCompetitorName(competitorName);
                cp1.setCategory(category);
                cp1.setCountry(country);
                cp1.setRegion(region);
                cp1.setClazz(clazz);
                cp1.setCompetitorLeadTime(leadTime);
                cp1.setDealerNet(partService.getAverageDealerNet(region, clazz, series));
                log.info("AVG Dealer Net: " + partService.getAverageDealerNet(region, clazz, series));
                cp1.setChineseBrand(isChineseBrand);

                cp1.setCompetitorPricing(competitorPricing);
                cp1.setDealerPremiumPercentage(percentageDealerPremium);
                cp1.setSeries(series);
                cp1.setMarketShare(marketShare);
                cp1.setModel(productDimensionService.getModelFromMetaSeries(series.substring(1)));


                competitorPricingList.add(cp1);
            }
        } else
            competitorPricingList.add(cp);
        return competitorPricingList;
    }

    public void importCompetitorPricing() throws IOException {

        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.competitor-pricing");

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, 3);
        List<ForeCastValue> foreCastValues = loadForecastForCompetitorPricingFromFile();

        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }
            logInfo("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(pathFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            HashMap<String, Integer> COMPETITOR_COLUMNS_NAME = new HashMap<>();
            Sheet competitorSheet = workbook.getSheet("Competitor Pricing Database");

            List<CompetitorPricing> competitorPricingList = new ArrayList<>();

            for (Row row : competitorSheet) {
                if (row.getRowNum() == 0) getOrderColumnsName(row, COMPETITOR_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 0) {
                    List<CompetitorPricing> competitorPricings = mapExcelDataIntoCompetitorObject(row, COMPETITOR_COLUMNS_NAME);
                    for (CompetitorPricing competitorPricing : competitorPricings) {
                        // if it has series -> assign ForeCastValue
                        if (!competitorPricing.getSeries().isEmpty()) {
                            String strRegion = competitorPricing.getRegion();
                            String metaSeries = competitorPricing.getSeries().substring(1); // extract metaSeries from series

                            Calendar time = Calendar.getInstance();
                            int currentYear = time.get(Calendar.YEAR);

                            ForeCastValue actualForeCast = findForeCastValue(foreCastValues, strRegion, metaSeries, currentYear - 1);
                            ForeCastValue AOPFForeCast = findForeCastValue(foreCastValues, strRegion, metaSeries, currentYear);
                            ForeCastValue LRFFForeCast = findForeCastValue(foreCastValues, strRegion, metaSeries, currentYear + 1);

                            competitorPricing.setActual(actualForeCast == null ? 0 : actualForeCast.getQuantity());
                            competitorPricing.setAOPF(AOPFForeCast == null ? 0 : AOPFForeCast.getQuantity());
                            competitorPricing.setLRFF(LRFFForeCast == null ? 0 : LRFFForeCast.getQuantity());
                            competitorPricing.setPlant(LRFFForeCast == null ? "" : LRFFForeCast.getPlant());

                            //AOPMargin
                            Double aopMargin = aopMarginService.getAOPMargin(competitorPricing.getSeries(), competitorPricing.getRegion(), competitorPricing.getPlant());
                        }
                        competitorPricingList.add(competitorPricing);
                    }
                }
            }
            competitorPricingRepository.saveAll(competitorPricingList);
            assigningCompetitorValues();
            updateStateImportFile(pathFile);
        }
    }

    /**
     * Find a forecast value by Region and Series, year is an option if year is empty then we get all years
     */
    private ForeCastValue findForeCastValue(List<ForeCastValue> foreCastValues, String strRegion, String metaSeries, int year) {
        for (ForeCastValue foreCastValue : foreCastValues) {
            if (foreCastValue.getRegion().getRegion().equals(strRegion) && foreCastValue.getMetaSeries().equals(metaSeries) && foreCastValue.getYear() == year)
                return foreCastValue;
        }
        return null;
    }

    private List<ForeCastValue> loadForecastForCompetitorPricingFromFile() throws IOException {

        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.forecast-pricing");
        List<String> fileList = getAllFilesInFolder(folderPath, -1);

        List<ForeCastValue> foreCastValues = new ArrayList<>();

        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }
            logInfo("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(pathFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);


            List<Integer> years = new ArrayList<>();
            HashMap<Integer, Integer> YEARS_COLUMN = new HashMap<>();
            HashMap<String, Integer> FORECAST_ORDER_COLUMN = new HashMap<>();

            for (Sheet sheet : workbook) {
                Region region = getRegionBySheetName(sheet.getSheetName());
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        getYearsInForeCast(YEARS_COLUMN, row, years);

                    } else if (row.getRowNum() == 1)
                        getOrderColumnsName(row, FORECAST_ORDER_COLUMN);
                    else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() &&       // checking null
                            row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().length() == 3 &&    // checking cell is whether metaSeries or not
                            row.getRowNum() > 1) {

                        // get all quantity value from 2021 to 2027
                        for (int year : years) {
                            String metaSeries = row.getCell(FORECAST_ORDER_COLUMN.get("Series /Segments")).getStringCellValue();
                            String plant = row.getCell(FORECAST_ORDER_COLUMN.get("Plant"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                            int quantity = (int) row.getCell(YEARS_COLUMN.get(year)).getNumericCellValue();
                            // setting values
                            ForeCastValue foreCastValue = new ForeCastValue(region, year, metaSeries, quantity, plant);
                            foreCastValues.add(foreCastValue);
                        }
                    }
                }
            }
        }
        log.info("Number of ForeCastValue: " + foreCastValues.size());
        return foreCastValues;
    }

    private void getYearsInForeCast(HashMap<Integer, Integer> YEARS_COLUMN, Row row, List<Integer> years) {
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.NUMERIC) {
                int year = (int) cell.getNumericCellValue();
                if (YEARS_COLUMN.get(year) == null) {
                    YEARS_COLUMN.put(year, cell.getColumnIndex());
                    years.add(year);
                }
            }
        }
    }

    /**
     * Find the region based on sheetName in Forecast Value
     */
    private Region getRegionBySheetName(String sheetName) {
        String strRegion;
        switch (sheetName) {
            case "Asia_Fin":
                strRegion = "Asia";
                break;
            case "Pac_Fin":
                strRegion = "Pacific";
                break;
            default:
                strRegion = "India";
        }
        return regionService.getRegionByName(strRegion);
    }

    /**
     * Get CompetitorGroup based on country, class, category, series --> for assigning HYGLeadTime, DealerStreetPricing and calculating Variance %
     */
    private List<String[]> getCompetitorGroup() {
        return competitorPricingRepository.getCompetitorGroup();
    }

    /**
     * Using {country, clazz, category, series} to specify a group of CompetitorPricing -> then can use for calculating values later
     */
    private List<CompetitorPricing> getListOfCompetitorInGroup(String country, String clazz, String category, String series) {
        return competitorPricingRepository.getListOfCompetitorInGroup(country, clazz, category, series);
    }

    /**
     * Assign hygLeadTime, averageDealerNet, dealerStreetPremium and calculating variancePercentage for CompetitorPricing
     * after save based data into DB
     */
    @Transactional
    public void assigningCompetitorValues() {
        List<String[]> competitorGroups = getCompetitorGroup();
        for (String[] competitorGroup : competitorGroups) {
            String country = competitorGroup[0];
            String clazz = competitorGroup[1];
            String category = competitorGroup[2];
            String series = competitorGroup[3];

            List<CompetitorPricing> competitorPricingList = getListOfCompetitorInGroup(country, clazz, category, series);
            double hygLeadTime = 0;
            double totalDealerNet = 0;
            double dealerStreetPricing = 0;
            for (CompetitorPricing cp : competitorPricingList) {

                // Find HYG Brand to assign hygLeadTime and dealerStreetPricing for other brand in a group {country, class, category, series}
                String competitorName = cp.getCompetitorName();
                if (competitorName.contains("HYG") || competitorName.contains("Hyster") || competitorName.contains("Yale") || competitorName.contains("HYM")) {
                    hygLeadTime = cp.getCompetitorLeadTime();
                    dealerStreetPricing = cp.getCompetitorPricing();
                }
                totalDealerNet += cp.getDealerNet();
            }
            double averageDealerNet = totalDealerNet / competitorPricingList.size();

            // Assigning hygLeadTime, averageDealerNet, dealerStreetPremium
            for (CompetitorPricing cp : competitorPricingList) {
                cp.setHYGLeadTime(hygLeadTime);
                cp.setDealerStreetPricing(dealerStreetPricing);
                cp.setAverageDN(averageDealerNet);
                double handlingCost = dealerStreetPricing - cp.getDealerNet() * (1 + cp.getDealerPremiumPercentage());
                double dealerPricingPremium = dealerStreetPricing - (cp.getDealerNet() + handlingCost);
                double dealerPricingPremiumPercentage = dealerPricingPremium / dealerStreetPricing;
                cp.setDealerHandlingCost(handlingCost);
                cp.setDealerPricingPremium(dealerPricingPremium);
                cp.setDealerPricingPremiumPercentage(dealerPricingPremiumPercentage);

                // calculate Variance % = competitorPricing - (dealerStreetPricing + dealerPricingPremium)
                double variancePercentage = (cp.getCompetitorPricing() - (cp.getDealerStreetPricing() + cp.getDealerPricingPremium())) / cp.getCompetitorPricing();
                cp.setVariancePercentage(variancePercentage);
            }
            competitorPricingRepository.saveAll(competitorPricingList);
        }

    }

    public void importShipment() throws IOException {
        String baseFolder = EnvironmentUtils.getEnvironmentValue("import-files.base-folder");
        String folderPath = baseFolder + EnvironmentUtils.getEnvironmentValue("import-files.shipment");

        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath, 4);
        for (String fileName : fileList) {
            String pathFile = folderPath + "/" + fileName;
            //check file has been imported ?
            if (isImported(pathFile)) {
                logWarning("file '" + fileName + "' has been imported");
                continue;
            }
            logInfo("{ Start importing file: '" + fileName + "'");

            InputStream is = new FileInputStream(pathFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            HashMap<String, Integer> SHIPMENT_COLUMNS_NAME = new HashMap<>();
            Sheet competitorSheet = workbook.getSheet("Sheet1");
            List<Shipment> shipmentList = new ArrayList<>();

            for (Row row : competitorSheet) {
                if (row.getRowNum() == 0) getOrderColumnsName(row, SHIPMENT_COLUMNS_NAME);
                else if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty() && row.getRowNum() > 0) {
                    Shipment shipment = mapExcelDataIntoShipmentObject(row, SHIPMENT_COLUMNS_NAME);
                    shipmentList.add(shipment);
                }
            }
            shipmentRepository.saveAll(shipmentList);

            updateStateImportFile(pathFile);
        }
    }

    private Shipment mapExcelDataIntoShipmentObject(Row row, HashMap<String, Integer> shipmentColumnsName) {
        Shipment shipment = new Shipment();

        try {
            // Set orderNo
            String orderNo = row.getCell(shipmentColumnsName.get("Order number")).getStringCellValue();
            shipment.setOrderNo(orderNo);

            // Set serialNUmber
            String serialNumber = row.getCell(shipmentColumnsName.get("Serial Number")).getStringCellValue();
            shipment.setSerialNumber(serialNumber);

            // Set model
            String model = row.getCell(shipmentColumnsName.get("Model")).getStringCellValue();
            shipment.setModel(model);

            // Revenue
            double revenue = row.getCell(shipmentColumnsName.get("Revenue")).getNumericCellValue();
            shipment.setNetRevenue(revenue);

            // dealerName
            String dealerName = row.getCell(shipmentColumnsName.get("End Customer Name")).getStringCellValue();
            shipment.setDealerName(dealerName);

            // country
            String country = row.getCell(shipmentColumnsName.get("Ship-to Country Code")).getStringCellValue();
            shipment.setCtryCode(country);

            // date
            Date date = row.getCell(shipmentColumnsName.get("Created On")).getDateCellValue();
            shipment.setDate(date);

            //totalCost
            Double totalCost = row.getCell(shipmentColumnsName.get("Cost of Sales")).getNumericCellValue();
            shipment.setTotalCost(totalCost);

            //quantity
            int quantity =(int) row.getCell(shipmentColumnsName.get("Quantity")).getNumericCellValue();
            shipment.setQuantity(quantity);

            // get data from BookingOrder
            Optional<BookingOrder> bookingOrderOptional = bookingOrderRepository.getBookingOrderByOrderNo(orderNo);
            if(bookingOrderOptional.isPresent()){
                BookingOrder booking = bookingOrderOptional.get();
                // set series
                shipment.setSeries(booking.getSeries());

                // set productDimension
                shipment.setProductDimension(booking.getProductDimension());

                // set Region
                shipment.setRegion(booking.getRegion());

                // DN
                shipment.setDealerNet(booking.getDealerNet());

                //DN AfterSurcharge
                double dealerNetAfterSurcharge = booking.getDealerNetAfterSurCharge();
                shipment.setDealerNetAfterSurCharge(dealerNetAfterSurcharge);

                double marginAfterSurcharge = dealerNetAfterSurcharge - totalCost;
                double marginPercentageAfterSurcharge = marginAfterSurcharge / dealerNetAfterSurcharge;

                // set Margin Percentage After surcharge
                shipment.setMarginPercentageAfterSurCharge(marginPercentageAfterSurcharge);

                // Set Margin after surcharge
                shipment.setMarginAfterSurCharge(marginAfterSurcharge);

                // AOP Margin %
                shipment.setAOPMarginPercentage(booking.getAOPMarginPercentage());

            }else{
               // logWarning("Not found BookingOrder with OrderNo:  "+orderNo);
            }

        } catch (Exception e) {
          //  logError(e.toString());
        }


        return shipment;
    }


}