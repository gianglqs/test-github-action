package com.hysteryale.authorization;

import com.hysteryale.model.Account;
import com.hysteryale.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Collections;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class AuthWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    AccountService accountService;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated();
    }

    /**
     * Override the UserDetailsService: loadUserByUsername() to get Account's information from DB
     * @return an Account under UserDetails
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
                Optional<Account> optionalAccount = accountService.getAccountByEmail(userEmail);

                if(optionalAccount.isEmpty())
                    throw new UsernameNotFoundException("No user founded with: " + userEmail);

                Account account = optionalAccount.get();
                UserDetails userDetails;
                if (account.getRole().equals("admin"))
                    userDetails = User
                            .builder()
                            .username(account.getEmail())
                            .password(account.getPassword())
                            .roles("ADMIN")
                            .build();
                else
                    userDetails = User
                            .builder()
                            .username(account.getEmail())
                            .password(account.getPassword())
                            .roles("USER")
                            .build();
                return userDetails;
            }
        };


    }
}

