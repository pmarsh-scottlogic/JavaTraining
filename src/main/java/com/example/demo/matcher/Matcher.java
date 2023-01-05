package com.example.demo.matcher;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Matcher {
    private final OrderService orderService;
    private final TradeService tradeService;

    public Matcher(OrderService orderService, TradeService tradeService) {
        this.orderService = orderService;
        this.tradeService = tradeService;
    }

    public void match(Order newOrder) {
        Order matchedOrder;

        do {
            matchedOrder = getMatchingOrder(newOrder);
            if (matchedOrder == null) break;

            Order buyOrder = newOrder.getAction() == OrderAction.BUY? newOrder : matchedOrder;
            Order sellOrder = newOrder.getAction() == OrderAction.SELL? newOrder : matchedOrder;

            float tradePrice = matchedOrder.getPrice();
            float tradeQuantity = Math.min(newOrder.getQuantity(), matchedOrder.getQuantity());

            tradeService.add(new Trade(buyOrder.getAccountId(), buyOrder.getId(), sellOrder.getAccountId(), sellOrder.getId(), tradePrice, tradeQuantity));

            newOrder.setQuantity(newOrder.getQuantity() - tradePrice);
            matchedOrder.setQuantity(matchedOrder.getQuantity() - tradePrice);

            if (matchedOrder.getQuantity() <= 0) {
                orderService.remove(matchedOrder);
            }
        } while(newOrder.getQuantity() > 0);

        if (newOrder.getQuantity() > 0) orderService.add(newOrder);
    }

    private Order getMatchingOrder(Order newOrder) {
        // match the new order to the best order of opposite action (sorted by price and then time)

        ArrayList<Order> eligibleOrders = new ArrayList<>(orderService.get());
        eligibleOrders = eligibleOrders.stream().filter(order -> order.getAction() != newOrder.getAction() && order.getAccountId() != newOrder.getAccountId()).collect(Collectors.toCollection(ArrayList::new));;

        if (newOrder.getAction() == OrderAction.BUY) {
            eligibleOrders = eligibleOrders.stream().filter(order -> order.getPrice() <= newOrder.getPrice()).collect(Collectors.toCollection(ArrayList::new));
            eligibleOrders = OrderService.sortDesc(eligibleOrders);
        }
        else {
            eligibleOrders = eligibleOrders.stream().filter(order -> order.getPrice() >= newOrder.getPrice()).collect(Collectors.toCollection(ArrayList::new));
            eligibleOrders = OrderService.sortAsc(eligibleOrders);
        }
        return eligibleOrders.size() == 0? null : eligibleOrders.get(0);
    }
}
