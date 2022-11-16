package com.controllers;

import com.models.Account;
import com.repositories.AccountRepository;
import com.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private AccountRepository accountRepository;
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
}
