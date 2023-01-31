package com.example.demo.security.authInfo.passwordValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidation {
    String message() default "Password should be 8-20 characters and contain at least 1 each of: digit, lowercase letter uppercase letter, special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
