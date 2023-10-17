package com.hysteryale.controller;

import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import com.hysteryale.model.marginAnalyst.inmemory.IMMarginAnalystData;
import com.hysteryale.model.marginAnalyst.inmemory.IMMarginAnalystSummary;
import com.hysteryale.service.marginAnalyst.IMMarginAnalystDataService;
import com.hysteryale.service.marginAnalyst.MarginAnalystFileUploadService;
import com.hysteryale.service.marginAnalyst.MarginAnalystService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * Upload Excel file for calculating MarginAnalystData and MarginAnalystSummary
     * @param excelFile contains modelCode, partNumbers and needed information
     * @param authentication contains the owner of the uploaded file
     */
    @PostMapping(path = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestParam("excelFile")MultipartFile excelFile, Authentication authentication) throws IOException {
        marginAnalystFileUploadService.saveMarginAnalystFileUpload(excelFile, authentication);
    }

    /**
     * Calculate MarginAnalystData and MarginAnalystSummary based on user's uploaded file
     * @param fileUUID identifier of uploaded file
     * @return Map of MarginAnalystData and MarginAnalystSummary
     */
    @PostMapping(path = "/estimateMarginAnalystData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> estimateMarginAnalystData(@RequestParam String fileUUID) {

        IMMarginAnalystData imMarginAnalystData = IMMarginAnalystDataService.calculateMarginAnalystData(fileUUID);
        IMMarginAnalystSummary imMarginAnalystSummary = IMMarginAnalystDataService.calculateMarginAnalystSummary(fileUUID);

        return Map.of(
                "marginAnalystData", imMarginAnalystData,
                "marginAnalystSummary", imMarginAnalystSummary
        );
    }
}

