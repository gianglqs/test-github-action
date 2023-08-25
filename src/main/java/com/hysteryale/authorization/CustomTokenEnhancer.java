package com.hysteryale.authorization;

import com.hysteryale.model.User;
import com.hysteryale.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    @Resource
    UserService userService;

    /**
     * Enhance what is included in the Token, adding "userId", "defaultLocale" and "role" into Token
     * @return access Token for authenticated user
     */
    @Override
    @Transactional
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) oAuth2Authentication.getPrincipal();

        User dbUser = userService.getUserByEmail(user.getUsername()).get();
        userService.setNewLastLogin(dbUser);

        final Map<String, Object> additionalInfo = new HashMap<>();

        additionalInfo.put("userId", dbUser.getId());
        additionalInfo.put("userName", dbUser.getUserName());
        additionalInfo.put("email", dbUser.getEmail());
        additionalInfo.put("defaultLocale", dbUser.getDefaultLocale());
        additionalInfo.put("role", dbUser.getRole());

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        return oAuth2AccessToken;
    }
}
