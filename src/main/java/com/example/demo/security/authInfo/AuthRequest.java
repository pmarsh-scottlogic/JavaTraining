package com.example.demo.security.authInfo;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter @AllArgsConstructor
public class AuthRequest { // POJO
    @NotNull @Length(min = 1, max = 50)
    private String username;

    @NotNull @Length(min = 1, max = 50)
    private String password;

}