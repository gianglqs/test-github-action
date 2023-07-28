package com.hysteryale.authorization;

import com.hysteryale.model.Account;
import com.hysteryale.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    AccountService accountService;

    /**
     * Enhance what is included in the Token, adding "accountId" and "defaultLocale" into Token
     * @param oAuth2AccessToken
     * @param oAuth2Authentication
     * @return access Token for authenticated account
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        User user = (User) oAuth2Authentication.getPrincipal();

        Account account = accountService.getAccountByEmail(user.getUsername()).get();

        final Map<String, Object> additionalInfo = new HashMap<>();

        additionalInfo.put("accountId", account.getId());
        additionalInfo.put("defaultLocale", account.getDefaultLocale());

        oAuth2AccessToken = (DefaultOAuth2AccessToken) oAuth2AccessToken;
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        return oAuth2AccessToken;
    }
}
