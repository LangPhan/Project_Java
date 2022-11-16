package com.controllers;

import com.commons.UserConstant;
import com.models.Account;
import com.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class AccountController {
    String message = "";
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    private List<String> getRolesByLoggedUser(Principal principal){
        String roles = getLoggedUser(principal).getRole();
        List<String> assignRoles = Arrays.stream(roles.split(",")).toList();
        if(assignRoles.contains("ROLE_ADMIN")){
            return Arrays.stream(UserConstant.ADMIN_ACCESS).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Account getLoggedUser(Principal principal){
        return accountRepository.findByUsername(principal.getName()).get();
    }

    @GetMapping("/register")
    public String getRegister(Model model){
        Account account = new Account();
        model.addAttribute(account);
        return "users/register";
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("account") Account account, Model model,
                               @RequestParam(name = "repassword" ) String repass){
        if(!Objects.equals(account.getPassword(), repass)){
            message = "Password doesn't match with re-password";
            model.addAttribute("message",message);
            return "users/register";
        }
        if(accountRepository.findByUsername(account.getUsername()).isPresent()){
            message = "Username is exist! Please using another username";
            model.addAttribute("message",message);
            return "users/register";
        }
        account.setRole(UserConstant.DEFAULT_ROLE);
        String encryptedPwd = passwordEncoder.encode(account.getPassword());
        account.setPassword(encryptedPwd);
        accountRepository.save(account);
        String notify = "Register Successfully";
        model.addAttribute("notify", notify);
        return "users/login";
    }

    @GetMapping("/login")
    public String getLogin(Model model){
        Account account = new Account();
        model.addAttribute("account", account);
        return "users/login";
    }
    @PostMapping("/login")
    public String postsLogin(@ModelAttribute(name = "account") Account account, Model model,
                             HttpServletResponse response){
        Optional<Account> findUser = accountRepository.findByUsername(account.getUsername());
        if(findUser.isEmpty()){
            message = "Username or password was wrong";
            model.addAttribute("message", message);
            return "users/login";
        }else{
            if(passwordEncoder.matches(account.getPassword(), findUser.get().getPassword())){
                Cookie userCookie = new Cookie("username",account.getUsername());
                userCookie.setMaxAge(60*60*24);
                userCookie.setPath("/");
                response.addCookie(userCookie);
                return "redirect:/user/";
            }else{
                message = "Username or password was wrong";
                model.addAttribute("message", message);
                return "users/login";
            }
        }
    }
    @GetMapping("/logout")
    public String getLogout(HttpServletResponse response){
        Cookie cookie = new Cookie("username", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ("redirect:/user/login");
    }
    @GetMapping("/admin")
    public String getAdmin(){
        return "users/admin";
    }
    @GetMapping("/admin/edit")
    public String getAdminEdit(){
        return "users/admin";
    }
    @GetMapping("/404")
    public String getError(){
        return "users/404";
    }
    @GetMapping()
    public String getIndex(){
        return "home/index";
    }
}
