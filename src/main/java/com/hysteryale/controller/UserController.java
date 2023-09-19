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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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
     * Reverse user's active state into true or false based on whether user is active or not
     */
    @PutMapping(path = "users/activate/{userId}")
    @Secured("ROLE_ADMIN")
    public void activateUser(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        userService.setUserActiveState(user, !user.isActive());
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
    @PutMapping(path = "/users/updateUser/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserInformation(@RequestBody User updateUser, @PathVariable int userId) {
        User dbUser = userService.getUserById(userId);
        userService.updateUserInformation(dbUser, updateUser);
        return ResponseEntity.ok("Update user's information successfully");
    }

    /**
     * Get list of users based on searchString (if searchString is null then get all users)
     */
    @GetMapping(path = "/users")
    @Secured("ROLE_ADMIN")
    public Map<String, Object> searchUser(@RequestParam(required = false) String search,
                                          @RequestParam(defaultValue = "100") int perPage,
                                          @RequestParam(defaultValue = "1") int pageNo) {

        Page<User> userPage = userService.searchUser(search, pageNo, perPage);

        Map<String, Object> userPageMap = new HashMap<>();

        userPageMap.put("perPage", perPage);
        userPageMap.put("userList", userPage.getContent());
        userPageMap.put("page", userPage.getNumber());
        userPageMap.put("totalPages", userPage.getTotalPages());
        userPageMap.put("totalItems", userPage.getTotalElements());

        return userPageMap;
    }

    /**
     * Change user's password, {userId, password} passed from JSON format
     * @param changedPasswordUser contains {userId, password}
     */
    @PostMapping(path = "/users/changePassword/{userId}")
    public ResponseEntity<?> changePassword(@RequestBody User changedPasswordUser, @PathVariable int userId) {
        User dbUser = userService.getUserById(userId);

        if(changedPasswordUser.getPassword().length() < 6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        else
        {
            userService.changeUserPassword(dbUser, changedPasswordUser.getPassword());
            return ResponseEntity.ok("Password has been changed successfully");
        }
    }

    /**
     * Reset user's password specified by email (if email is existed), then send informing email for user.
     * @param email get from front-end
     */
    @PostMapping(path = "/users/resetPassword")
    public void resetPassword(@RequestBody String email){
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(email);
            try {
                userService.resetUserPassword((String) jsonObject.get("email"));
            } catch (MailjetSocketTimeoutException | MailjetException e) {
                throw new RuntimeException(e);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
