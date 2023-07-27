package com.hysteryale.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
    @Size(min = 2, message = "User name must be at least 2 characters")
    private String userName;
    @NotBlank(message = "Email must not be blank")
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
