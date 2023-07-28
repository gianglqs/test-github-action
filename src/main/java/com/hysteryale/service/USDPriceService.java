package com.hysteryale.service;

import com.hysteryale.model.USDPrice;
import com.hysteryale.repository.USDPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class USDPriceService {
    @Autowired
    private USDPriceRepository usdPriceRepository;

    public USDPriceService(USDPriceRepository usdPriceRepository) {
        this.usdPriceRepository = usdPriceRepository;
    }

    public List<USDPrice> getAllUSDPrices(){
        return usdPriceRepository.findAll();
    }

    public void addListOfUSDPrices(List<USDPrice> usdPriceList){
        usdPriceRepository.saveAll(usdPriceList);
    }
}
