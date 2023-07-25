package com.hysteryale.service;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    public AccountRepository accountRepository;

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }
    public void addAccount(Account account) {
       accountRepository.save(account);
    }
}
