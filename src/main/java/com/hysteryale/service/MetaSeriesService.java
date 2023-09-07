package com.hysteryale.service;

import com.hysteryale.model.MetaSeries;
import com.hysteryale.repository.MetaSeriesRepository;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MetaSeriesService {
    @Resource
    MetaSeriesRepository metaSeriesRepository;

    private final HashMap<String, Integer> META_SERIES_COLUMNS = new HashMap<>();
    private String segment1Group;

    /**
     * Get MetaSeries columns' name in Excel file "Meta series vs segment vs class mapping"
     * @param row Excel row
     */
    public void getMetaSeriesColumnsName(Row row) {
        for(int i = 1; i < 5; i++) {
            String columnName = row.getCell(i).getStringCellValue();
            META_SERIES_COLUMNS.put(columnName, i);
        }
        log.info("MetaSeries Columns: " + META_SERIES_COLUMNS);
    }

    /**
     * Map Excel data into MetaSeries Object
     */
    public MetaSeries mapExcelDataToMetaSeries(Row row) throws IllegalAccessException {
        MetaSeries metaSeries = new MetaSeries();
        Class<? extends  MetaSeries> metaSeriesClass = metaSeries.getClass();
        Field[] fields = metaSeriesClass.getDeclaredFields();

        for(Field field : fields) {
            String fieldName = field.getName();
            String hashMapKey = null;
            field.setAccessible(true);

            /* field "segment1" is not defined every row in Excel file but put as a header
                Therefore, this field is imported by assigning "segment1Group" which is updated if the cell has value
             */
            if(fieldName.equals("segment1")) {
                field.set(metaSeries, segment1Group);
            }
            else {
                // Get hashMapKey by every fieldName
                switch (fieldName) {
                    case "series":
                        hashMapKey = "AP Total by Segment";
                        break;
                    case "segment2":
                        hashMapKey = "Segment2";
                        break;
                    case "clazz":
                        hashMapKey = "Class";
                        break;
                }
                field.set(metaSeries, row.getCell(META_SERIES_COLUMNS.get(hashMapKey), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
            }
        }
        return metaSeries;
    }
    public void importMetaSeries() throws FileNotFoundException, IllegalAccessException {
        // Initialize folderPath and fileName
        String folderPath = "import_files/meta_series";
        String fileName = "Meta series vs segment vs class mapping.xlsx";

        InputStream is = new FileInputStream(folderPath + "/" + fileName);
        Workbook workbook = StreamingReader
                .builder()              //setting Buffer
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is);

        Sheet metaSeriesSheet = workbook.getSheet("Class Series Segment ");
        List<MetaSeries> metaSeriesList = new ArrayList<>();

        for(Row row : metaSeriesSheet) {
            if(row.getRowNum() == 0)
                getMetaSeriesColumnsName(row);
            else if(row.getRowNum() > 0 && !row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().isEmpty()) {

                /*
                    the series is 3(up to 4)-character long so
                    if the value is > 4, it will update the segment1Group
                    else mapping Excel data to object
                 */
                if(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().length() > 4)
                    segment1Group = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                else {
                    MetaSeries metaSeries = mapExcelDataToMetaSeries(row);
                    metaSeriesList.add(metaSeries);
                }

            }
        }
        metaSeriesRepository.saveAll(metaSeriesList);
        log.info("MetaSeries Updated or newly saved: " + metaSeriesList.size());
        metaSeriesList.clear();
    }
    public MetaSeries getMetaSeriesBySeries(String series) {
        Optional<MetaSeries> optionalMetaSeries = metaSeriesRepository.findById(series);

        if(optionalMetaSeries.isPresent())
            return optionalMetaSeries.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No MetaSeries found with " + series);
    }
}
