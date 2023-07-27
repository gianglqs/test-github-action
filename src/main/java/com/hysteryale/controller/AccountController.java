package com.hysteryale.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.hysteryale.model.Account;
import com.hysteryale.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@EnableResourceServer
public class AccountController {
    @Autowired
    public AccountService accountService;

    /**
     * Getting all accounts existed and filter to hide password
     * @return list of accounts
     */
    @GetMapping(path = "/accounts")
    public MappingJacksonValue getAllAccounts(){

        // Filter to hide the password before return to JSON format
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("userName", "email", "role");
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("PasswordFilter", filter);

        //Mapping the Object through the Filters
        MappingJacksonValue mapping = new MappingJacksonValue(accountService.getAllAccounts());
        mapping.setFilters(filterProvider);

        return mapping;
    }

    /**
     * Adding new account
     * @param account mapping from JSON format
     * @throws Exception if Account is not in valid format / constraints
     */
    @PostMapping(path = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addAccount(@Valid @RequestBody Account account) throws Exception{
        accountService.addAccount(account);
    }
}