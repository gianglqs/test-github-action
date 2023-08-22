package com.hysteryale.controller;

import com.hysteryale.model.Account;
import com.hysteryale.model.Role;
import com.hysteryale.repository.AccountRepository;
import com.hysteryale.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
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
        Role role = new Role(1, "admin", null);
        Account given1 = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm",role,"us", true);
        Account given2 = new Account(2, "given2", "given2@gmail.com", "given", role, "us", true);

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
    @Test
    void testAddAccount() {
        // GIVEN
        Role role = new Role(1, "admin", null);
        Account givenAccount = new Account(1, "given1", "given2@gmail.com", "user", role, "us", true);

        // WHEN
        accountController.addAccount(givenAccount);

        // THEN
        Mockito.verify(accountService).addAccount(givenAccount);
    }
    @Test
    void testDeactivateAccount() {
        // GIVEN
        Role role = new Role(1, "admin", null);
        Account givenAccount = new Account(1, "given1", "given2@gmail.com", "user", role, "us", true);

        // WHEN
        when(accountService.getAccountById(givenAccount.getId())).thenReturn(Optional.of(givenAccount));
        accountController.deactivateAccount(givenAccount.getId());

        // THEN
        verify(accountService).setAccountActiveState(givenAccount, false);
    }

    @Test
    void testActivateAccount() {
        // GIVEN
        Role role = new Role(1, "admin", null);
        Account givenAccount = new Account(1, "given1", "given2@gmail.com", "user", role, "us", true);

        // WHEN
        when(accountService.getAccountById(givenAccount.getId())).thenReturn(Optional.of(givenAccount));
        accountController.activateAccount(givenAccount.getId());

        // THEN
        verify(accountService).setAccountActiveState(givenAccount, true);
    }
    @Test
    void testGetAccountById() {
        // GIVEN
        Role role = new Role(1, "admin", null);
        Account givenAccount = new Account(1, "given1", "given2@gmail.com", "user", role, "us", true);

        // WHEN
        when(accountService.getAccountById(givenAccount.getId())).thenReturn(Optional.of(givenAccount));
        Optional<Account> result = accountController.getAccountById(givenAccount.getId());

        // THEN
        Mockito.verify(accountService).getAccountById(givenAccount.getId());
        Assertions.assertEquals(givenAccount, result.get());
    }

    @Test
    void testSearchAccountByUserName() {
        // GIVEN
        Role role = new Role(1, "admin", null);
        Account given1 = new Account(1, "given1", "given1@gmail.com", "given", role, "us", true);
        Account given2 = new Account(2, "given2", "given2@gmail.com", "given", role, "us", true);
        List<Account> accountList = new ArrayList<>();
        accountList.add(given1);
        accountList.add(given2);

        String userName = "given";

        // WHEN
        when(accountService.searchAccountByUserName(userName)).thenReturn(accountList);
        List<Account> result = accountController.searchAccountByUserName(userName);

        // THEN
        Mockito.verify(accountService).searchAccountByUserName(userName);
        Assertions.assertEquals(accountList.size(), result.size());
    }
    @Test
    void testUpdateAccountInformation() {
        // GIVEN
        Role role = new Role(1, "admin", null);
        Account given1 = new Account(1, "given1", "given1@gmail.com", "given", role, "us", true);

        // WHEN
        when(accountService.getAccountByEmail(given1.getEmail())).thenReturn(Optional.of(given1));
        accountController.updateAccountInformation(given1);

        // THEN
        Mockito.verify(accountService).updateAccountInformation(given1, given1);
    }
}