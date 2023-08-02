package com.hysteryale.controller;

import com.hysteryale.model.Price;
import com.hysteryale.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class PriceBookController {
    @Autowired
    private PriceService priceService;

    public PriceBookController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping(path = "/price")
    public List<Price> getAllPrices() {
        return priceService.getAllPrices();
    }

    @GetMapping(path = "/price/series/{strSeries}")
    public List<Price> getPricesBySeries(@PathVariable String strSeries) {
        List<Price> priceList = priceService.getPricesBySeries(strSeries);
        if(!priceList.isEmpty())
            return priceList;
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Price found with series: " + strSeries);
    }
}
