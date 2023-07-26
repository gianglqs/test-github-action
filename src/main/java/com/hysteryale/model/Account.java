package com.hysteryale.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter("PasswordFilter")
public class Account {
    @Id
    @GeneratedValue
    private Integer id;
    private String userName;
    private String email;
    private String password;
    private String role;
    private String defaultLocale;

    public Account(String userName, String email, String password, String role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Account(Account account) {
        this.id = account.id;
        this.userName = account.userName;
        this.email = account.email;
        this.password = account.password;
        this.role = account.role;
    }


}
