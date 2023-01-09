package com.example.demo.matcher;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Matcher {
    private final OrderService orderService;
    private final TradeService tradeService;

    @Autowired
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

            BigDecimal tradePrice = matchedOrder.getPrice();
            BigDecimal tradeQuantity = newOrder.getQuantity().min(matchedOrder.getQuantity());

            tradeService.add(new Trade(buyOrder.getAccountId(),
                    buyOrder.getOrderId(),
                    sellOrder.getAccountId(),
                    sellOrder.getOrderId(),
                    tradePrice,
                    tradeQuantity,
                    LocalDateTime.now()));

            newOrder.setQuantity(newOrder.getQuantity().subtract(tradeQuantity));
            matchedOrder.setQuantity(matchedOrder.getQuantity().subtract(tradeQuantity));

            if (matchedOrder.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                orderService.remove(matchedOrder);
            }
        } while(newOrder.getQuantity().compareTo(BigDecimal.ZERO) > 0);

        if (newOrder.getQuantity().compareTo(BigDecimal.ZERO) > 0) orderService.add(newOrder);
    }

    private Order getMatchingOrder(Order newOrder) {
        // match the new order to the best order of opposite action (sorted by price and then time)

        // get all orders
        List<Order> eligibleOrders = new ArrayList<>(orderService.get());

        // filter incompatible actions and accounts
        eligibleOrders = eligibleOrders.stream()
                .filter(
                        order -> order.getAction() != newOrder.getAction() && !Objects.equals(order.getAccountId(),
                                newOrder.getAccountId()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (newOrder.getAction() == OrderAction.BUY) {
            // filter out sell orders that are being sold for more than the buy price of the new order
            eligibleOrders = eligibleOrders.stream()
                    .filter(order -> order.getPrice().compareTo(newOrder.getPrice()) <= 0)
                    .collect(Collectors.toCollection(ArrayList::new));
            eligibleOrders = OrderService.sortDesc(eligibleOrders);
        }
        else {
            // filter out buy orders that are buying for less than the sell price of the new order
            eligibleOrders = eligibleOrders.stream()
                    .filter(order -> order.getPrice().compareTo(newOrder.getPrice()) >= 0)
                    .collect(Collectors.toCollection(ArrayList::new));
            eligibleOrders = OrderService.sortAsc(eligibleOrders);
        }
        return eligibleOrders.size() == 0? null : eligibleOrders.get(0);
    }
}
