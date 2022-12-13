package com.services;

import com.models.CartItem;
import com.repositories.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;


    public void save(CartItem cartItem){
        cartItemRepository.save(cartItem);
    }

    public Optional<CartItem> findCardItemById(Long id){
        return cartItemRepository.findById(id);
    }
    public void deleteById(Long id){
        cartItemRepository.deleteById(id);
    }
    public void delete(CartItem cartItem){
        cartItemRepository.delete(cartItem);
    }
}
