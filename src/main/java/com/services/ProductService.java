package com.services;

import com.models.Product;
import com.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAllProduct(){
        return productRepository.findAll();
    }
    public void saveProduct(Product product){
        productRepository.save(product);
    }

    public Optional<Product> findProductById(Long id){
        return productRepository.findById(id);
    }

    public void deleteProductById(Long id){
        productRepository.deleteById(id);
    }

}
