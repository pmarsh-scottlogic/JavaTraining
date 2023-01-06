package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;

import java.util.*;
import java.util.stream.Collectors;

public class OrderService {
    private final List<Order> orders;

    public OrderService() {
        orders = new ArrayList<>();
    }

    public List<Order> get() {
        return orders;
    }

    public void add(Order order) {
        orders.add(order);
    }

    public void remove(Order order) {
        orders.remove(order);
    }

    public List<OrderbookItem> getOrderbook(OrderAction action, UUID accountId) {
        // filter the order list by action
        List<Order> filtered = this.get().stream()
                .filter(order -> order.getAction() == action && Objects.equals(order.getAccountId(), accountId))
                .collect(Collectors.toCollection(ArrayList::new));
        return makeOrderbook(filtered, action);
    }

    public List<OrderbookItem> getOrderbook(OrderAction action) {
        // filter the order list by action
        ArrayList<Order> filtered = this.get().stream()
                .filter(order -> order.getAction() == action)
                .collect(Collectors.toCollection(ArrayList::new));
        return makeOrderbook(filtered, action);
    }

    private static List<OrderbookItem> makeOrderbook(List<Order> orderList, OrderAction action) {
        // aggregate orders
        List<OrderbookItem> orderbook = aggregateOrders(orderList);

        // sort increasing / decreasing by price depending on the action
        if (action == OrderAction.BUY) orderbook.sort(Collections.reverseOrder());
        else Collections.sort(orderbook);

        return orderbook;
    }

    private static List<OrderbookItem> aggregateOrders(List<Order> orderList) {
        // aggregate orders using hashmap
        Map<Float, Float> aggregated = new HashMap<>();
        for (Order order : orderList) {
            aggregated.merge(order.getPrice(), order.getQuantity(), Float::sum);
        }

        // convert hashmap to ArrayList
        List<OrderbookItem> orderbook = new ArrayList<>();
        for (Float price : aggregated.keySet()) {
            orderbook.add(new OrderbookItem(price, aggregated.get(price)));
        }
        return orderbook;
    }

    public List<OrderbookItem> getOrderDepth(OrderAction action) {
        List<OrderbookItem> orderBook = this.getOrderbook(action);
        List<OrderbookItem> orderDepth = new ArrayList<>();
        float runningTotal = 0;
        for (OrderbookItem obi : orderBook) {
            runningTotal += obi.getQuantity();
            orderDepth.add(new OrderbookItem(obi.getPrice(), runningTotal));
        }
        return orderDepth;
    }

    public static List<Order> sortAsc(ArrayList<Order> orders) {
        List<Order> sorted = new ArrayList<>(orders);
        sorted.sort((order1, order2) -> {
            int priceComp = Math.round(order1.getPrice() - order2.getPrice());
            int datetimeComp = order1.getDatetime().compareTo(order2.getDatetime());
            if (priceComp != 0) return priceComp;
            else return datetimeComp;
        });
        return sorted;
    }

    public static List<Order> sortDesc(ArrayList<Order> orders) {
        List<Order> sorted = new ArrayList<>(orders);
        sorted.sort((order1, order2) -> {
            int priceComp = Math.round(order2.getPrice() - order1.getPrice());
            int datetimeComp = order1.getDatetime().compareTo(order2.getDatetime());
            if (priceComp != 0) return priceComp;
            else return datetimeComp;
        });
        return sorted;
    }
}
