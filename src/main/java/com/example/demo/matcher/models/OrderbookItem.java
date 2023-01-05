package com.example.demo.matcher.models;

public class OrderbookItem implements Comparable<OrderbookItem>{
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

    @Override
    public int compareTo(OrderbookItem obi) {
        return Float.compare(this.price, obi.price);
    }
}
