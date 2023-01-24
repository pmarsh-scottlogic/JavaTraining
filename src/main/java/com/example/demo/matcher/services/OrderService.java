package com.example.demo.matcher.services;

import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final List<OrderObj> orders;

    public OrderService() {
        orders = new ArrayList<>();
    }

    public List<OrderObj> get() {
        return orders;
    }

    public void add(OrderObj order) {
        orders.add(order);
    }

    public void remove(OrderObj order) {
        orders.remove(order);
    }

    public List<OrderbookItem> getOrderbook(OrderAction action, String username) {
        // filter the order list by action
        List<OrderObj> filtered = this.get().stream()
                .filter(order -> order.getAction() == action && order.getUsername().equals(username))
                .collect(Collectors.toList());
        return makeOrderbook(filtered, action);
    }

    public List<OrderbookItem> getOrderbook(OrderAction action) {
        // filter the order list by action
        List<OrderObj> filtered = this.get().stream()
                .filter(order -> order.getAction() == action)
                .collect(Collectors.toList());
        return makeOrderbook(filtered, action);
    }

    private static List<OrderbookItem> makeOrderbook(List<OrderObj> orderList, OrderAction action) {
        // aggregate orders
        List<OrderbookItem> orderbook = aggregateOrders(orderList);

        // sort increasing / decreasing by price depending on the action
        if (action == OrderAction.BUY) orderbook.sort(Collections.reverseOrder());
        else Collections.sort(orderbook);

        return orderbook;
    }

    private static List<OrderbookItem> aggregateOrders(List<OrderObj> orderList) {
        // aggregate orders using hashmap
        Map<BigDecimal, BigDecimal> aggregated = new HashMap<>();
        for (OrderObj order : orderList) {
            aggregated.merge(order.getPrice(), order.getQuantity(), BigDecimal::add);
        }

        // convert hashmap to ArrayList
        List<OrderbookItem> orderbook = new ArrayList<>();
        for (BigDecimal price : aggregated.keySet()) {
            orderbook.add(new OrderbookItem(price, aggregated.get(price)));
        }
        return orderbook;
    }

    public List<OrderbookItem> getOrderDepth(OrderAction action) {
        List<OrderbookItem> orderBook = this.getOrderbook(action);
        List<OrderbookItem> orderDepth = new ArrayList<>();
        BigDecimal runningTotal = new BigDecimal(0);
        for (OrderbookItem obi : orderBook) {
            runningTotal = runningTotal.add(obi.getQuantity());
            orderDepth.add(new OrderbookItem(obi.getPrice(), runningTotal));
        }
        return orderDepth;
    }

    public static List<OrderObj> sortAsc(List<OrderObj> orders) {
        List<OrderObj> sorted = new ArrayList<>(orders);
        sorted.sort((order1, order2) -> {
            int priceComp = order1.getPrice().subtract(order2.getPrice()).signum();
            int datetimeComp = order1.getDatetime().compareTo(order2.getDatetime());
            if (priceComp != 0) return priceComp;
            else return datetimeComp;
        });
        return sorted;
    }

    public static List<OrderObj> sortDesc(List<OrderObj> orders) {
        List<OrderObj> sorted = new ArrayList<>(orders);
        sorted.sort((order1, order2) -> {
            int priceComp = order2.getPrice().subtract(order1.getPrice()).signum();
            int datetimeComp = order1.getDatetime().compareTo(order2.getDatetime());
            if (priceComp != 0) return priceComp;
            else return datetimeComp;
        });
        return sorted;
    }
}
