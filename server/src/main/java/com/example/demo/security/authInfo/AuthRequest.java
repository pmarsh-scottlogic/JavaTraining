package com.example.demo.security.authInfo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.example.demo.security.authInfo.passwordValidation.PasswordValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter @AllArgsConstructor
public class AuthRequest { // POJO
    @NotNull @Length(min = 1, max = 20)
    private String username;

    @PasswordValidation
    private String password;
}
