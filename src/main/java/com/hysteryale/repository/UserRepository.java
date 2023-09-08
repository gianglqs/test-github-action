package com.hysteryale.repository;

import com.hysteryale.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT a FROM User a WHERE a.email = ?1")
    public Optional<User> getUserByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM User a WHERE a.email = ?1")
    public boolean isEmailExisted(String email);
    @Query("SELECT a FROM User a WHERE a.email = ?1 AND a.isActive = true")
    public Optional<User> getActiveUserByEmail(String email);
    @Query("SELECT a FROM User a WHERE CONCAT(a.userName, a.email) LIKE %?1%")
    public List<User> searchUser(String searchString);
}
