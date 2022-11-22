package com.repositories;

import com.models.Category;
import com.models.Price;
import com.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
            + " OR p.description LIKE %?1%"
            + " OR CONCAT(p.price, '') LIKE %?1%")
    List<Product> search(String keyword);

    List<Product> findAllByCategoryIs(Category category);
}
