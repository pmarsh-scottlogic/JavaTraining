package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Order;

import java.util.ArrayList;

public class OrderService {
    private ArrayList<Order> orders;

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
}
