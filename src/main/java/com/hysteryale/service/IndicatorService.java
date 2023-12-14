package com.hysteryale.service;

import com.hysteryale.model.competitor.CompetitorColor;
import com.hysteryale.model.competitor.CompetitorPricing;
import com.hysteryale.model.filters.FilterModel;
import com.hysteryale.model.filters.SwotFilters;
import com.hysteryale.repository.CompetitorColorRepository;
import com.hysteryale.repository.CompetitorPricingRepository;
import com.hysteryale.utils.ConvertDataFilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
public class IndicatorService extends BasedService {
    @Resource
    CompetitorPricingRepository competitorPricingRepository;
    @Resource
    CompetitorColorRepository competitorColorRepository;


    public Map<String, Object> getCompetitorPriceForTableByFilter(FilterModel filterModel) throws ParseException {
        logInfo(filterModel.toString());
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        List<CompetitorPricing> competitorPricingList = competitorPricingRepository.findCompetitorByFilterForTable(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(1), (Pageable) filterMap.get("pageable"));
        result.put("listCompetitor", competitorPricingList);

        //get total Recode
        int totalCompetitor = competitorPricingRepository.getCountAll(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(1));
        result.put("totalItems", totalCompetitor);

        // get Total
        List<CompetitorPricing> getTotal = competitorPricingRepository.getTotal( filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(1));
        result.put("total", getTotal);

        return result;
    }


    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByRegion(FilterModel filterModel) throws ParseException {
        logInfo(filterModel.toString());
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        return competitorPricingRepository.findCompetitorByFilterForLineChartRegion(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(1));
    }


    public List<CompetitorPricing> getCompetitorPricingAfterFilterAndGroupByPlant(FilterModel filterModel) throws ParseException {
        logInfo(filterModel.toString());
        Map<String, Object> filterMap = ConvertDataFilterUtil.loadDataFilterIntoMap(filterModel);
        return competitorPricingRepository.findCompetitorByFilterForLineChartPlant(
                filterMap.get("regionFilter"), filterMap.get("plantFilter"), filterMap.get("metaSeriesFilter"),
                filterMap.get("classFilter"), filterMap.get("modelFilter"), filterMap.get("ChineseBrandFilter"),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(0),
                ((List) filterMap.get("marginPercentageFilter")).isEmpty() ? null: ((List) filterMap.get("marginPercentageFilter")).get(1));
    }

    public List<CompetitorPricing> getCompetitiveLandscape(SwotFilters filters) {

        String regions = filters.getRegions();
        List<String> countryNames = filters.getCountries();
        String competitorClass = filters.getClasses();
        String category = filters.getCategories();
        List<String> series = filters.getSeries();

        if(countryNames.isEmpty())
            countryNames = null;
        if(series.isEmpty())
            series = null;
        return competitorPricingRepository.getDataForBubbleChart(Collections.singletonList(regions), countryNames, Collections.singletonList(competitorClass), Collections.singletonList(category), series);
    }

    /**
     * Get CompetitorColor by competitorName
     * @return competitor color if existed, else randomly generate new one.
     */
    public CompetitorColor getCompetitorColor(String competitorName) {
        Optional<CompetitorColor> optional = competitorColorRepository.getCompetitorColor(competitorName.strip());
        if(optional.isEmpty()) {
            Random random = new Random();
            int nextColorCode = random.nextInt(256 * 256 * 256);
            String colorCode = String.format("#%06x", nextColorCode);
            return competitorColorRepository.save(new CompetitorColor(competitorName, colorCode));
        }
        else
            return optional.get();
    }

    public CompetitorColor getCompetitorById(int id) {
        Optional<CompetitorColor> optional = competitorColorRepository.findById(id);
        if(optional.isPresent())
            return optional.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Competitor Color not found");
    }

    public Page<CompetitorColor> searchCompetitorColor(String search, int pageNo, int perPage) {
        Pageable pageable = PageRequest.of(pageNo - 1, perPage, Sort.by("competitorName").ascending());
        return competitorColorRepository.searchCompetitorColor(search, pageable);
    }

    @Transactional
    public void updateCompetitorColor(CompetitorColor modifyColor) {
        Optional<CompetitorColor> optional = competitorColorRepository.findById(modifyColor.getId());
        if(optional.isPresent()) {
            CompetitorColor dbCompetitorColor = optional.get();

            dbCompetitorColor.setCompetitorName(modifyColor.getCompetitorName());
            dbCompetitorColor.setColorCode(modifyColor.getColorCode());
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Competitor Color not found");
    }
}
