package com.hysteryale.controller;

import com.hysteryale.service.MetaSeriesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@RestController
public class MetaSeriesController {
    @Resource
    MetaSeriesService metaSeriesService;

    /**
     * For testing only, will be removed later
     */
    @PostMapping(path = "/metaSeries/import")
    void importMetaSeries() throws FileNotFoundException, IllegalAccessException {
        metaSeriesService.importMetaSeries();
    }
}
