package com.hysteryale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RestController
public class WelcomeController {

    @Autowired
    MessageSource messageSource;

    @GetMapping(path = "/welcome")
    public String welcome(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String lang){
        return messageSource.getMessage("welcome", null, Locale.forLanguageTag(lang));
    }
}
