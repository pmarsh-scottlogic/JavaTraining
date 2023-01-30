package com.example.demo.matcher.services;

import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.repo.OrderRepo;
import com.example.demo.security.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {
    private final List<OrderObj> orders = new ArrayList<>();
    private final OrderRepo orderRepo;
    private  final UserRepo userRepo;

    public void removeAll() {
        orderRepo.deleteAll();
    }

    public List<OrderObj> get() {
        log.info("Fetching all orders");
        return orderRepo.findAll();
    }

    public OrderObj add(OrderObj order) {
        log.info("Saving new order {} to the database", order.toString());
        return orderRepo.save(order);
    }

    public void remove(OrderObj order) {
        log.info("Removing order {} from the database", order.toString());
        orderRepo.delete(order);
    }

    public List<OrderbookItem> getOrderbook(OrderAction action, String username) {
        return makeOrderbook(orderRepo.findByUsernameAndAction(username, action.ordinal()), action);
    }

    public List<OrderbookItem> getOrderbook(OrderAction action) {
        return makeOrderbook(orderRepo.findByAction(action), action);
    }

    private static List<OrderbookItem> makeOrderbook(List<OrderObj> orderList, OrderAction action) {
        // aggregate orders
        List<OrderbookItem> orderbook = aggregateOrders(orderList);

        // sort increasing / decreasing by price depending on the action
        if (action == OrderAction.BUY) orderbook.sort(Collections.reverseOrder());
        else Collections.sort(orderbook);

        return orderbook;
    }

    public List<OrderObj> getEligibleOrders(OrderObj newOrder) {
        return newOrder.getAction() == OrderAction.BUY ?
                orderRepo.getEligibleSellOrders(newOrder.getUser().getUsername(), newOrder.getPrice().doubleValue()) :
                orderRepo.getEligibleBuyOrders(newOrder.getUser().getUsername(), newOrder.getPrice().doubleValue());
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
}
