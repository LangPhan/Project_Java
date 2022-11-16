package com.controllers;

import com.models.Account;
import com.repositories.AccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private AccountRepository accountRepository;
    private Account getLoggedUser(Principal principal){
        return accountRepository.findByUsername(principal.getName()).get();
    }

    @GetMapping("/")
    public String getRegister(Model model){
        Account account = new Account();
        model.addAttribute(account);
        return "admin/category";
    }
}
