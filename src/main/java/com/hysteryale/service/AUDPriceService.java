package com.hysteryale.service;

import com.hysteryale.model.AUDPrice;
import com.hysteryale.repository.AUDPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AUDPriceService {
    @Autowired
    private AUDPriceRepository audPriceRepository;

    public AUDPriceService(AUDPriceRepository audPriceRepository) {
        this.audPriceRepository = audPriceRepository;
    }

    public void addListOfAUDPrices(List<AUDPrice> audPriceList) {
        audPriceRepository.saveAll(audPriceList);
    }
}
