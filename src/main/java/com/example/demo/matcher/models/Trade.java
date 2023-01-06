package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @ToString
public class Trade {
    private final UUID tradeId;
    private final UUID accountIdBuyer;
    private final UUID orderIdBuy;
    private final UUID accountIdSeller;
    private final UUID orderIdSell;
    private final BigDecimal price;
    private final BigDecimal quantity;
    private final LocalDateTime datetime;

    public Trade(UUID accountIdBuyer,
                 UUID orderIdBuy,
                 UUID accountIdSeller,
                 UUID orderIdSell,
                 BigDecimal price,
                 BigDecimal quantity,
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
