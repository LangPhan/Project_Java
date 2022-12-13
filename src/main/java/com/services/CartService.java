package com.services;

import com.commons.CartConstant;
import com.models.Account;
import com.models.Cart;
import com.models.CartItem;
import com.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    public Optional<Cart> findCartById(Long id){
        return cartRepository.findById(id);
    }
    public void save(Cart cart){
        cartRepository.save(cart);
    }
    public List<Cart> findAllCart(){
        return cartRepository.findAll();
    }
    public Cart findCartIsOrdering(List<Cart> cartList, Account account){
        cartList = cartRepository.findAllByAccount(account);
        if(cartList.isEmpty()){
            return new Cart();
        }
        for(Cart cart: cartList){
            if(cart.getStatus().equals(CartConstant.ORDERING)){
                return cart;
            }
        }
        return new Cart();
    }
    public  List<Cart> findAllIsOrdered(List<Cart> cartList, Account account){
        cartList = cartRepository.findAllByAccount(account);
        List<Cart> listCartOrdered = new ArrayList<>();
        if(cartList.isEmpty()){
            return null;
        }
        for(Cart cart: cartList){
            if(cart.getStatus().equals(CartConstant.ORDERED)){
                listCartOrdered.add(cart);
            }
        }
        return listCartOrdered;
    }
}
