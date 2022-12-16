package com.services;

import com.models.Category;
import com.models.Product;
import com.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void saveProduct(Product product){
        productRepository.save(product);
    }

    public Optional<Product> findProductById(Long id){
        return productRepository.findById(id);
    }

    public void deleteProductById(Long id){
        productRepository.deleteById(id);
    }


    public List<Product> listProduct(){
        return (List<Product>) productRepository.findAll();
    }

    public Page<Product> pageProducts(int pageNo){
        Pageable pageable = PageRequest.of(pageNo,5);
        Page<Product> page = productRepository.pageProduct(pageable);
        return page;
    }

    public Page<Product> pageProductsandSort(int pageNo,String sortField,String sortDir, String keyword){

        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );
        if(!Objects.equals(keyword, "")){
            Page<Product> products = productRepository.searchProduct(keyword, pageable);
            return products;
        }
        return productRepository.findAll(pageable);
    }

    public Page<Product> searchProduct(int pageNo,String keyword){
        Pageable pageable = PageRequest.of(pageNo,5);
        Page<Product> products = productRepository.searchProduct(keyword, pageable);
        return products;
    }
    public List<Product> searchProductByKeyword(String keyword){
        return productRepository.searchProductByKeyword(keyword);
    }
    public List<Product> findTop6ByCreatedAt(){
        return productRepository.findTop6ByOrderByCreatedAtDesc();
    }
    public List<Product> findProductByCategory(Category category){
        return productRepository.findProductByCategory(category).stream()
                .sorted((p1,p2) -> p1.getPrice().getSizeS() < p2.getPrice().getSizeS() ? 1 : 0)
                .collect(Collectors.toList());
    }
}
