package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Size(min = 2, message = "User name must be at least 2 characters")
    private String userName;
    @NotBlank(message = "Email must not be blank")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
    private String defaultLocale;
    private boolean isActive;
    @Temporal(TemporalType.DATE)
    private Date lastLogin;


    public User(String userName, String email, String password, Role role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(Integer id, String userName, String email, String password, Role role, String defaultLocale, boolean isActive) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.defaultLocale = defaultLocale;
        this.isActive = isActive;
    }
    public User(Integer id, String password) {
        this.id = id;
        this.password = password;
    }
}
