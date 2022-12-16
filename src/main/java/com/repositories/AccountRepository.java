package com.repositories;

import com.models.Account;
import com.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String Username);
    @Query("SELECT a FROM Account a WHERE a.username LIKE %?1%"
            + " OR a.role LIKE %?1%")
    List<Account> searchAccount(String keyword);
}
