package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @ToString @Entity @NoArgsConstructor
public class OrderObj {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;
    private String username;
    private BigDecimal price;
    @Setter
    private BigDecimal quantity;
    private OrderAction action;
    private Long datetime;

    public static final int minPrice = 0;
    public static final int maxPrice = 1000000000;
    public static final int minQuantity = 0;
    public static final int maxQuantity = 1000000000;

    public OrderObj(String username, BigDecimal price, BigDecimal quantity, OrderAction action) {
        this.orderId = null; // use some sort of UUId generating library later for this
        this.username = username;
        this.price = price;
        this.quantity = quantity;
        this.action = action;
        this.datetime = Instant.now().toEpochMilli();
    }
}
