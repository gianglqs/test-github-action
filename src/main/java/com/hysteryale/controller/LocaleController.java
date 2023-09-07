package com.hysteryale.controller;

import com.hysteryale.model.User;
import com.hysteryale.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Map;
@RestController
public class LocaleController {
    @Resource
    MessageSource messageSource;
    @Resource
    UserService userService;
    @Resource
    AuthorizationServerTokenServices tokenServices;

    @GetMapping(path = "/welcome")
    public String welcome(OAuth2Authentication authentication){
        Map<String, Object> additionalInfo = tokenServices.getAccessToken(authentication).getAdditionalInformation();
        String defaultLocale = additionalInfo.get("defaultLocale").toString();

        return messageSource.getMessage("welcome", null, Locale.forLanguageTag(defaultLocale));
    }

    @GetMapping(path = "/locale/change/{localeTag}")
    public ResponseEntity changeDefaultLocale(OAuth2Authentication authentication, @PathVariable String localeTag) throws NotFoundException {
        // Get additional information from access token
        Map<String, Object> additionalInfo = tokenServices.getAccessToken(authentication).getAdditionalInformation();
        String accountId = additionalInfo.get("accountId").toString();

        User user = userService.getUserById(Integer.valueOf(accountId));
        userService.changeDefaultLocale(user, Locale.forLanguageTag(localeTag).toString());

        return ResponseEntity.ok("Default locale changed into: " + Locale.forLanguageTag(localeTag).toString());
    }
    @GetMapping(path = "/locale/getDefaultLocale")
    @Transactional
    public ResponseEntity getDefaultLocale(OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = tokenServices.getAccessToken(authentication).getAdditionalInformation();
        String defaultLocale = additionalInfo.get("defaultLocale").toString();

        return ResponseEntity.ok(defaultLocale);
    }
}
