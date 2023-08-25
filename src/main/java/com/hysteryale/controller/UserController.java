package com.hysteryale.controller;

import com.hysteryale.model.User;
import com.hysteryale.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@EnableResourceServer
@CrossOrigin
@Slf4j
public class UserController {
    @Resource
    public UserService userService;

    /**
     * Getting all users existed and filter to hide password
     * @return list of users
     */
    @GetMapping(path = "/users")
    @Secured("ROLE_ADMIN")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    /**
     * Get an user by userId
     */
    @GetMapping(path = "/users/{userId}")
    public Optional<User> getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    /**
     * Set user's active state into false
     */
    @GetMapping(path = "users/{userId}/deactivate")
    @Secured("ROLE_ADMIN")
    public void deactivateUser(@PathVariable int userId) {
        Optional<User> user = userService.getUserById(userId);
        user.ifPresent(value -> userService.setUserActiveState(value, false));
    }

    /**
     * Set user's active state into true
     */
    @GetMapping(path = "users/{userId}/activate")
    @Secured("ROLE_ADMIN")
    public void activateUser(@PathVariable int userId) {
        Optional<User> user = userService.getUserById(userId);
        user.ifPresent(value -> userService.setUserActiveState(value, true));
    }

    /**
     * Adding new user
     * @param user mapping from JSON format
     */
    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public void addUser(@Valid @RequestBody User user) {
        user.setActive(true);
        userService.addUser(user);
    }

    /**
     * Update user's information
     */
    @PostMapping(path = "/users/updateInformation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUserInformation(@Valid @RequestBody User updateUser) {
        Optional<User> dbUser = userService.getUserByEmail(updateUser.getEmail());
        dbUser.ifPresent(user -> userService.updateUserInformation(user, updateUser));
    }
    @GetMapping(path = "/users/search/{userName}")
    @Secured("ROLE_ADMIN")
    public List<User> searchUserByUserName(@PathVariable String userName) {
        return userService.searchUserByUserName(userName);
    }
}
