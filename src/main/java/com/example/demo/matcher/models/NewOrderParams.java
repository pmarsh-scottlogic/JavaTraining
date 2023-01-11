package com.example.demo.matcher.models;

import com.example.demo.validation.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@ToString @AllArgsConstructor @Getter
public class NewOrderParams {
    @UUID(message = "Bad UUID format")
    String account;
    @Min(value = Order.minPrice) @Max(value = Order.maxPrice)
    double price;
    @Min(value = Order.minQuantity) @Max(value = Order.maxQuantity)
    double quantity;
    @NotEmpty @Pattern(regexp = "^buy|sell$", message = "should be buy or sell")
    String action;
}