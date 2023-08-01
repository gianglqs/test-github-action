package com.hysteryale.controller;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import com.hysteryale.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class AccountControllerTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    AccountService accountService;
    @InjectMocks
    AccountController accountController;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        accountService = new AccountService(accountRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canGetAllAccounts() throws Exception {
        // GIVEN
        Account given1 = new Account(1, "given1", "given1@gmail.com", "given", "user", "us");
        Account given2 = new Account(2, "given2", "given2@gmail.com", "given", "user", "us");

        accountService.addAccount(given1);
        accountService.addAccount(given2);

        List<Account> accountList = new ArrayList<>();
        accountList.add(given1);
        accountList.add(given2);

        //WHEN
        when(accountController.getAllAccounts()).thenReturn(accountList);
        List<Account> result = accountController.getAllAccounts();

        //THEN
        Assertions.assertEquals(accountList.size(), result.size());
    }
}