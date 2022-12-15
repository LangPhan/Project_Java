package com.controllers;

import com.commons.UserConstant;
import com.models.Account;
import com.models.User;
import com.repositories.AccountRepository;
import com.services.UserService;
import com.utils.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class AccountController {
    String message = "";
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private SendEmail sendEmail;

    /*private List<String> getRolesByLoggedUser(Principal principal){
        String roles = getLoggedUser(principal).getRole();
        List<String> assignRoles = Arrays.stream(roles.split(",")).toList();
        if(assignRoles.contains("ROLE_ADMIN")){
            return Arrays.stream(UserConstant.ADMIN_ACCESS).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }*/

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
        if(account.getUsername().length() < 6){
            message = "Tên đăng nhập tối thiểu có 6 kí tự";
            model.addAttribute("message",message);
            return "users/register";
        }
        if(account.getPassword().length() < 8){
            message = "Mật tối thiểu có 8 kí tự";
            model.addAttribute("message",message);
            return "users/register";
        }
        if(!Objects.equals(account.getPassword(), repass)){
            message = "Mật khẩu không khớp";
            model.addAttribute("message",message);
            return "users/register";
        }
        if(accountRepository.findByUsername(account.getUsername()).isPresent()){
            message = "Tên tài khoản đã tồn tại! Vui lòng sử dụng tên khác";
            model.addAttribute("message",message);
            return "users/register";
        }
        account.setRole(UserConstant.DEFAULT_ROLE);
        String encryptedPwd = passwordEncoder.encode(account.getPassword());
        account.setPassword(encryptedPwd);
        accountRepository.save(account);
        String notify = "Đăng kí tài khoản thành công";
        model.addAttribute("notify", notify);
        return "users/login";
    }
    @GetMapping("/update-info/{id}")
    public String getUpdateInfo(Model model, @PathVariable String id,
                                @CookieValue(value = "username", defaultValue = "") String username){
        Optional<Account> account = accountRepository.findByUsername(username);
        if(account.isPresent()){
            if(account.get().getId() != Long.parseLong(id)){
                return "redirect:/user/update-info/"+account.get().getId();
            }
        }
        User user;
        if(account.get().getUser() == null){
             user = new User();
        }else{
             user = account.get().getUser();
        }
        if(model.containsAttribute("user_exist")){
            model.addAttribute("user_exits",model.getAttribute("user_exist"));
        }else{
            account.ifPresent(value -> model.addAttribute("user_exits", value.getUser()));
        }
        model.addAttribute(user);
        return "users/update-info";
    }
    @PostMapping("/update-info/{id}")
    public String postUpdateInfo(@ModelAttribute("user")User user,
                                 @PathVariable String id,Model model,
                                 @CookieValue(value = "username", defaultValue = "") String username){
        model.addAttribute("user_exist", user);
        Optional<Account> account = accountRepository.findByUsername(username);
        if(account.isPresent()){
            if(account.get().getId() != Long.parseLong(id)){
                return "redirect:/user/update-info/"+account.get().getId();
            }
        }
        if(user.getEmail().isEmpty()||user.getPhoneNumber().isEmpty()){
            model.addAttribute("message","Vui lòng điền đầy đủ email và số điện thoại");
            return getUpdateInfo(model, id, username);
        }
        if(account.get().getUser() == null){
            if(userService.emailIsExist(user.getEmail())){
                model.addAttribute("message","Email đã tồn tại. Vui lòng sử dụng email khác");
                return getUpdateInfo(model, id, username);
            }
            if(userService.phoneIsExist(user.getPhoneNumber())){
                model.addAttribute("message",
                        "Số điện thoại đã được đăng kí. Vui lòng sử dụng số điện thoại khác khác");
                return getUpdateInfo(model, id, username);
            }
        }
        else{
            if(userService.emailIsExist(user.getEmail()) && !account.get().getUser().getEmail().equals(user.getEmail())){
                model.addAttribute("message","Email đã tồn tại. Vui lòng sử dụng email khác");
                return getUpdateInfo(model, id, username);
            }
            if(userService.phoneIsExist(user.getPhoneNumber()) && !account.get().getUser().getPhoneNumber().equals(user.getPhoneNumber())){
                model.addAttribute("message",
                        "Số điện thoại đã được đăng kí. Vui lòng sử dụng số điện thoại khác khác");
                return getUpdateInfo(model, id, username);
            }
            User user1 = account.get().getUser();
            if(user1.getFullName().equals(user.getFullName()) && user1.getEmail().equals(user.getEmail())
                    && user1.getPhoneNumber().equals(user.getPhoneNumber()) && user1.getAddress().equals(user.getAddress())){
                return "redirect:/";
            }
        }

        if(user.getPhoneNumber().length() != 10){
            model.addAttribute("message",
                    "Số điện thoại không đúng. Vui lòng kiểm tra lại");
            return getUpdateInfo(model, id, username);
        }
            account.get().setUser(user);
            accountRepository.save(account.get());
        return "redirect:/";
    }
    @GetMapping("/login")
    public String getLogin(Model model,
                           @CookieValue(name = "pass", defaultValue = "") String pass,
                           @CookieValue(name = "username", defaultValue = "") String username){
        Account account = new Account();
        model.addAttribute("account", account);
        model.addAttribute("user", username);
        model.addAttribute("pass", pass);
        return "users/login";
    }
    @PostMapping("/login")
    public String postsLogin(@ModelAttribute(name = "account") Account account, Model model,
                             HttpServletResponse response,
                             @RequestParam(value = "remember-me", defaultValue = "") String remember){
        Optional<Account> findUser = accountRepository.findByUsername(account.getUsername());
        if(findUser.isEmpty()){
            message = "Tên đăng nhập và mật khẩu không chính xác";
            model.addAttribute("message", message);
            return "users/login";
        }else{
            if(passwordEncoder.matches(account.getPassword(), findUser.get().getPassword())){
                Cookie userCookie = new Cookie("username",account.getUsername());
                userCookie.setMaxAge(60*60*24);
                userCookie.setPath("/");
                response.addCookie(userCookie);
                if(remember.equals("on")){
                    Cookie pass = new Cookie("pass", account.getPassword());
                    pass.setMaxAge(60*60*24);
                    pass.setPath("/user/login");
                    pass.setPath("/user/renewpass");
                    response.addCookie(pass);
                }
                if(findUser.get().getUser() == null){
                    return "redirect:/user/update-info/"+findUser.get().getId();
                }
                return "redirect:/";
            }else{
                message = "Tên đăng nhập và mật khẩu không chính xác";
                model.addAttribute("message", message);
                return "users/login";
            }
        }
    }
    @GetMapping("/resetpassword")
    public String getForgotPassword(){
        return "users/resetpassword";
    }
    @PostMapping("/resetpassword")
    public String postForgotPassword(@RequestParam(name = "email") String email, Model model,
                                     @CookieValue(name = "pass", defaultValue = "") String pass,
                                     @CookieValue(name = "username", defaultValue = "") String username,
                                     HttpServletResponse response){
        model.addAttribute("user", username);
        model.addAttribute("pass", pass);
        if(!userService.emailIsExist(email)){
            model.addAttribute("message","Email không đúng");
            return "users/resetpassword";
        }
        Account account = userService.findAccountByEmail(email);
        Random random = new Random();
        int numRandom = random.nextInt(10000,99999);
        account.setUpdateAt(String.valueOf(numRandom));
        accountRepository.save(account);

        sendEmail.sendSimpleMail(account.getUser().getEmail(),"Mã nhận lại mật khẩu", String.valueOf(numRandom));

        Cookie acc = new Cookie("acc", account.getUsername());
        acc.setMaxAge(60*60*24);
        acc.setPath("/");
        response.addCookie(acc);
        return "users/confirm";
    }
    @GetMapping("/typecode")
    public String getTypeCode(@ModelAttribute(name = "message") String message,
                              @ModelAttribute(name = "account") Account account, Model model){
        model.addAttribute("account", account);
        return "users/confirm";
    }
    @PostMapping("/typecode")
    public String postTypeCode(@CookieValue(name = "acc") String username, Model model,
                               @RequestParam(name = "code") String code){
        Optional<Account> acc = accountRepository.findByUsername(username);
        if(!code.equals(acc.get().getUpdateAt())){
            model.addAttribute("message","Mã nhập không đúng");
            return "users/confirm";
        }
        return "users/renewpass";
    }
    @GetMapping("/renewpass")
    public String getRenewPass(){
        return "users/renewpass";
    }
    @PostMapping("/renewpass")
    public String postRenewPass(@CookieValue(name = "acc", defaultValue = "") String username,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name="re-password") String rePassword,Model model){
        if(password.length() < 8){
            model.addAttribute("message", "Mật khẩu phải có ít nhất 8 kí tự");
            return "users/renewpass";
        }
        if(!password.equals(rePassword)){
            model.addAttribute("message","Mật khẩu không khớp");
            return "users/renewpass";
        }
        Optional<Account> account = accountRepository.findByUsername(username);
        account.get().setPassword(passwordEncoder.encode(password));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        account.get().setUpdateAt(dateFormat.format(date));
        accountRepository.save(account.get());
        model.addAttribute("notify","Phục hồi tài khoản thành công");
        return getLogin(model, password, username);
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
