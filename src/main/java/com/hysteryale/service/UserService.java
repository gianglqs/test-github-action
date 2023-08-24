package com.hysteryale.service;

import com.hysteryale.model.User;
import com.hysteryale.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Retrieving all the users
     * @return List of existed user
     */
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * Getting an user by the given Id
     * @param userId: given Id
     * @return an User
     */
    public Optional<User> getUserById(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with id: " + userId);
        return user;
    }

    /**
     * Adding new user with the encrypted password if the registered email is not existed
     * @param user : new registered User
     */
    public void addUser(User user){

        if(!userRepository.isEmailExisted(user.getEmail()))
        {
            // encrypt password
            user.setPassword(passwordEncoder().encode(user.getPassword()));
            userRepository.save(user);
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email has been already taken");
    }

    /**
     * Getting an user by the email
     * @param email: given email
     * @return an User
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    /**
     * Getting an user which is still active by email
     */
    public Optional<User> getActiveUserByEmail(String email) {return userRepository.getActiveUserByEmail(email); }

    @Transactional
    public void changeDefaultLocale(User user, String locale) {
        user.setDefaultLocale(locale);
    }

    /**
     * Set user's isActive state (isActive: true or false)
     */
    @Transactional
    public void setUserActiveState(User user, boolean isActive) {
        user.setActive(isActive);
    }

    /**
     * Update user's information: userName ,role, defaultLocale
     * @param dbUser user get from Database
     * @param updateUser user contained changed information
     */
    @Transactional
    public void updateUserInformation(User dbUser, User updateUser) {
        dbUser.setUserName(updateUser.getUserName());
        dbUser.setRole(updateUser.getRole());
        dbUser.setDefaultLocale(updateUser.getDefaultLocale());
    }
    @Transactional
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder().encode(password));
    }
    @Transactional
    public void setNewLastLogin(User user) {
        user.setLastLogin(new Date());
    }
    public List<User> searchUserByUserName(String userName) {
        return userRepository.searchUserByUserName(userName);
    }
}
