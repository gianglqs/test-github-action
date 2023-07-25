package com.hysteryale.controller;

import com.hysteryale.model.Account;
import com.hysteryale.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Id;
import java.util.List;

@RestController
@EnableResourceServer
public class AccountController {

    @Autowired
    public AccountService accountService;


    @GetMapping(path = "/accounts")
    public List<Account> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @PostMapping(path = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addAccount(@RequestBody Account account){
        accountService.addAccount(account);
    }

    @GetMapping(path = "/test")
    public String test(){
        return "test";
    }
}
