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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.mockito.Mockito.when;

public class AccountControllerTest {
    @Autowired
    private RestTemplate testRestTemplate;

    @Mock
    private AccountRepository accountRepository;
    private AutoCloseable autoCloseable;

    @Autowired @Mock
    AccountService accountService;

    private static String ACCESS_TOKEN = "Bearer";

    @BeforeEach
    void setUp(){
        testRestTemplate = new RestTemplate();
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    void testGetAccessToken()
    {
        Account given1 = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm","admin2","us");
        accountRepository.save(given1);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", "admin2@gmail.com");
        map.add("password", "123456");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.setBasicAuth("client", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Object> responseEntity = testRestTemplate.postForEntity("http://localhost:8080/oauth/token", request, Object.class);

        LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) responseEntity.getBody();

        ACCESS_TOKEN = "Bearer" + linkedHashMap.get("access_token");

        Assertions.assertNotEquals("Bearer", ACCESS_TOKEN);
    }

    List<HttpMessageConverter<?>> getConverter(){
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }

    @Test
    void itShouldGetAllAccounts() throws Exception {
        // GIVEN
        Account given1 = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm","admin2","us");
        Account given2 = new Account(2, "given2", "given2@gmail.com", "given", "user", "us");

        List<Account> accounts = new ArrayList<>();
        accounts.add(given1);
        accounts.add(given2);
        accountRepository.saveAll(accounts);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        // GET ACCESS TOKEN
//        canGetAccessToken();

        // setting Header and MessageConverter
        List<HttpMessageConverter<?>> messageConverters = getConverter();
        testRestTemplate.setMessageConverters(messageConverters);

        HttpHeaders accountHeader = new HttpHeaders();
        accountHeader.setContentType(MediaType.APPLICATION_JSON);
        accountHeader.add(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map ,accountHeader);

        // WHEN
        ResponseEntity<Object> accountsResponse = testRestTemplate.exchange("http://localhost:8080/accounts", HttpMethod.GET,request, Object.class);
        ArrayList<String> accountList = (ArrayList<String>) accountsResponse.getBody();

        // THEN
        Assertions.assertNotNull(accountList);
    }
}