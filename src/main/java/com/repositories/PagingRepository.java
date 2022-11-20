package com.repositories;

import com.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PagingRepository extends PagingAndSortingRepository<Product, Long> {
    Iterable<Product> findAll(Sort sort);
    Page<Product> findAll(Pageable pageable);
}
