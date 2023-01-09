package com.example.demo.matcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString @Getter @AllArgsConstructor
public class OrderbookItem implements Comparable<OrderbookItem>{
    private final BigDecimal price;
    private final BigDecimal quantity;

    @Override
    public int compareTo(OrderbookItem obi) {
        return this.price.compareTo(obi.price);
    }
}
