package com.example.demo.matcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString @Getter @Setter @AllArgsConstructor
public class OrderbookItem implements Comparable<OrderbookItem>{
    private final float price;
    private final float quantity;

    @Override
    public int compareTo(OrderbookItem obi) {
        return Float.compare(this.price, obi.price);
    }
}
