package com.hysteryale.controller;

import com.hysteryale.model.User;
import com.hysteryale.service.UserService;
import com.hysteryale.service.impl.EmailServiceImpl;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

@RestController
@EnableResourceServer
@CrossOrigin
@Slf4j
public class UserController {
    @Resource
    public UserService userService;
    @Resource
    EmailServiceImpl emailService;

    /**
     * Getting all users existed
     * @return Map contains list of users
     */
    @GetMapping(path = "/users")
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public Map<String, List<User>> getAllUsers(){
        Map<String, List<User>> userListMap = new HashMap<>();
        List<User> userList = userService.getAllUsers();
        userListMap.put("userList", userList);
        return userListMap;
    }

    /**
     * Get an user by userId
     */
    @GetMapping(path = "/users/{userId}")
    public Optional<User> getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    /**
     * Reverse user's active state into true or false
     */
    @GetMapping(path = "users/{userId}/activate")
    @Secured("ROLE_ADMIN")
    public void activateUser(@PathVariable int userId) {
        Optional<User> user = userService.getUserById(userId);
        user.ifPresent(value -> userService.setUserActiveState(value, !value.isActive()));
    }

    /**
     * Add new User and send informing email to registered email
     * @param user mapping from JSON format
     */
    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public void addUser(@Valid @RequestBody User user) {
        String password = user.getPassword();

        user.setActive(true);
        userService.addUser(user);

        try {
            emailService.sendRegistrationEmail(user.getUserName(), password, user.getEmail());
        } catch (MailjetSocketTimeoutException | MailjetException e){
            log.error(e.toString());
        }
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
    public Map<String, List<User>> searchUserByUserName(@PathVariable String userName) {
        Map<String, List<User>> userListMap = new HashMap<>();
        userListMap.put("searchedUserList", userService.searchUserByUserName(userName));

        return userListMap;
    }
}
