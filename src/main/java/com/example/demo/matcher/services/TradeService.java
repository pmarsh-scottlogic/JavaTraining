package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Trade;

import java.util.ArrayList;

public class TradeService {
    ArrayList<Trade> trades;

    public TradeService() {
        trades = new ArrayList<>();
    }

    public void add(Trade trade) {
        trades.add(trade);
    }

    public void remove(Trade trade) {
        trades.remove(trade);
    }

    public ArrayList<Trade> getTrades() {
        return trades;
    }
}
