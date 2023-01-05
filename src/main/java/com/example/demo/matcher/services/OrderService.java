package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;

import java.util.ArrayList;

public class OrderService {
    private final ArrayList<Order> orders;

    public OrderService() {
        orders = new ArrayList<Order>();
    }

    public ArrayList<Order> get() {
        return orders;
    }

    public void add(Order order) {
        orders.add(order);
    }

    public void remove(Order order) {
        orders.remove(order);
    }

    public ArrayList<OrderbookItem> orderbook(OrderAction action, String accountId) {

    }
}
