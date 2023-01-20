package com.example.demo.matcher.models;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @ToString
public class Trade implements Comparable<Trade> {
    private final UUID tradeId;
    private final String usernameBuyer;
    private final UUID orderIdBuy;
    private final String usernameSeller;
    private final UUID orderIdSell;
    private final BigDecimal price;
    private final BigDecimal quantity;
    private final Long datetime;

    public Trade(String usernameBuyer,
                 UUID orderIdBuy,
                 String usernameSeller,
                 UUID orderIdSell,
                 BigDecimal price,
                 BigDecimal quantity,
                 Long datetime) {
        this.tradeId = UUID.randomUUID();
        this.usernameBuyer = usernameBuyer;
        this.orderIdBuy = orderIdBuy;
        this.usernameSeller = usernameSeller;
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
