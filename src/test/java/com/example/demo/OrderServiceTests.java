package com.example.demo;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
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

    @Test
    void ItShouldGenerateAnOrderbookWithBuyAction() {
        Order order1 = new Order("account1", 20, 9, OrderAction.BUY);
        Order order2 = new Order("account2", 10, 7, OrderAction.BUY);
        Order order3 = new Order("account3", 20, 9, OrderAction.BUY);
        Order order4 = new Order("account4", 10, 10, OrderAction.BUY);
        Order order5 = new Order("account5", 30, 19, OrderAction.BUY);
        Order order6 = new Order("account6", 40, 100, OrderAction.SELL);

        orderService.add(order1);
        orderService.add(order2);
        orderService.add(order3);
        orderService.add(order4);
        orderService.add(order5);
        orderService.add(order6);

        OrderbookItem obi1 = new OrderbookItem(30, 19);
        OrderbookItem obi2 = new OrderbookItem(20, 18);
        OrderbookItem obi3 = new OrderbookItem(10, 17);
        ArrayList<OrderbookItem> expected = new ArrayList<OrderbookItem>();
        expected.add(obi1);
        expected.add(obi2);
        expected.add(obi3);

        assertThat(orderService.orderbook(OrderAction.BUY)).isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithSellAction() {
        Order order1 = new Order("account1", 20, 9, OrderAction.SELL);
        Order order2 = new Order("account2", 10, 7, OrderAction.SELL);
        Order order3 = new Order("account3", 20, 9, OrderAction.SELL);
        Order order4 = new Order("account4", 10, 10, OrderAction.SELL);
        Order order5 = new Order("account5", 30, 19, OrderAction.SELL);
        Order order6 = new Order("account6", 40, 100, OrderAction.BUY);

        orderService.add(order1);
        orderService.add(order2);
        orderService.add(order3);
        orderService.add(order4);
        orderService.add(order5);
        orderService.add(order6);

        OrderbookItem obi1 = new OrderbookItem(30, 19);
        OrderbookItem obi2 = new OrderbookItem(20, 18);
        OrderbookItem obi3 = new OrderbookItem(10, 17);
        ArrayList<OrderbookItem> expected = new ArrayList<OrderbookItem>();
        expected.add(obi1);
        expected.add(obi2);
        expected.add(obi3);

        assertThat(orderService.orderbook(OrderAction.SELL)).isEqualTo(expected);
    }
}
