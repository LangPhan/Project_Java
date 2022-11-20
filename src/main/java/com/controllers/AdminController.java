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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    //Index ADMIN
    @GetMapping("")
    public String getAdmin(){
        return "admin/index";
    }

    // USER MANAGEMENT
    @GetMapping("/user")
    public String getUsers(Model model){
        List<Account> accounts = accountService.findAllAccount();
        model.addAttribute("accountList",accounts);
        return "admin/users";
    }

    @GetMapping("/user/edit/{id}")
    public ModelAndView gettingEdit(@PathVariable(name= "id") int id){
        ModelAndView mav = new ModelAndView("admin/edit-user");
        Optional<Account> user = accountService.findAccountById(id);
        user.ifPresent(account -> mav.addObject("user", account));
        return mav;
    }
   @PostMapping("/user/edit/{id}")
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
        return "redirect:/admin/user";
    }
    @PostMapping("/user/delete/{id}")
    public String postingDelete(@PathVariable("id") Long id, Model model){
        String message = "";
        model.addAttribute("message",message);
        accountService.deleteAccount(id);
        return "admin/users";
    }


    //CATEGORY MANAGEMENT
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

    //PRODUCT MANAGEMENT
    @GetMapping("/product")
    public String gettingProduct(Model model){
        List<Product> products = productService.findAllProduct();
        model.addAttribute("products", products);
        return "admin/products";
    }
    @GetMapping("product/add")
    public String gettingAddProduct(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/add-product";
    }
    @PostMapping("product/add")
        public String postingAddProduct(@ModelAttribute("product")Product product,
                                    @RequestParam(name = "priceS") Double priceS,
                                    @RequestParam(name = "priceM") Double priceM,
                                    @RequestParam(name = "priceL") Double priceL,
                                    @RequestParam(name = "image") MultipartFile multipartFile) throws IOException {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//        giang
        product.setImg(filename);

//        giang
        Price price = new Price(priceS,priceM,priceL);
        priceService.savePrice(price);
        product.setPrice(price);
        productService.saveProduct(product);

//        String uploadDir = "uploads/";
        Path uploadPath = Paths.get("uploads/");
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            throw new IOException("Could not upload file "+ filename);
        }

        return "admin/add-product";
    }
    @GetMapping("product/detail/{id}")
    public ModelAndView gettingDetailProduct(@PathVariable("id") Long id){
        Optional<Product> product = productService.findProductById(id);
        ModelAndView modelAndView = new ModelAndView("admin/edit-product");
        modelAndView.addObject("product",product);
        modelAndView.addObject("category_id", categoryService.getCategoryById(product.get().getCategory().getId()));
        modelAndView.addObject("price", priceService.findPriceById(product.get().getPrice().getId()));
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.addObject("detail", true);
        return modelAndView;
    }
    @GetMapping("product/edit/{id}")
    public String gettingEditProduct(@PathVariable("id") Long id, Model model){
        Optional<Product> product = productService.findProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("category_id", categoryService.getCategoryById(product.get().getCategory().getId()));
        model.addAttribute("price", priceService.findPriceById(product.get().getPrice().getId()));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/edit-product";
    }
    @PostMapping("product/edit/{id}")
    public String postingEditProduct(@ModelAttribute(name = "product") Product product,
                                     @RequestParam(name = "priceS") Double priceS,
                                     @RequestParam(name = "priceM") Double priceM,
                                     @RequestParam(name = "priceL") Double priceL
                                     ){
        Long idPrice = productService.findProductById(product.getId()).get().getPrice().getId();
        Optional<Price> price = priceService.findPriceById(idPrice);
        if(price.isPresent()){
            price.get().setSizeS(priceS);
            price.get().setSizeM(priceM);
            price.get().setSizeL(priceL);
            product.setPrice(price.get());
        }
        productService.saveProduct(product);
       return "redirect:/admin/product";
    }
    @PostMapping("product/delete/{id}")
    public String postingDeleteProduct(@PathVariable("id") Long id, Model model){
        if(productService.findProductById(id).isPresent()){
            productService.findProductById(id).get().setCategory(null);
            productService.findProductById(id).get().setPrice(null);
            productService.deleteProductById(id);
            model.addAttribute("message","Xóa sản phẩm thành công");
            return gettingProduct(model);
        }
        return gettingProduct(model);
    }
}
