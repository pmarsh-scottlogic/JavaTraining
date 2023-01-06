package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @ToString
public class Order {
    final UUID orderId;
    final UUID accountId;
    final BigDecimal price;
    BigDecimal quantity;
    final OrderAction action;
    final LocalDateTime datetime;

    public Order(UUID accountId, BigDecimal price, BigDecimal quantity, OrderAction action) {
        this.orderId = UUID.randomUUID();; // use some sort of UUId generating library later for this
        this.accountId = accountId;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = LocalDateTime.now();
    }

    public Order(UUID accountId, BigDecimal price, BigDecimal quantity, OrderAction action, LocalDateTime datetime) {
        this.orderId = UUID.randomUUID();
        this.accountId = accountId;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = datetime;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
