package com.example.demo.security.authInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthResponse { // POJO
    private String id;
    private String username;
    private String accessToken;

    //todo add accountId field so that only orderbook of authorized account can be retrieved
}
