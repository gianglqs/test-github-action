package com.hysteryale.service;

import com.hysteryale.model.CostUplift;
import com.hysteryale.repository.CostUpliftRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class CostUpliftService {
    @Resource
    CostUpliftRepository costUpliftRepository;
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
        monthMap.put("June", 5);
        monthMap.put("July", 6);
        monthMap.put("Aug", 7);
        monthMap.put("Sept", 8);
        monthMap.put("Oct", 9);
        monthMap.put("Nov", 10);
        monthMap.put("Dec", 11);
    }

    public List<String> getAllFilesInFolder(String folderPath) {
        Pattern pattern = Pattern.compile("^(01. Bookings Register).*(.xlsx)$");

        List<String> fileList = new ArrayList<>();
        Matcher matcher;
        try {
            DirectoryStream<Path> folder = Files.newDirectoryStream(Paths.get(folderPath));
            for(Path path : folder) {
                matcher = pattern.matcher(path.getFileName().toString());
                if(matcher.matches())
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

    public void importCostUplift() throws FileNotFoundException {
        // Folder contains Excel file of Booking Order
        String folderPath = "import_files/booking";
        // Get files in Folder Path
        List<String> fileList = getAllFilesInFolder(folderPath);
        List<CostUplift> costUpliftList = new ArrayList<>();

        for(String fileName : fileList) {
            log.info("{ Start importing file: '" + fileName + "'");
            InputStream is = new FileInputStream(folderPath + "/" + fileName);
            Workbook workbook = StreamingReader
                    .builder()              //setting Buffer
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(is);

            //Pattern for getting month and year in fileName
            Pattern pattern = Pattern.compile(".{24}(.{4}).*(\\d{4}).*");
            Matcher matcher = pattern.matcher(fileName);

            initMonthMap();
            Calendar date = new GregorianCalendar();

            // Assign date
            if(matcher.find()) {
                String month = matcher.group(1).strip().replace("-", "");
                int year = Integer.parseInt(matcher.group(2));
                date.set(year, monthMap.get(month), 1);
            }

            Sheet sheet = workbook.getSheet("Currency & Conversion");

            for(Row row : sheet) {
                if(!row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()
                    && row.getRowNum() > 2 && row.getRowNum() < 9) {

                    String plantName = row.getCell(5).getStringCellValue();
                    double costUplift = row.getCell(6).getNumericCellValue();

                    CostUplift costUpliftObject = new CostUplift();
                    costUpliftObject.setPlant(plantName);
                    costUpliftObject.setCostUplift(costUplift);
                    costUpliftObject.setDate(date);

                    // Check if CostUplift with plantName and date is existed
                    if(costUpliftRepository.getCostUpliftByPlantAndDate(plantName, date).isEmpty())
                        costUpliftList.add(costUpliftObject);
                }
            }
            costUpliftRepository.saveAll(costUpliftList);
            log.info("CostUplift are newly saved or updated: " + costUpliftList.size());
            log.info("End importing }");
            costUpliftList.clear();
        }
    }
}
