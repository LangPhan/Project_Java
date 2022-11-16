package com.controllers;

import com.models.Account;
import com.repositories.AccountRepository;
import com.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    private Account getLoggedUser(Principal principal){
        return accountRepository.findByUsername(principal.getName()).get();
    }

    @GetMapping("/")
    public String getAdmin(Model model){
        return "admin/index";
    }

    @GetMapping("/category")
    public String getCategory(Model model){
        return "admin/category";
    }

    @GetMapping("/userlist")
    public String getUsers(Model model){
        List<Account> accounts = accountService.findAllAccount();
        model.addAttribute("accountList",accounts);
        return "admin/users";
    }

    @GetMapping("/userlist/edit/{id}")
    public ModelAndView gettingEdit(@PathVariable(name= "id") int id){
        ModelAndView mav = new ModelAndView("admin/edituser");
        Optional<Account> user = accountService.findAccountById(id);
        mav.addObject("user", user);
        return mav;
    }
//    @PostMapping("/edit/{id}")
//    public String postingEdit(@ModelAttribute("employee") Employee employee){
//        service.save(employee);
//        return "redirect:/employees/";
//    }
}
