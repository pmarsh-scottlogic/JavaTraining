package com.example.demo.matcher.models;

import java.time.LocalDateTime;

public class Trade {
    private final String tradeId;
    private final String accountIdBuyer;
    private final String orderIdBuy;
    private final String accountIdSeller;
    private final String orderIdSell;
    private final float price;
    private final float quantity;
    private final LocalDateTime datetime;

    public Trade(String accountIdBuyer, String orderIdBuy, String accountIdSeller, String orderIdSell, float price, float quantity, LocalDateTime datetime) {
        this.tradeId = "RandomTradeId";
        this.accountIdBuyer = accountIdBuyer;
        this.orderIdBuy = orderIdBuy;
        this.accountIdSeller = accountIdSeller;
        this.orderIdSell = orderIdSell;
        this.price = price;
        this.quantity = quantity;
        this.datetime = datetime;
    }

    public Trade(String accountIdBuyer, String orderIdBuy, String accountIdSeller, String orderIdSell, float price, float quantity) {
        this.tradeId = "RandomTradeId";
        this.accountIdBuyer = accountIdBuyer;
        this.orderIdBuy = orderIdBuy;
        this.accountIdSeller = accountIdSeller;
        this.orderIdSell = orderIdSell;
        this.price = price;
        this.quantity = quantity;
        this.datetime = LocalDateTime.now();
    }

    public String getAccountIdBuyer() {
        return accountIdBuyer;
    }

    public String getOrderIdBuy() {
        return orderIdBuy;
    }

    public String getAccountIdSeller() {
        return accountIdSeller;
    }

    public String getOrderIdSell() {
        return orderIdSell;
    }

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "accountIdBuyer='" + accountIdBuyer + '\'' +
                ", orderIdBuy='" + orderIdBuy + '\'' +
                ", accountIdSeller='" + accountIdSeller + '\'' +
                ", orderIdSell='" + orderIdSell + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", datetime=" + datetime +
                '}';
    }
}
