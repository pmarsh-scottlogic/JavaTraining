package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter @ToString
public class Trade {
    private final String tradeId;
    private final String accountIdBuyer;
    private final String orderIdBuy;
    private final String accountIdSeller;
    private final String orderIdSell;
    private final float price;
    private final float quantity;
    private final LocalDateTime datetime;

    public Trade(String accountIdBuyer,
                 String orderIdBuy,
                 String accountIdSeller,
                 String orderIdSell,
                 float price,
                 float quantity,
                 LocalDateTime datetime) {
        this.tradeId = "RandomTradeId";
        this.accountIdBuyer = accountIdBuyer;
        this.orderIdBuy = orderIdBuy;
        this.accountIdSeller = accountIdSeller;
        this.orderIdSell = orderIdSell;
        this.price = price;
        this.quantity = quantity;
        this.datetime = datetime;
    }
}
