package com.example.demo.matcher.models;

import com.example.demo.validation.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@ToString @AllArgsConstructor @Getter
public class NewOrderParams {
    @UUID
    @NotEmpty
    String account;
    @NotEmpty @Range(min = 0, max = 1000000000)
    double price;
    @NotEmpty @Range(min = 0, max = 1000000000)
    double quantity;
    @NotEmpty @Pattern(regexp = "^buy|sell$")
    String action;
}
