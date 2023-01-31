package com.example.demo.security.repo;

import com.example.demo.security.userInfo.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> { // managing Objects of type AppUser, with primary key of type Long
    Role findByName(String name); // this will be a method to retrieve roles from the database by role name
}
