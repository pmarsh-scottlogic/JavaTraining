package com.example.demo;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

        orderService.add(order1);
        assertThat(orderService.get()).isEqualTo(order1);
    }
}
