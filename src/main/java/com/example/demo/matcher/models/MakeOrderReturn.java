package com.example.demo.matcher.models;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MakeOrderReturn {
    private final List<OrderbookItem> buy;
    private final List<OrderbookItem> sell;
    private final List<OrderbookItem> buyPrivate;
    private final List<OrderbookItem> sellPrivate;
    private final List<Trade> history;
    private final List<OrderbookItem> orderDepthBuy;
    private final List<OrderbookItem> orderDepthSell;
}
