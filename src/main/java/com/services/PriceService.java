package com.services;

import com.models.Price;
import com.repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;

    public void savePrice(Price price){
        priceRepository.save(price);
    }

    public Optional<Price> findPriceById(Long id){
       return priceRepository.findById(id);
    }
}
