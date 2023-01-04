package com.example.demo.matcher.models;

import java.time.LocalDateTime;

public class Order {
    String id;
    String account;
    float price;
    float quantity;
    OrderAction action;
    LocalDateTime datetime;

    public Order(String account, float price, float quantity, OrderAction action) {
        this.id = "RANDOMID"; // use some sort of UUId generating library later for this
        this.account = account;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = LocalDateTime.now();
    }
}
