package com.example.demo.security.repo;

import com.example.demo.security.userInfo.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser, Long> { // managing Objects of type AppUser, with primary key of type Long
    Optional<AppUser> findByUsername(String username); // this will be a method to retrieve users from the database
}
