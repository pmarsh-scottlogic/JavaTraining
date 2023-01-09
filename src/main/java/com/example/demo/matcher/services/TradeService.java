package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Trade;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TradeService {
    @Getter
    List<Trade> trades;

    public TradeService() {
        trades = new ArrayList<>();
    }

    public void add(Trade trade) {
        trades.add(trade);
    }

}
