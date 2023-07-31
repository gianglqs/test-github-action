package com.hysteryale.service;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieving all the accounts
     * @return List of existed account
     */
    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    /**
     * Getting an account by the given Id
     * @param accountId: given Id
     * @return an Account
     * @throws NotFoundException if there is not any account existed with given Id
     */

    public Optional<Account> getAccountById(Integer accountId) throws NotFoundException {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No account with id: " + accountId);
        return account;
    }

    /**
     * Adding new account with the encrypted password if the registered email is not existed
     * @param account : new registered Account
     * @throws Exception if the Email has already been taken
     */
    public void addAccount(Account account) throws Exception{

        if(!accountRepository.isEmailExisted(account.getEmail()))
        {
            // encrypt password
            account.setPassword(passwordEncoder().encode(account.getPassword()));
            accountRepository.save(account);
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email has been already taken");
    }

    /**
     * Getting an account by the email
     * @param email: given email
     * @return an Account
     */
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.getAccountByEmail(email);
    }

    @Transactional
    public void changeDefaultLocale(Account account, String locale) {
        account.setDefaultLocale(locale);
    }
}
