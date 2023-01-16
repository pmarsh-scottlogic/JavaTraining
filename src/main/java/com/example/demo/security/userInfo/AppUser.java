package com.example.demo.security.userInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.GenerationType.AUTO;

@Entity // in JPA, an entity is any POJO representing data that can be persisted to a database
// An entity class represents a table, and each instance of the class is a new row in the table
@Data @NoArgsConstructor @AllArgsConstructor // lombok stuff for boilerplate code
public class AppUser {
    @Id // defines the primary key for the table
    @GeneratedValue(strategy = AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;

    // JPA (Java Persistence API) setup relationship between user and role
    // when we fetch a user, we will always load all of their roles (hence EAGER)
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();
}