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
    @UUID(message = "Bad UUID format")
    String account;
    @Range(min = Order.minPrice, max = Order.maxPrice)
    double price;
    @Range(min = Order.minQuantity, max = Order.maxQuantity)
    double quantity;
    @NotEmpty @Pattern(regexp = "^buy|sell$", message = "should be buy or sell")
    String action;
}
