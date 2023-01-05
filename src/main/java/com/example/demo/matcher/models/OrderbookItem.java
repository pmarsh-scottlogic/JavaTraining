package com.example.demo.matcher.models;

public class OrderbookItem {
    public float price;
    public float quantity;

    public OrderbookItem(float price, float quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderbookItem{" +
                "price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
