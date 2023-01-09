package com.example.demo.matcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor @ToString @Getter
public class MakeOrderReturn {
    private final List<OrderbookItem> buy;
    private final List<OrderbookItem> sell;
    private final List<OrderbookItem> buyPrivate;
    private final List<OrderbookItem> sellPrivate;
    private final List<Trade> history;
    private final List<OrderbookItem> orderDepthBuy;
    private final List<OrderbookItem> orderDepthSell;
}
