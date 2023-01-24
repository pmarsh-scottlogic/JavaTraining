package com.example.demo.matcher.services;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
public class OrderRepoHandler {
    private final OrderRepo orderRepo;

    public Order saveOrder(Order order) {
        log.info("Saving new order {} to the database", order.toString());
        return orderRepo.save(order);
    }

    public List<Order> getOrders() {
        log.info("Fetching all orders");
        return orderRepo.findAll();
    }
}
