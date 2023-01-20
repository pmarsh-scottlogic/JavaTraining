package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @ToString
public class Order {
    private final UUID orderId;
    private final String username;
    private final BigDecimal price;
    @Setter
    private BigDecimal quantity;
    private final OrderAction action;
    private final Long datetime;

    public static final int minPrice = 0;
    public static final int maxPrice = 1000000000;
    public static final int minQuantity = 0;
    public static final int maxQuantity = 1000000000;

    public Order(String username, BigDecimal price, BigDecimal quantity, OrderAction action) {
        this.orderId = UUID.randomUUID(); // use some sort of UUId generating library later for this
        this.username = username;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = Instant.now().toEpochMilli();
    }

    public Order(String username, BigDecimal price, BigDecimal quantity, OrderAction action, Long datetime) {
        this.orderId = UUID.randomUUID();
        this.username = username;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = datetime;
    }
}
