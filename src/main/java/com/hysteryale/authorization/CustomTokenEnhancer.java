package com.hysteryale.authorization;

import com.hysteryale.model.User;
import com.hysteryale.service.UserService;
import lombok.extern.slf4j.Slf4j;
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

        User dbUser = userService.getUserByEmail(user.getUsername());
        userService.setNewLastLogin(dbUser);

        final Map<String, Object> additionalInfo = assignAdditionalInformation(dbUser);

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        return oAuth2AccessToken;
    }

    private static Map<String, Object> assignAdditionalInformation(User dbUser) {
        final Map<String, Object> additionalInfo = new HashMap<>();

        Map<String, Object> userInformation = new HashMap<>();

        userInformation.put("userId", dbUser.getId());
        userInformation.put("userName", dbUser.getUserName());
        userInformation.put("email", dbUser.getEmail());
        userInformation.put("defaultLocale", dbUser.getDefaultLocale());
        userInformation.put("role", dbUser.getRole().getRoleName());

        additionalInfo.put("user", userInformation);

        //define redirect URL after Log in
        if(dbUser.getRole().getRoleName().equals("ADMIN"))
            additionalInfo.put("redirect_to", "/dashboard");
        else
            additionalInfo.put("redirect_to", "/bookingOrder");
        return additionalInfo;
    }
}
