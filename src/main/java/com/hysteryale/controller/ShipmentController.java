package com.hysteryale.controller;

import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.model.filters.OrderFilter;
import com.hysteryale.service.ImportService;
import com.hysteryale.service.ShipmentService;
import com.hysteryale.utils.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

@RestController
public class ShipmentController {

    @Resource
    ShipmentService shipmentService;

    @Resource
    ImportService importService;

    @PostMapping("/getShipmentData")
    public Map<String, Object> getDataFinancialShipment(@RequestBody FilterModel filters,
                                                        @RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam(defaultValue = "100") int perPage) throws ParseException {
        filters.setPageNo(pageNo);
        filters.setPerPage(perPage);

        return shipmentService.getShipmentByFilter(filters);

    }

    @PostMapping(path = "/importNewShipment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importNewDataShipment(@RequestBody MultipartFile file) throws IOException {

        InputStream is = file.getInputStream();
        if (FileUtils.isExcelFile(is)) {
            importService.importShipmentFileOneByOne(is);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is not an Excel file");
        }
    }
}
