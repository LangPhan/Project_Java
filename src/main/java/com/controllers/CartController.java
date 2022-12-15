package com.controllers;

import com.commons.CartConstant;
import com.models.*;
import com.repositories.AccountRepository;
import com.repositories.CartRepository;
import com.services.*;
import com.utils.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("cart")
public class CartController {
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
    @Autowired
    private CartService cartService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private SendEmail sendEmail;

    @GetMapping("")
    public String getCart(@CookieValue(value = "username", defaultValue = "") String username, Model model){
        Optional<Account> accountSaved = accountRepository.findByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        Optional<Account> account = accountRepository.findByUsername(username);
        Cart cart = cartService.findCartIsOrdering(cartService.findAllCart(), accountSaved.get());
        if(!Objects.equals(username, "")){
            model.addAttribute("account", accountSaved);
        }else{
            model.addAttribute("account",null);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("cart",cart);
        model.addAttribute("user_exits",account.get().getUser());
        return "home/cart";
    }

    @PostMapping("/add/{id}")
    public String postCartItem(@PathVariable Long id,
                             @RequestParam(name = "price") Double price,
                             @RequestParam(name = "quantity") int quantity,
                               @CookieValue(name = "username", defaultValue = "") String username){
        Optional<Account> account_saved = accountService.findAccountByUsername(username);
        Cart cart = cartService.findCartIsOrdering(cartService.findAllCart(), account_saved.get());
        cart.setAccount(account_saved.get());

        productService.findProductById(id);
        CartItem cartItem = new CartItem();
        cartItem.setProduct(productService.findProductById(id).get());
        cartItem.setPrice(price);
        cartItem.setQuantity(quantity);
        cartItem.setCart(cart);
        cartItemService.save(cartItem);
        double total = 0;
        if(cart.getCartItem()!=null){
            for(CartItem item: cart.getCartItem()){
                total += item.getPrice() * item.getQuantity();
            }
        }else{
            total = cartItem.getPrice();
        }
        cart.setTotal(total);
        cartService.save(cart);

        return "redirect:/cart";
    }
    @PostMapping("/pay/{id}")
    public String postPayment(@PathVariable Long id,
                              @CookieValue(value = "username", defaultValue = "") String username,
                              @RequestParam(name = "fullName")String fullName,
                              @RequestParam(name = "phoneNumber") String phoneNumber,
                              @RequestParam(name = "address") String address,
                              @RequestParam(name = "email") String email,
                              Model model){
        Optional<Account> account_saved = accountService.findAccountByUsername(username);
        Cart cart = cartService.findCartIsOrdering(cartService.findAllCart(), account_saved.get());
        cart.setStatus(CartConstant.ORDERED);
        cartService.save(cart);
        Receipt newReceipt = new Receipt();
        newReceipt.setAddress(address);
        newReceipt.setEmail(email);
        newReceipt.setFullName(fullName);
        newReceipt.setPhoneNumber(phoneNumber);
        newReceipt.setCart(cart);
        receiptService.save(newReceipt);
        model.addAttribute("message", "Thanh toán thành công");
        String msg = "Người đặt hàng: "+username+"\n"
                +"Số điện thoại: "+ phoneNumber+"\n"
                +"Địa chỉ: "+address+"\n"
                +"Tổng tiền thanh toán: "+cart.getTotal()+" VND\n"
                +"Sản phẩm sẽ được giao cho bạn sớm thôi!";
        sendEmail.sendSimpleMail(email,"Thanh toán đơn hàng thành công",msg);
        return getCart(username,model);
    }
    @GetMapping("/history")
    public String getHistory(@CookieValue(value = "username", defaultValue = "") String username, Model model){
        Optional<Account> accountSaved = accountRepository.findByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        List<Cart> cart = cartService.findAllIsOrdered(cartService.findAllCart(), accountSaved.get());
        cart =  cart.stream()
                .sorted(Comparator.comparing(Cart::getUpdateAt).reversed())
                .collect(Collectors.toList());
        if(!Objects.equals(username, "")){
            model.addAttribute("account", accountSaved);
        }else{
            model.addAttribute("account",null);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("cart",cart);
        return "home/history";
    }
    @PostMapping("/delete/{id}")
    public String postDeleteItem(@PathVariable Long id){
        CartItem cartItem = cartItemService.findCardItemById(id).get();
        cartItem.setCart(null);
        cartItem.setProduct(null);
        cartItemService.save(cartItem);
        cartItemService.delete(cartItem);
        return "redirect:/cart";
    }
    @PostMapping("/edit/{id}")
    public String putEditItemCart(@PathVariable Long id, @RequestParam(name = "price") Double price,
                                  @RequestParam(name = "quantity") int quantity){
        CartItem cartItem = cartItemService.findCardItemById(id).get();
        cartItem.setPrice(price);
        cartItem.setQuantity(quantity);
        cartItemService.save(cartItem);
        return "redirect:/cart";
    }
}
