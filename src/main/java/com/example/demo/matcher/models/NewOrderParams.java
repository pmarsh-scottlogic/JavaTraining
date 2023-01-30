package com.example.demo.matcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@ToString @AllArgsConstructor @Getter
public class NewOrderParams {
    String username;
    @Min(value = OrderObj.minPrice) @Max(value = OrderObj.maxPrice)
    double price;
    @Min(value = OrderObj.minQuantity) @Max(value = OrderObj.maxQuantity)
    double quantity;
    @NotEmpty @Pattern(regexp = "^buy|sell$", message = "should be buy or sell")
    String action;
}