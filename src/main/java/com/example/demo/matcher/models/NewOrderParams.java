package com.example.demo.matcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString @AllArgsConstructor @Getter
public class NewOrderParams {
    String account;
    double price;
    double quantity;
    String action;
}
