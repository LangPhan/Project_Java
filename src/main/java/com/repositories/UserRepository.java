package com.repositories;

import com.models.Account;
import com.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmail(String email);
    boolean existsUserByPhoneNumber(String phone);

    User findByEmail(String email);
}
