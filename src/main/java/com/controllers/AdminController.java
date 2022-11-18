package com.controllers;

import com.models.Account;
import com.models.Category;
import com.models.Price;
import com.models.Product;
import com.repositories.AccountRepository;
import com.services.AccountService;
import com.services.CategoryService;
import com.services.PriceService;
import com.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
    private Account getLoggedUser(Principal principal){
        return accountRepository.findByUsername(principal.getName()).get();
    }

    @GetMapping("")
    public String getAdmin(Model model){
        return "admin/index";
    }

    @GetMapping("/userlist")
    public String getUsers(Model model){
        List<Account> accounts = accountService.findAllAccount();
        model.addAttribute("accountList",accounts);
        return "admin/users";
    }

    @GetMapping("/userlist/edit/{id}")
    public ModelAndView gettingEdit(@PathVariable(name= "id") int id){
        ModelAndView mav = new ModelAndView("admin/edit-user");
        Optional<Account> user = accountService.findAccountById(id);
        user.ifPresent(account -> mav.addObject("user", account));
        return mav;
    }
   @PostMapping("/userlist/edit/{id}")
   public String postingEdit(@ModelAttribute("user") Account account,
                             @PathVariable("id") Long id){
        Optional<Account> accountSaved = accountService.findAccountById(id);
        if(accountSaved.isPresent()){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            accountSaved.get().setRole(account.getRole());
            accountSaved.get().setActive(account.isActive());
            accountSaved.get().setUpdateAt(dateFormat.format(date));
            accountService.save(accountSaved.get());
        }
        return "redirect:/admin/userlist";
    }
    @PostMapping("/userlist/delete/{id}")
    public String postingDelete(@PathVariable("id") Long id, Model model){
        String message = "";
        model.addAttribute("message",message);
        accountService.deleteAccount(id);
        return "admin/users";
    }

    @GetMapping("/category")
    public String getCategory(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/category";
    }
    @PostMapping("/category")
    public String addCategory(@ModelAttribute("category") Category category, Model model){
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }
    @GetMapping("product")
    public String gettingProduct(Model model){
        List<Product> products = productService.findAllProduct();
        model.addAttribute("products", products);
        return "admin/products";
    }
    @GetMapping("products/add")
    public String gettingAddProduct(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/add-product";
    }
    @PostMapping("products/add")
    public String postingAddProduct(@ModelAttribute("product")Product product,
                                    @RequestParam(name = "priceS") Double priceS,
                                    @RequestParam(name = "priceM") Double priceM,
                                    @RequestParam(name = "priceL") Double priceL){
        Price price = new Price(priceS,priceL,priceM);
        priceService.savePrice(price);
        product.setPrice(price);
        productService.saveProduct(product);
        return "admin/add-product";
    }
    @GetMapping("product/edit/{id}")
    public String gettingEditProduct(@PathVariable("id") Long id, Model model){
        Optional<Product> product = productService.findProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("category_id", categoryService.getCategoryById(id));
        model.addAttribute("price", priceService.findPriceById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/edit-product";
    }
    @PostMapping("product/edit/{id}")
    public String postingEditProduct(@ModelAttribute(name = "product") Product product,
                                     @RequestParam(name = "priceS") Double priceS,
                                     @RequestParam(name = "priceM") Double priceM,
                                     @RequestParam(name = "priceL") Double priceL){
        Optional<Price> price = priceService.findPriceById(product.getId());
        price.get().setSizeS(priceS);
        price.get().setSizeM(priceM);
        price.get().setSizeL(priceL);
        product.setPrice(price.get());
        productService.saveProduct(product);
       return "admin/products";
    }
}
