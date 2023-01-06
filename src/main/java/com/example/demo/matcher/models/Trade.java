package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @ToString
public class Trade {
    private final UUID tradeId;
    private final UUID accountIdBuyer;
    private final UUID orderIdBuy;
    private final UUID accountIdSeller;
    private final UUID orderIdSell;
    private final float price;
    private final float quantity;
    private final LocalDateTime datetime;

    public Trade(UUID accountIdBuyer,
                 UUID orderIdBuy,
                 UUID accountIdSeller,
                 UUID orderIdSell,
                 float price,
                 float quantity,
                 LocalDateTime datetime) {
        this.tradeId = UUID.randomUUID();
        this.accountIdBuyer = accountIdBuyer;
        this.orderIdBuy = orderIdBuy;
        this.accountIdSeller = accountIdSeller;
        this.orderIdSell = orderIdSell;
        this.price = price;
        this.quantity = quantity;
        this.datetime = datetime;
    }
}
