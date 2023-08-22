package com.hysteryale.controller;

import com.hysteryale.model.Account;
import com.hysteryale.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@EnableResourceServer
@CrossOrigin
@Slf4j
public class AccountController {
    @Autowired
    public AccountService accountService;

    /**
     * Getting all accounts existed and filter to hide password
     * @return list of accounts
     */
    @GetMapping(path = "/accounts")
    @Secured("ROLE_ADMIN")
    public List<Account> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    /**
     * Get an account by accountId
     */
    @GetMapping(path = "/accounts/{accountId}")
    public Optional<Account> getAccountById(@PathVariable int accountId) {
        return accountService.getAccountById(accountId);
    }

    /**
     * Set account's active state into false
     */
    @GetMapping(path = "accounts/{accountId}/deactivate")
    @Secured("ROLE_ADMIN")
    public void deactivateAccount(@PathVariable int accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        account.ifPresent(value -> accountService.setAccountActiveState(value, false));
    }

    /**
     * Set account's active state into true
     */
    @GetMapping(path = "accounts/{accountId}/activate")
    @Secured("ROLE_ADMIN")
    public void activateAccount(@PathVariable int accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        account.ifPresent(value -> accountService.setAccountActiveState(value, true));
    }

    /**
     * Adding new account
     * @param account mapping from JSON format
     */
    @PostMapping(path = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public void addAccount(@Valid @RequestBody Account account) {
        account.setActive(true);
        accountService.addAccount(account);
    }

    /**
     * Update account's information
     */
    @PostMapping(path = "/accounts/updateInformation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateAccountInformation(@Valid @RequestBody Account updateAccount) {
        Optional<Account> dbAccount = accountService.getAccountByEmail(updateAccount.getEmail());
        dbAccount.ifPresent(account -> accountService.updateAccountInformation(account, updateAccount));
    }
    @GetMapping(path = "/accounts/search/{userName}")
    @Secured("ROLE_ADMIN")
    public List<Account> searchAccountByUserName(@PathVariable String userName) {
        return accountService.searchAccountByUserName(userName);
    }
}
