package com.hysteryale.service;

import com.hysteryale.model.Account;
import com.hysteryale.repository.AccountRepository;
import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    private AccountService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new AccountService(accountRepository);
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

    @Test
    @Disabled
    void canGetAccountById() throws NotFoundException {
        //GIVEN
        Integer accountId = 14;

        //WHEN
        given(underTest.getAccountById(accountId)).willReturn(any());

        //THEN
        verify(accountRepository).findById(accountId);
    }
    @Test
    void throwNotFoundIfIdIsNotExisted() {
        //GIVEN
        Integer accountId = 0;

        //THEN
        assertThatThrownBy(()-> underTest.getAccountById(accountId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No account with id: " + accountId);
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

        assertThat(capturedAccount).isEqualTo(givenAccount);
    }
    @Test
    void throwExceptionIfEmailIsTaken() {
        //GIVEN
        String email = "admin@gmail.com";
        Account givenAccount = new Account("givenAccount", email, "123456", "admin");
        given(accountRepository.isEmailExisted(givenAccount.getEmail())).willReturn(true);

        //THEN
        assertThatThrownBy(() -> underTest.addAccount(givenAccount))
                .isInstanceOf(Exception.class)
                .hasMessage("Email has been already taken");
        verify(accountRepository, never()).save(any());

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

        assertThat(capturedEmail).isEqualTo(email);
    }
}