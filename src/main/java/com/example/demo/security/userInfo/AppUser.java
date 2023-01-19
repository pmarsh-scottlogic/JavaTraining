package com.example.demo.security.userInfo;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity // in JPA, an entity is any POJO representing data that can be persisted to a database
// An entity class represents a table, and each instance of the class is a new row in the table
@Data // lombok annotation that shortcuts @ToString @EqualsAndHashCode @Getter @Setter @RequiredArgsConstructor
@NoArgsConstructor @AllArgsConstructor // lombok stuff for boilerplate code
public class AppUser implements UserDetails { // todo: why do we implement UserDetails?
    @Id // defines the primary key for the table
    private String id;
    private String name;
    private String username;
    private String password;

    // JPA (Java Persistence API) setup relationship between user and role
    // when we fetch a user, we will always load all of their roles (hence EAGER)
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();


    // todo: what's the point in the below? Should we  return meaningful stuff? Should this information be included in the database?
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}