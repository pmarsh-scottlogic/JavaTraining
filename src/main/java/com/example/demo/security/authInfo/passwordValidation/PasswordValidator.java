package com.example.demo.security.authInfo.passwordValidation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return false;
    }
}

