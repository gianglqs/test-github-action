package com.hysteryale.controller;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import com.hysteryale.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
public class AccountControllerTest {
    @Autowired @Mock
    private AccountRepository accountRepository;
    private AutoCloseable autoCloseable;
    @Autowired @Mock
    AccountService accountService;
    @Autowired @InjectMocks
    AccountController accountController;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    void testGetAllAccounts() {
        // GIVEN
        Account given1 = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm","admin2","us");
        Account given2 = new Account(2, "given2", "given2@gmail.com", "given", "user", "us");

        List<Account> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);
        accountRepository.saveAll(givenList);
        // WHEN
        when(accountService.getAllAccounts()).thenReturn(givenList);
        List<Account> result = accountController.getAllAccounts();

        // THEN
        Assertions.assertEquals(givenList.size(), result.size());
    }
}