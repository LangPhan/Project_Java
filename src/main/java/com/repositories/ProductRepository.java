package com.repositories;

import com.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
            + " OR p.description LIKE %?1%"
            + " OR CONCAT(p.price, '') LIKE %?1%")
    Page<Product> searchProduct(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p")
    Page<Product> pageProduct(Pageable pageable);
//    public List<Product> findAll(String keyword);

}
