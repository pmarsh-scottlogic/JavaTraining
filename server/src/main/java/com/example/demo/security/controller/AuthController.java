package com.example.demo.security.controller;

import com.example.demo.security.authInfo.AuthRequest;
import com.example.demo.security.authInfo.AuthResponse;
import com.example.demo.security.token.JwtTokenUtil;
import com.example.demo.security.userInfo.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        // If we get to this endpoint, it means the client doesn't have a jwt yet and wants one.
        // The client provides a username and password via the AuthRequest object. These credentials are checked as follows:
        try {
            // We set up this authenticationManager object in ApplicationSecurity, and we told it how to get users from the database.
            // the Authenticate method will return a fully populated Authentication object if successful, and if not throw one of these exceptions:
            // DisabledException, LockedException, BadCredentialsException
            // todo: But how does it know how to access the users password? Does it know it needs to encode the request password? Does it check the user's Roles?
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            // respond with username and JWT
            AppUser user = (AppUser) authentication.getPrincipal();
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);
            return ResponseEntity.ok().body(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
