package com.hysteryale.repository;

import com.hysteryale.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}
