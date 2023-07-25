package com.hysteryale.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class AuthWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/test").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder().username("user").password(passwordEncoder.encode("password")).
                roles("USER").build();
        UserDetails userAdmin = User.builder().username("admin").password(passwordEncoder.encode("password")).
                roles("ADMIN").build();
        return new InMemoryUserDetailsManager(user, userAdmin);
    }
}

