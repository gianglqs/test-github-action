package com.hysteryale.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.model.filters.OrderFilter;
import com.hysteryale.response.ResponseObject;
import com.hysteryale.service.*;
import com.hysteryale.utils.EnvironmentUtils;
import com.hysteryale.utils.FileUtils;
import com.hysteryale.utils.PagingnatorUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class BookingOrderController {

    @Resource
    BookingOrderService bookingOrderService;

    @Resource
    ImportService importService;

    private FilterModel filters;


    /**
     * Get BookingOrders based on filters and pagination
     *
     * @param filters from FE
     * @param pageNo  current page
     * @param perPage number of items per page
     */

    @PostMapping(path = "/bookingOrders", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDataBooking(@RequestBody FilterModel filters,
                                                        @RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam(defaultValue = "100") int perPage) throws java.text.ParseException {
        filters.setPageNo(pageNo);
        filters.setPerPage(perPage);
        this.filters = filters;
        return bookingOrderService.getBookingByFilter(filters);

    }

    @PostMapping(path = "/importNewBooking", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> importNewDataBooking(@RequestBody MultipartFile file) throws IOException, ParseException {
        InputStream is = file.getInputStream();

        if (FileUtils.isExcelFile(is)) {
            // save file in folder tmp
            String folderPath = EnvironmentUtils.getEnvironmentValue("upload_files.base-folder");
            FileUtils.saveFile(file, folderPath);

            // open file to import
            String pathFile = FileUtils.getPath(folderPath, file.getOriginalFilename());
            InputStream inputStream = new FileInputStream(pathFile);

            importService.importBookingFileOneByOne(inputStream);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Import data successfully", bookingOrderService.getBookingByFilter(filters)));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Uploaded file is not an Excel file", null));
        }
    }

}
