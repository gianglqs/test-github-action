package com.hysteryale.service;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServiceTest {
    @Autowired @Mock
    private AccountRepository accountRepository;
    @Autowired @InjectMocks
    private AccountService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
    void testGetAllAccounts() {
        // GIVEN
        Account given1 = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm","admin2","us", true);
        Account given2 = new Account(2, "given2", "given2@gmail.com", "given", "user", "us", true);
        List<Account> accountList = new ArrayList<>();
        accountList.add(given1);
        accountList.add(given2);

        underTest.addAccount(given1);
        underTest.addAccount(given2);


        // WHEN
        when(accountRepository.findAll()).thenReturn(accountList);
        List<Account> result = underTest.getAllAccounts();

        // THEN
        Assertions.assertEquals(accountList.size(), result.size());     // assert the result
        verify(accountRepository).findAll();                            // verify the flow of function
    }
    @Test
    void testThrowNotFoundIfIdIsNotExisted() {
        //GIVEN
        Integer accountId = 0;

        //WHEN
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, ()-> underTest.getAccountById(accountId));

            // expected
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
            //return
        HttpStatus returnStatus = responseStatusException.getStatus();

        // THEN
        Assertions.assertEquals(expectedStatus, returnStatus);
    }

    @Test
    void testAddAccount() {
        // GIVEN
        Account givenAccount = new Account("givenAccount", "test@gmail.com", "123456", "user");

        // WHEN
        underTest.addAccount(givenAccount);

        // THEN
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account capturedAccount = accountArgumentCaptor.getValue();

        Assertions.assertEquals(capturedAccount, givenAccount);
    }
    @Test
    void testThrowExceptionIfEmailIsTaken() {
        //GIVEN
        String email = "admin@gmail.com";
        Account givenAccount = new Account("givenAccount", email, "123456", "admin");
        given(accountRepository.isEmailExisted(givenAccount.getEmail())).willReturn(true);

        //THEN
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> underTest.addAccount(givenAccount));

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        HttpStatus returnStatus = responseStatusException.getStatus();

        Assertions.assertEquals(returnStatus, expectedStatus);
    }

    @Test
    void testCheckIfEmailIsExisted() {
        // GIVEN
        String email = "admin@gmail.com";
        Account givenAccount = new Account("givenAccount", email, "123456", "admin");

        //WHEN
        underTest.addAccount(givenAccount);

        //THEN
        verify(accountRepository).isEmailExisted(email);
    }

    @Test
    void testGetAccountByEmail() {
        //GIVEN
        String email = "admin@gmail.com";

        //WHEN
        underTest.getAccountByEmail(email);

        //THEN
        ArgumentCaptor<String> emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountRepository).getAccountByEmail(emailArgumentCaptor.capture());
        String capturedEmail = emailArgumentCaptor.getValue();

        Assertions.assertEquals(capturedEmail, email);
    }
    @Test
    void testGetActiveAccountByEmail() {
        // GIVEN
        String email = "user1@gmail.com";

        // WHEN
        underTest.getActiveAccountByEmail(email);

        // THEN
        Mockito.verify(accountRepository).getActiveAccountByEmail(email);
    }
    @Test
    void testSearchAccountByUserName() {
        // GIVEN
        Account given1 = new Account(1, "given1", "given1@gmail.com", "given", "user", "us", true);
        Account given2 = new Account(2, "given2", "given2@gmail.com", "given", "user", "us", true);
        List<Account> accountList = new ArrayList<>();
        accountList.add(given1);
        accountList.add(given2);

        String userName = "given";

        // WHEN
        when(accountRepository.searchAccountByUserName(userName)).thenReturn(accountList);
        List<Account> result = underTest.searchAccountByUserName(userName);

        // THEN
        Mockito.verify(accountRepository).searchAccountByUserName(userName);
        Assertions.assertEquals(accountList.size(), result.size());
    }
}