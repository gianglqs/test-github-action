package com.hysteryale.service;

import com.hysteryale.repository.RegionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegionService {

    @Resource
    RegionRepository regionRepository;

    public List<Map<String, String>> getAllRegionForFilter() {
        List<Map<String, String>> listRegion = new ArrayList<>();
        List<String> regions = regionRepository.findAllRegion();
        for (String region : regions) {
            Map<String, String> mapRegion = new HashMap<>();
            mapRegion.put("value", region);
            listRegion.add(mapRegion);
        }
        return listRegion;
    }

}
