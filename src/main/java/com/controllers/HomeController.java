package com.controllers;

import com.models.Account;
import com.models.Category;
import com.models.Product;
import com.repositories.AccountRepository;
import com.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("")

public class HomeController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public String getHome(@CookieValue(value = "username", defaultValue = "") String username, Model model){
        Optional<Account> accountSaved = accountRepository.findByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        List<Product> productsTop6th = productService.findTop6ByCreatedAt();
        if(accountSaved.isPresent()) {
            if (accountSaved.get().getRole().equals("ROLE_ADMIN")) {
                return "redirect:/admin";
            }
            if (accountSaved.get().getRole().equals("ROLE_EMPLOYEE")) {
                return "redirect:/employees";
            }
        }
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
    @GetMapping("/category/{id}")
    public String getProductCategory(@PathVariable Long id,
                                     @CookieValue(value = "username", defaultValue = "") String username,
                                     Model model){
        Optional<Account> accountSaved = accountRepository.findByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        Optional<Category> categoryName = categoryService.getCategoryById(id);
        if(!categoryName.isPresent()){
            return "home/404";
        }
        if(!Objects.equals(username, "")){
            model.addAttribute("account", accountSaved);
        }else{
            model.addAttribute("account",null);
        }
        List<Product> listProduct = productService.findProductByCategory(categoryName.get());
        model.addAttribute("categories", categories);
        model.addAttribute("products",listProduct);
        model.addAttribute("category", categoryName);

        return "home/category";
    }
    @GetMapping("/search")
    public String getSearch(@CookieValue(value = "username", defaultValue = "") String username,
                            Model model, @RequestParam(name = "keyword") String keyword){
        Optional<Account> accountSaved = accountRepository.findByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.searchProductByKeyword(keyword);
        if(!Objects.equals(username, "")){
            model.addAttribute("account", accountSaved);
        }else{
            model.addAttribute("account",null);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "home/search";
    }
    @GetMapping("/no-login")
    public String handleNoLogin(){
        return "home/404";
    }

}
