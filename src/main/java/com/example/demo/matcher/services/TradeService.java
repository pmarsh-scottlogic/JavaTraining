package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Trade;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeService {
    @Getter
    private final List<Trade> trades;

    public TradeService() {
        trades = new ArrayList<>();
    }

    public void add(Trade trade) {
        trades.add(trade);
    }

    public List<Trade> getRecent() {
        Collections.sort(trades, Collections.reverseOrder());
        int returnCount = 30;
        return trades.stream().limit(returnCount).collect(Collectors.toList());
    }
}
