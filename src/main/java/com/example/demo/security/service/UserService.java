package com.example.demo.security.service;

import com.example.demo.security.userInfo.AppUser;
import com.example.demo.security.userInfo.Role;

import java.util.List;

public interface UserService {
    AppUser saveUser(AppUser user); // this method will save a given user to the database
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    AppUser getUser(String username);
    List<AppUser> getUsers();
}

