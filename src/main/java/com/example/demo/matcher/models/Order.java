package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @ToString @Entity
public class Order {
    @Id
    private final Long orderId;

    @ManyToOne
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
        this.orderId = null; // use some sort of UUId generating library later for this
        this.username = username;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = Instant.now().toEpochMilli();
    }

    public Order(String username, BigDecimal price, BigDecimal quantity, OrderAction action, Long datetime) {
        this.orderId = null;
        this.username = username;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = datetime;
    }
}
