package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class OrderService {
    private final ArrayList<Order> orders;

    public OrderService() {
        orders = new ArrayList<>();
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

    public ArrayList<OrderbookItem> getOrderbook(OrderAction action, String accountId) {
        return new ArrayList<>();
    }

    public ArrayList<OrderbookItem> getOrderbook(OrderAction action) {
        // filter the order list by action
        ArrayList<Order> filtered = this.get().stream().filter(order -> order.getAction() == action).collect(Collectors.toCollection(ArrayList::new));
        return makeOrderbook(filtered, action);
    }

    private static ArrayList<OrderbookItem> makeOrderbook(ArrayList<Order> orderList, OrderAction action) {
        // aggregate orders
        ArrayList<OrderbookItem> orderbook = aggregateOrders(orderList);

        // sort increasing / decreasing by price depending on the action
        if (action == OrderAction.BUY) orderbook.sort(Collections.reverseOrder());
        else Collections.sort(orderbook);

        return orderbook;
    }

    private static ArrayList<OrderbookItem> aggregateOrders(ArrayList<Order> orderList) {
        // aggregate orders using hashmap
        HashMap<Float, Float> aggregated = new HashMap<>();
        for (Order order : orderList) {
            aggregated.merge(order.getPrice(), order.getQuantity(), Float::sum);
        }

        // convert hashmap to ArrayList
        ArrayList<OrderbookItem> orderbook = new ArrayList<>();
        for (Float price : aggregated.keySet()) {
            orderbook.add(new OrderbookItem(price, aggregated.get(price)));
        }
        return orderbook;
    }
}
