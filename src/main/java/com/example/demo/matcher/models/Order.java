package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @ToString
public class Order {
    final UUID orderId;
    final String accountId;
    final float price;
    final float quantity;
    final OrderAction action;
    final LocalDateTime datetime;

    public Order(String accountId, float price, float quantity, OrderAction action) {
        this.orderId = UUID.randomUUID();; // use some sort of UUId generating library later for this
        this.accountId = accountId;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = LocalDateTime.now();
    }

    public Order(String accountId, float price, float quantity, OrderAction action, LocalDateTime datetime) {
        this.orderId = UUID.randomUUID();
        this.accountId = accountId;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = datetime;
    }
}
