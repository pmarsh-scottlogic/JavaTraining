package com.example.demo.matcher.models;

import java.time.LocalDateTime;

public class Order {
    final String id;
    final String accountId;
    final float price;
    final float quantity;
    final OrderAction action;
    final LocalDateTime datetime;

    public Order(String accountId, float price, float quantity, OrderAction action) {
        this.id = "RANDOMID"; // use some sort of UUId generating library later for this
        this.accountId = accountId;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
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

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", account='" + accountId + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", action=" + action +
                ", datetime=" + datetime +
                '}';
    }
}
