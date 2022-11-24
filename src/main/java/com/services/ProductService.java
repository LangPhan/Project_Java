package com.services;

import com.models.Product;
import com.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

//    public Page<Product> listAll(int page, String sortField, String sortDir,String keyword) {
//        Sort sort = Sort.by(sortField);
//        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
//
//        Pageable pageable = PageRequest.of(page - 1, 7, sort); // 7 rows per page
//
//        if (keyword != null) {
//            return productRepository.findByNameContaining(keyword,pageable);
//        }
//        return productRepository.findAll(pageable);
//    }

//    public Page<Product> findAll(Pageable page){
//        return findAll(page);
//    }

    public List<Product> listProduct(){
        return (List<Product>) productRepository.findAll();
    }

    public Page<Product> pageProducts(int pageNo){
        Pageable pageable = PageRequest.of(pageNo,5);
        Page<Product> page = productRepository.pageProduct(pageable);
        return page;
    }

    public Page<Product> pageProductsandSort(int pageNo,String sortField,String sortDir ){

        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        return productRepository.findAll(pageable);
    }

    public Page<Product> searchProduct(int pageNo,String keyword){
        Pageable pageable = PageRequest.of(pageNo,5);
        Page<Product> products = productRepository.searchProduct(keyword,pageable);
        return products;
    }

}
