package com.example.demo.security.service;

import com.example.demo.security.repo.RoleRepo;
import com.example.demo.security.repo.UserRepo;
import com.example.demo.security.userInfo.AppUser;
import com.example.demo.security.userInfo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service // Tells Spring that this is a service - it should be injected into a controller later
@RequiredArgsConstructor // Lombok
@Transactional // This marks all class methods as transactional.
// A transaction is a series of actions that must all complete successfully.
// Hence if at least one action fails, everything should roll back to leave the application state unchanged.
// Apparently it also means that when we've edited an entity that we took from a database, it will automatically save those changes to the database
@Slf4j // Lombok annotation that enables logging
public class MyUserService implements UserService, UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This method is from UserDetailsService, which is part of Spring Security. For a particular username, it returns
        // a UserDetails object, which includes the username, password and a collection of SimpleGrantedAuthority.
        //

        AppUser user = userRepo.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User {} not found in the database")
        );
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(
                role -> authorities.add(new SimpleGrantedAuthority(role.getName()))
        );
        return new User(user.getUsername(), user.getPassword(), authorities);

    }

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) throws UsernameNotFoundException {
        log.info("Adding role {} to user {}", roleName, username);
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found."));
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public AppUser getUser(String username) throws UsernameNotFoundException {
        log.info("Fetching user {}", username);
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found."));
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
    }


}
