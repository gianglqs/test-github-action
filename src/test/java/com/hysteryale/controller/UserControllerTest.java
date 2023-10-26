package com.hysteryale.controller;

import com.hysteryale.model.Role;
import com.hysteryale.model.User;
import com.hysteryale.repository.UserRepository;
import com.hysteryale.service.UserService;
import com.hysteryale.service.impl.EmailServiceImpl;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
public class UserControllerTest{// extends BasedControllerTest{
    @InjectMocks
    UserController userController;

    @Resource
    @Mock
    private UserRepository userRepository;
    @Resource
    @Mock
    private EmailServiceImpl emailService;

    int pageNo = 0;
    int perPage = 100;
    String sortType = "ascending";


    @Test
    void testGetAllUsers() {
        /*
        // GIVEN
        Role role = new Role(1, "admin", null);
        User given1 = new User(1,"user","admin2@gmail.com","$2a$10$oTxck2rZyU6y6LbUrUM3Zey/CBjNRonGAQ3cM5.QjzkRVIw5.hOhm",role,"us", true);
        User given2 = new User(2, "given2", "given2@gmail.com", "given", role, "us", true);

        List<User> givenList = new ArrayList<>();
        givenList.add(given1);
        givenList.add(given2);
        userRepository.saveAll(givenList);

        // WHEN
        when(userService.searchUser("", pageNo, perPage, sortType)).thenReturn(new PageImpl<>(givenList, PageRequest.of(pageNo, perPage), 2));
        Map<String, Object> result = userController.searchUser("", pageNo, perPage, sortType);

        // THEN
        Mockito.verify(userService).searchUser("", pageNo, perPage, sortType);
        Assertions.assertEquals(givenList.size(), ((List<User>) result.get("userList")).size());
         */
    }
    @Test
    void testAddUser() throws MailjetSocketTimeoutException, MailjetException {
        /*
        // GIVEN
        Role role = new Role(1, "admin", null);
        User givenUser = new User(1, "given1", "given2@gmail.com", "user", role, "us", true);

        // WHEN
        userController.addUser(givenUser);

        // THEN
        Mockito.verify(userService).addUser(givenUser);
        Mockito.verify(emailService).sendRegistrationEmail(givenUser.getUserName(), givenUser.getPassword(), givenUser.getEmail());
        */

    }
    @Test
    void testDeactivateUser() {
        /*
        // GIVEN
        Role role = new Role(1, "admin", null);
        User givenUser = new User(1, "given1", "given2@gmail.com", "user", role, "us", true);

        // WHEN
        when(userService.getUserById(givenUser.getId())).thenReturn(givenUser);
        userController.activateUser(givenUser.getId());

        // THEN
        verify(userService).setUserActiveState(givenUser, false);
        */

    }

    @Test
    void testActivateUser() {
        /*
        // GIVEN
        Role role = new Role(1, "admin", null);
        User givenUser = new User(1, "given1", "given2@gmail.com", "user", role, "us", false);

        // WHEN
        when(userService.getUserById(givenUser.getId())).thenReturn(givenUser);
        userController.activateUser(givenUser.getId());

        // THEN
        verify(userService).setUserActiveState(givenUser, true);

         */
    }

    @Test
    void testSearchUser() {
        /*
        // GIVEN
        Role role = new Role(1, "admin", null);
        User given1 = new User(1, "given1", "given1@gmail.com", "given", role, "us", true);
        User given2 = new User(2, "given2", "given2@gmail.com", "given", role, "us", true);
        List<User> userList = new ArrayList<>();
        userList.add(given1);
        userList.add(given2);

        String searchString = "given";

        // WHEN
        when(userService.searchUser(searchString, pageNo, perPage, sortType)).thenReturn(new PageImpl<>(userList));
        Map<String, Object> result = userController.searchUser(searchString, pageNo, perPage, sortType);

        // THEN
        Mockito.verify(userService).searchUser(searchString, pageNo, perPage, sortType);
        Assertions.assertEquals(userList.size(), ((List<User>) result.get("userList")).size());
        */
    }
    @Test
    void testUpdateUserInformation() {
        /*
        // GIVEN
        Role role = new Role(1, "admin", null);
        User given1 = new User(1, "given1", "given1@gmail.com", "given", role, "us", true);

        // WHEN
        when(userService.getUserById(given1.getId())).thenReturn(given1);
        userController.updateUserInformation(given1, given1.getId());

        // THEN
        Mockito.verify(userService).updateUserInformation(given1, given1);
         */
    }
}