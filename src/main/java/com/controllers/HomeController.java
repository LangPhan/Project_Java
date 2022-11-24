package com.controllers;

import com.models.Account;
import com.models.Category;
import com.models.Product;
import com.repositories.AccountRepository;
import com.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("")

public class HomeController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PriceService priceService;

    @GetMapping("")
    public String getHome(@CookieValue(value = "username", defaultValue = "") String username, Model model){
        Optional<Account> accountSaved = accountRepository.findByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        List<Product> productsTop6th = productService.findTop6ByCreatedAt();
        if(!Objects.equals(username, "")){
            model.addAttribute("account", accountSaved);
        }else{
            model.addAttribute("account",null);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("productTop6th", productsTop6th);
        return "home/home";
    }

    @GetMapping("detail/{id}")
    public String getDetailProduct(@CookieValue(value = "username", defaultValue = "") String username,
                                   Model model,
                                   @PathVariable("id") Long id){

        Optional<Account> accountSaved = accountRepository.findByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        Optional<Product> productDetail = productService.findProductById(id);
        List<Product> productsRelation = productService.findProductByCategory(productDetail.get().getCategory());
        if(!Objects.equals(username, "")){
            model.addAttribute("account", accountSaved);
        }else{
            model.addAttribute("account",null);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("productDetail", productDetail);
        model.addAttribute("productRelation", productsRelation);
        return "home/detail";
    }
}