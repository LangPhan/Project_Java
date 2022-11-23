package com.services;

import com.models.Category;
import com.models.Product;
import com.repositories.CategoryRepository;
import com.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> findAllProduct(int pageNum) {
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        return productRepository.findAll(pageable);
    }
//    public List<Product> findAllProduct(){
//        return productRepository.findAll();
//    }
    public void saveProduct(Product product){
        productRepository.save(product);
    }

    public Optional<Product> findProductById(Long id){
        return productRepository.findById(id);
    }

    public void deleteProductById(Long id){
        productRepository.deleteById(id);
    }

    public List<Product> listAll(String keyword) {
        if (keyword != null) {
            return productRepository.search(keyword);
        }
        return productRepository.findAll();
    }
    public List<Product> findTop6ByCreatedAt(){
        return productRepository.findTop6ByOrderByCreatedAtDesc();
    }
    public List<Product> findProductByCategory(Category category){
        return productRepository.findProductByCategory(category);
    }
}
