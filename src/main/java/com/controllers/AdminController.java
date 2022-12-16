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
import com.utils.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @GetMapping("/user/search")
    public String getSearch(Model model,
                            @RequestParam(name = "keyword") String keyword){
        List<Account> accounts = accountService.search(keyword);
        model.addAttribute("accountList", accounts);
        model.addAttribute("keyword", keyword);
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
    @PostMapping("/category/edit/{id}")
    public String editCategory(@PathVariable Long id, @RequestParam(name = "name") String name){
        Category category = categoryService.findById(id).get();
        category.setName(name);
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }
    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return "redirect:/admin/category";
    }
    @GetMapping("/product/search")
    public String getSearchKeyWord(Model model,
                            @RequestParam(name = "keyword") String keyword){
        List<Product> products = productService.searchProductByKeyword(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "admin/products";
    }

    @GetMapping("/product/pagingandsort/{pageNum}")
    public String viewProductsByPagingAndSorting(Model model,
                                       @PathVariable(name = "pageNum") int pageNum,
                                                 @Param("sortField") String sortField,
                                                 @Param("sortDir") String sortDir,
                                                 @Param("keyword") String keyword){
        Page<Product> products = productService.pageProductsandSort(pageNum,sortField,sortDir,keyword);

        List<Product> listProducts = products.getContent();
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("size", products.getSize());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("productsList", products); // next bc of thymeleaf we make the index.html
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        return "admin/productsPaging";
    }


    @RequestMapping("/product")
    public String viewHomePage(Model model) {
        return "redirect:/admin/product/pagingandsort/0?sortField=name&sortDir=asc";
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
                                    @RequestParam(name = "image") MultipartFile multipartFile,
                                        Model model) throws IOException {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        if(!filename.isEmpty()){
            product.setImg(filename);
            UploadFile.uploadFiles(multipartFile, filename);
        }else{
            product.setImg(null);
        }
        Price price = new Price(priceS,priceM,priceL);
        priceService.savePrice(price);
        product.setPrice(price);
        productService.saveProduct(product);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("message","Thêm sản phẩm thành công");

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
                                     @RequestParam(name = "priceL") Double priceL,
                                     @RequestParam(name = "image", defaultValue = "") MultipartFile multipartFile,
                                     Model model) throws IOException{

        String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        if(!filename.isEmpty()){
            product.setImg(filename);
            UploadFile.uploadFiles(multipartFile, filename);
        }else{
            product.setImg(productService.findProductById(product.getId()).get().getImg());
        }

        Long idPrice = productService.findProductById(product.getId()).get().getPrice().getId();
        Optional<Price> price = priceService.findPriceById(idPrice);
        if(price.isPresent()){
            price.get().setSizeS(priceS);
            price.get().setSizeM(priceM);
            price.get().setSizeL(priceL);
            product.setPrice(price.get());
        }
        productService.saveProduct(product);
        model.addAttribute("message","Chỉnh sửa sản phẩm thành công");
       return "redirect:/admin/product";
    }
    @PostMapping("product/delete/{id}")
    public String postingDeleteProduct(@PathVariable("id") Long id, Model model){
       if(productService.findProductById(id).isPresent()){
            productService.findProductById(id).get().setCategory(null);
            productService.findProductById(id).get().setPrice(null);
            productService.deleteProductById(id);
            model.addAttribute("message","Xóa sản phẩm thành công");
            return viewHomePage(model);
        }
        return viewHomePage(model);
    }


}
