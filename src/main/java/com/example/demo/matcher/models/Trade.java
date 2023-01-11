package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @ToString
public class Trade implements Comparable<Trade> {
    private final UUID tradeId;
    private final UUID accountIdBuyer;
    private final UUID orderIdBuy;
    private final UUID accountIdSeller;
    private final UUID orderIdSell;
    private final BigDecimal price;
    private final BigDecimal quantity;
    private final Long datetime;

    public Trade(UUID accountIdBuyer,
                 UUID orderIdBuy,
                 UUID accountIdSeller,
                 UUID orderIdSell,
                 BigDecimal price,
                 BigDecimal quantity,
                 Long datetime) {
        this.tradeId = UUID.randomUUID();
        this.accountIdBuyer = accountIdBuyer;
        this.orderIdBuy = orderIdBuy;
        this.accountIdSeller = accountIdSeller;
        this.orderIdSell = orderIdSell;
        this.price = price;
        this.quantity = quantity;
        this.datetime = datetime;
    }

    @Override
    public int compareTo(Trade o) {
        return this.datetime.compareTo(o.getDatetime());
    }
}
