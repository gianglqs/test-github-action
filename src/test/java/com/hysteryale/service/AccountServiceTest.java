package com.hysteryale.service;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    private AccountService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
//        underTest = new AccountService(accountRepository);
    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
    void canGetAllAccounts() {
        // WHEN
        underTest.getAllAccounts();

        // THEN
        verify(accountRepository).findAll();
    }
    //TODO consider exception
    @Test
    void throwNotFoundIfIdIsNotExisted() {
        //GIVEN
        Integer accountId = 0;

        //THEN
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, ()->{
            underTest.getAccountById(accountId);
        });

        // expected
        String expectedMessage = "No account with id: " + accountId;
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        //return
        String returnMessage = responseStatusException.getMessage();
        HttpStatus returnStatus = responseStatusException.getStatus();

        Assertions.assertTrue(returnMessage.contains(expectedMessage));
        Assertions.assertEquals(expectedStatus, returnStatus);
    }

    @Test
    void canAddAccount() throws Exception {
        // GIVEN
        Account givenAccount = new Account("givenAccount", "test@gmail.com", "123456", "user");

        // WHEN
        underTest.addAccount(givenAccount);

        // THEN
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account capturedAccount = accountArgumentCaptor.getValue();

        assertEquals(capturedAccount, givenAccount);
    }
    @Test
    void throwExceptionIfEmailIsTaken() {
        //GIVEN
        String email = "admin@gmail.com";
        Account givenAccount = new Account("givenAccount", email, "123456", "admin");
        given(accountRepository.isEmailExisted(givenAccount.getEmail())).willReturn(true);

        //THEN
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> {
                    underTest.addAccount(givenAccount);
        });

        String expectedMessage = "Email has been already taken";
        String returnMessage = responseStatusException.getMessage();

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        HttpStatus returnStatus = responseStatusException.getStatus();

        Assertions.assertTrue(returnMessage.contains(expectedMessage));
        Assertions.assertEquals(returnStatus, expectedStatus);
    }

    @Test
    void itShouldCheckIfEmailIsExisted() throws Exception {
        // GIVEN
        String email = "admin@gmail.com";
        Account givenAccount = new Account("givenAccount", email, "123456", "admin");

        //WHEN
        underTest.addAccount(givenAccount);

        //THEN
        verify(accountRepository).isEmailExisted(email);
    }

    @Test
    void canGetAccountByEmail() {
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
}