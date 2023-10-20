package com.hysteryale.controller;

import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import com.hysteryale.model_h2.IMMarginAnalystData;
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
     */
    @PostMapping(path = "/estimateMarginAnalystData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> estimateMarginAnalystData(@RequestBody MultipartFile file, Authentication authentication) throws IOException {

        // Verify the Excel file
        if (FileUtils.isExcelFile(file.getOriginalFilename())) {
            String originalFileName = file.getOriginalFilename();
            String fileUUID = marginAnalystFileUploadService.saveMarginAnalystFileUpload(file, authentication);

            log.info(file.getContentType());

            IMMarginAnalystDataService.calculateMarginAnalystData(originalFileName, fileUUID);
            IMMarginAnalystDataService.calculateMarginAnalystSummary(fileUUID, originalFileName, "monthly");
            IMMarginAnalystDataService.calculateMarginAnalystSummary(fileUUID, originalFileName, "annually");

            return Map.of(
                    "fileUUID", fileUUID
            );
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is not an Excel file");

    }

    @PostMapping(path = "/getEstimateMarginAnalystData")
    Map<String, Object> getIMMarginAnalystData(@RequestBody IMMarginAnalystData imMarginAnalystData) {
        List<IMMarginAnalystData> imMarginAnalystDataList =
                IMMarginAnalystDataService.getIMMarginAnalystData(imMarginAnalystData.getModelCode(), imMarginAnalystData.getCurrency(), imMarginAnalystData.getFileUUID());

        Map<String, Object> imMarginAnalystSummaryMap =
                IMMarginAnalystDataService.getIMMarginAnalystSummary(imMarginAnalystData.getModelCode(), imMarginAnalystData.getCurrency(), imMarginAnalystData.getFileUUID());

        return Map.of(
                "MarginAnalystData", imMarginAnalystDataList,
                "MarginAnalystSummary", imMarginAnalystSummaryMap
        );
    }
}

