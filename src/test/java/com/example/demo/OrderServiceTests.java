package com.example.demo;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class OrderServiceTests {
    OrderService orderService;
    @BeforeEach
    void InitialiseOrderService() {
        orderService = new OrderService();
    }

    @Test
    void ItShouldAddOrders() {
        Order order1 = new Order("account1", 1, 1, OrderAction.BUY);
        Order order2 = new Order("account1", 1, 1, OrderAction.SELL);

        orderService.add(order1);
        orderService.add(order2);

        assertThat(orderService.get()).isEqualTo(Arrays.asList(order1, order2));
    }

    @Test
    void ItShouldRemoveOrders() {
        Order order1 = new Order("account1", 1, 1, OrderAction.BUY);
        Order order2 = new Order("account2", 1, 1, OrderAction.SELL);
        Order order3 = new Order("account3", 1, 1, OrderAction.SELL);

        orderService.add(order1);
        orderService.add(order2);
        orderService.add(order3);

        orderService.remove(order2);
        assertThat(orderService.get()).isEqualTo(Arrays.asList(order1, order3));

        orderService.remove(order3);
        assertThat(orderService.get()).isEqualTo(Arrays.asList(order1));

        orderService.remove(order1);
        assertThat(orderService.get()).isEqualTo(Arrays.asList());
    }
}
