package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @ToString
public class Order {
    final UUID id;
    final UUID accountId;
    final float price;
    float quantity;
    final OrderAction action;
    final LocalDateTime datetime;

    public Order(UUID accountId, float price, float quantity, OrderAction action) {
        this.id = UUID.randomUUID(); // use some sort of UUId generating library later for this
        this.accountId = accountId;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = LocalDateTime.now();
    }

    public Order(UUID accountId, float price, float quantity, OrderAction action, LocalDateTime datetime) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = datetime;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
