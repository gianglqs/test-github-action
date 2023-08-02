package com.hysteryale.controller;

import com.hysteryale.model.Account;
import com.hysteryale.model.Price;
import com.hysteryale.repository.AccountRepository;
import com.hysteryale.repository.PriceRepository;
import com.hysteryale.service.PriceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.*;

import static org.mockito.Mockito.when;

class PriceBookControllerTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    PriceRepository priceRepository;
    @Autowired
    PriceService priceService;
    AutoCloseable autoCloseable;
    @InjectMocks
    PriceBookController priceBookController;
    @Autowired
    RestTemplate testRestTemplate;
    private static String ACCESS_TOKEN;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
//        priceService = new PriceService(priceRepository);
        priceBookController = new PriceBookController(priceService);
        testRestTemplate = new RestTemplate();
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    void getAccessToken(){
        // GIVEN
        Account givenAccount = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm","admin2","us");
        accountRepository.save(givenAccount);

        // GET ACCESS TOKEN
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", "admin2@gmail.com");
        map.add("password", "123456");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.setBasicAuth("client", "password");

        HttpEntity<MultiValueMap<String, String>> oauthEntity = new HttpEntity<>(map, headers);
        ResponseEntity<Object> responseEntity = testRestTemplate.postForEntity("http://localhost:8080/oauth/token", oauthEntity, Object.class);

        LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) responseEntity.getBody();
        ACCESS_TOKEN = "Bearer" + linkedHashMap.get("access_token");
    }
    List<HttpMessageConverter<?>> getConverter(){
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }
    //TODO test through functions
    @Test
    void canGetAllPrices() {
        // GIVEN
        Price given1 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        Price given2 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        List<Price> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);

        // Create account instance for authorization
        Account givenAccount = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm","admin2","us");
        accountRepository.save(givenAccount);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        // GET ACCESS TOKEN
        getAccessToken();

        // setting Header and MessageConverter
        HttpHeaders accountHeader = new HttpHeaders();
        accountHeader.setContentType(MediaType.APPLICATION_JSON);
        accountHeader.add(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);

        List<HttpMessageConverter<?>> messageConverters = getConverter();

        testRestTemplate.setMessageConverters(messageConverters);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map ,accountHeader);

        // WHEN
        ResponseEntity<Object> priceResponse = testRestTemplate.exchange("http://localhost:8080/price", HttpMethod.GET,request, Object.class);
        ArrayList<String> priceList = (ArrayList<String>) priceResponse.getBody();

        // THEN
        Assertions.assertNotNull(priceList);
        System.out.println(priceList);
    }

    @Test
    void throwNotFoundIfNoPriceBySeries() {
        // GIVEN
        String series = "not found";

        // Create account instance for authorization
        Account givenAccount = new Account(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm","admin2","us");
        accountRepository.save(givenAccount);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        // GET ACCESS TOKEN
        getAccessToken();

        // WHEN
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, ()->{
            priceBookController.getPricesBySeries(series);
        });

        // THEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("No Price found with series: " + series));
    }

    @Test
    void canGetPricesBySeries() {
        // GIVEN
        Price given1 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");
        Price given2 = new Price("price1", "price1", "price1", "price1", "price1", "price1", "price1", 1.0, 1.0, new Date(2023, 8, 1), new Date(2023, 8, 1), "price1");

        List<Price> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);

        priceRepository.save(given1);
        priceRepository.save(given2);

        Optional<Price> test = priceRepository.findById(1);

        priceService.addListOfPrices(givenList);

        String series = "price1";

        // WHEN
        when(priceService.getPricesBySeries(series)).thenReturn(givenList);
        List<Price> priceList = priceBookController.getPricesBySeries(series);

        // THEN
        Assertions.assertEquals(givenList.size(), priceList.size());
    }
}