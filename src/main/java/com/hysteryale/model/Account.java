package com.hysteryale.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties("password")
public class Account {
    @Id
    @GeneratedValue
    private Integer id;
    private String userName;
    private String email;
    private String password;
    private String role;

    public Account() {
    }

    public Account(Integer id, String userName, String email, String password, String role) {
        this.id = id;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
