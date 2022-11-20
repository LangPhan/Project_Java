package com.services;

import com.models.User;
import com.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
}
