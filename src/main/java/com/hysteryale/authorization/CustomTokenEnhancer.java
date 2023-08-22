package com.hysteryale.authorization;

import com.hysteryale.model.Account;
import com.hysteryale.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    AccountService accountService;

    /**
     * Enhance what is included in the Token, adding "accountId", "defaultLocale" and "role" into Token
     * @return access Token for authenticated account
     */
    @Override
    @Transactional
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        User user = (User) oAuth2Authentication.getPrincipal();

        Account account = accountService.getAccountByEmail(user.getUsername()).get();
        accountService.setNewLastLogin(account);

        final Map<String, Object> additionalInfo = new HashMap<>();

        additionalInfo.put("accountId", account.getId());
        additionalInfo.put("userName", account.getUserName());
        additionalInfo.put("email", account.getEmail());
        additionalInfo.put("defaultLocale", account.getDefaultLocale());
        additionalInfo.put("role", account.getRole());

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        return oAuth2AccessToken;
    }
}
