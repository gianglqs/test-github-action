package com.hysteryale.service;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
     */
    public Optional<Account> getAccountById(Integer accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No account with id: " + accountId);
        return account;
    }

    /**
     * Adding new account with the encrypted password if the registered email is not existed
     * @param account : new registered Account
     */
    public void addAccount(Account account){

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

    /**
     * Getting an account which is still active by email
     */
    public Optional<Account> getActiveAccountByEmail(String email) {return accountRepository.getActiveAccountByEmail(email); }

    @Transactional
    public void changeDefaultLocale(Account account, String locale) {
        account.setDefaultLocale(locale);
    }

    /**
     * Set account's isActive state (isActive: true or false)
     */
    @Transactional
    public void setAccountActiveState(Account account, boolean isActive) {
        account.setActive(isActive);
    }

    /**
     * Update account's information: userName ,role, defaultLocale
     * @param dbAccount account get from Database
     * @param updateAccount account contained changed information
     */
    @Transactional
    public void updateAccountInformation(Account dbAccount, Account updateAccount) {
        dbAccount.setUserName(updateAccount.getUserName());
        dbAccount.setRole(updateAccount.getRole());
        dbAccount.setDefaultLocale(updateAccount.getDefaultLocale());
    }
    @Transactional
    public void changeAccountPassword(Account account, String password) {
        account.setPassword(passwordEncoder().encode(password));
    }
    @Transactional
    public void setNewLastLogin(Account account) {
        account.setLastLogin(new Date());
    }
    public List<Account> searchAccountByUserName(String userName) {
        return accountRepository.searchAccountByUserName(userName);
    }
}
