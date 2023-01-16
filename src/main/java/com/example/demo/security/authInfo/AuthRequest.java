package com.example.demo.security.authInfo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class AuthRequest {
    @NotNull @Length(min = 1, max = 50)
    private String username;

    @NotNull @Length(min = 1, max = 10)
    private String password;

}