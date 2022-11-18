package com.repositories;

import com.models.Price;
import com.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
}
