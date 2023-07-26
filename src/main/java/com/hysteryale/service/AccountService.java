package com.hysteryale.service;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieving all the accounts
     * @return List of existed account
     */
    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    /**
     * Adding new account with the encrypted password if the registered email is not existed
     * @param account : new registered Account
     * @throws Exception if the Email has been taken before
     */
    public void addAccount(Account account) throws Exception{

        if(!accountRepository.isEmailExisted(account.getEmail()))
        {
            // encrypt password
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(account);
        }
        else
            throw new Exception("Email has been already taken");
    }

    /**
     * Getting an account by the email
     * @param email: given email
     * @return an Account
     */
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.getAccountByEmail(email);
    }
}
