package com.example.demo.security.authInfo;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponse {
    private String username;
    private String accessToken;
}
