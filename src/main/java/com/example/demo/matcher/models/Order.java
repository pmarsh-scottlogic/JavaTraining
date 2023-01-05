package com.example.demo.matcher.models;

import java.time.LocalDateTime;

public class Order {
    final String id;
    final String account;
    final float price;
    final float quantity;
    final OrderAction action;
    final LocalDateTime datetime;

    public Order(String account, float price, float quantity, OrderAction action) {
        this.id = "RANDOMID"; // use some sort of UUId generating library later for this
        this.account = account;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    public OrderAction getAction() {
        return action;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }
}
