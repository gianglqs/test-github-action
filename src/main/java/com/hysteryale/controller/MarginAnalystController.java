package com.hysteryale.controller;

import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import com.hysteryale.model_h2.IMMarginAnalystData;
import com.hysteryale.model_h2.IMMarginAnalystSummary;
import com.hysteryale.service.marginAnalyst.IMMarginAnalystDataService;
import com.hysteryale.service.marginAnalyst.MarginAnalystFileUploadService;
import com.hysteryale.service.marginAnalyst.MarginAnalystService;
import com.hysteryale.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MarginAnalystController {

    @Resource
    MarginAnalystService marginAnalystService;
    @Resource
    IMMarginAnalystDataService IMMarginAnalystDataService;
    @Resource
    MarginAnalystFileUploadService marginAnalystFileUploadService;

    @GetMapping(path = "/marginAnalystData/getDealers")
    public Map<String, List<Map<String, String>>> getDealersInMarginAnalystData() {
        return marginAnalystService.getDealersFromMarginAnalystData();
    }

    @PostMapping(path = "/marginAnalystData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<MarginAnalystData>> getMarginAnalystData(@RequestBody MarginAnalystData marginAnalystData) {
        if(marginAnalystData.getDealer().isEmpty())
            return marginAnalystService.getMarginAnalystData(marginAnalystData.getModelCode(), marginAnalystData.getCurrency().getCurrency(), marginAnalystData.getMonthYear());
        return marginAnalystService.getMarginDataForAnalysisByDealer(marginAnalystData.getModelCode(), marginAnalystData.getCurrency().getCurrency(), marginAnalystData.getMonthYear(), marginAnalystData.getDealer());
    }

    @PostMapping(path = "/marginAnalystSummary", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, MarginAnalystSummary> getMarginAnalystSummary(@RequestBody MarginAnalystData marginAnalystData) {
        return marginAnalystService.getMarginAnalystSummary(marginAnalystData.getModelCode(), marginAnalystData.getCurrency().getCurrency(), marginAnalystData.getMonthYear());
    }

    /**
     * Calculate MarginAnalystData and MarginAnalystSummary based on user's uploaded file
     * @return Map of MarginAnalystData and MarginAnalystSummary
     */
    @PostMapping(path = "/estimateMarginAnalystData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> estimateMarginAnalystData(@RequestParam("multipartFile")MultipartFile multipartFile, @RequestParam String modelCode,Authentication authentication) throws IOException {

        // Verify the Excel file
        if(FileUtils.isExcelFile(multipartFile.getOriginalFilename())) {
            String originalFileName = multipartFile.getOriginalFilename();
            String fileUUID = marginAnalystFileUploadService.saveMarginAnalystFileUpload(multipartFile, authentication);

            log.info(multipartFile.getContentType());

            List<IMMarginAnalystData> imMarginAnalystData = IMMarginAnalystDataService.calculateMarginAnalystData(originalFileName, fileUUID);
            IMMarginAnalystSummary imMarginAnalystSummary = IMMarginAnalystDataService.calculateMarginAnalystSummary(fileUUID, originalFileName, modelCode, "monthly");

            return Map.of(
                    "marginAnalystData", imMarginAnalystData,
                    "marginAnalystSummary", imMarginAnalystSummary
            );
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is not an Excel file");

    }
    @GetMapping (path = "/imMarginData")
    List<IMMarginAnalystData> getIMMarginAnalystData() {
        return IMMarginAnalystDataService.getIMMarginAnalystData();
    }
}

