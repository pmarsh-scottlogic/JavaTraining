package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Trade;

import java.util.ArrayList;
import java.util.List;

public class TradeService {
    List<Trade> trades;

    public TradeService() {
        trades = new ArrayList<>();
    }

    public void add(Trade trade) {
        trades.add(trade);
    }

    public List<Trade> get() {
        return trades;
    }


}
