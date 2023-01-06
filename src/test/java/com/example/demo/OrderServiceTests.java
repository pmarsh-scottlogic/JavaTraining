package com.example.demo;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;

@SpringBootTest
public class OrderServiceTests {
    OrderService orderService;

    @BeforeEach
    void InitialiseOrderService() {
        orderService = spy(new OrderService());
    }

    @Test
    void ItShouldAddOrders() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(1), OrderAction.BUY);
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(1), OrderAction.SELL);

        orderService.add(order1);
        orderService.add(order2);

        assertThat(orderService.get()).isEqualTo(Arrays.asList(order1, order2));
    }

    @Test
    void ItShouldRemoveOrders() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(1), OrderAction.BUY);
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(1), OrderAction.SELL);
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(1), OrderAction.SELL);

        orderService.add(order1);
        orderService.add(order2);
        orderService.add(order3);

        orderService.remove(order2);
        assertThat(orderService.get()).isEqualTo(Arrays.asList(order1, order3));

        orderService.remove(order3);
        assertThat(orderService.get()).isEqualTo(List.of(order1));

        orderService.remove(order1);
        assertThat(orderService.get()).isEqualTo(List.of());
    }

    @Test
    void ItShouldGenerateAnOrderbookWithBuyAction() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(20), new BigDecimal(9), OrderAction.BUY);
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(10), new BigDecimal(7), OrderAction.BUY);
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(20), new BigDecimal(9), OrderAction.BUY);
        Order order4 = new Order(UUID.randomUUID(), new BigDecimal(10), new BigDecimal(10), OrderAction.BUY);
        Order order5 = new Order(UUID.randomUUID(), new BigDecimal(30), new BigDecimal(19), OrderAction.BUY);
        Order order6 = new Order(UUID.randomUUID(), new BigDecimal(40), new BigDecimal(100), OrderAction.SELL);

        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4, order5, order6)
        ));

        OrderbookItem obi1 = new OrderbookItem(new BigDecimal(30), new BigDecimal(19));
        OrderbookItem obi2 = new OrderbookItem(new BigDecimal(20), new BigDecimal(18));
        OrderbookItem obi3 = new OrderbookItem(new BigDecimal(10), new BigDecimal(17));
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi1, obi2, obi3)
        );

        assertThat(orderService.getOrderbook(OrderAction.BUY))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithSellAction() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(20), new BigDecimal(9), OrderAction.SELL);
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(10), new BigDecimal(7), OrderAction.SELL);
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(20), new BigDecimal(9), OrderAction.SELL);
        Order order4 = new Order(UUID.randomUUID(), new BigDecimal(10), new BigDecimal(10), OrderAction.SELL);
        Order order5 = new Order(UUID.randomUUID(), new BigDecimal(30), new BigDecimal(19), OrderAction.SELL);
        Order order6 = new Order(UUID.randomUUID(), new BigDecimal(40), new BigDecimal(100), OrderAction.BUY);


        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4, order5, order6)
        ));

        OrderbookItem obi1 = new OrderbookItem(new BigDecimal(30), new BigDecimal(19));
        OrderbookItem obi2 = new OrderbookItem(new BigDecimal(20), new BigDecimal(18));
        OrderbookItem obi3 = new OrderbookItem(new BigDecimal(10), new BigDecimal(17));
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi3, obi2, obi1)
        );

        assertThat(orderService.getOrderbook(OrderAction.SELL))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithBuyActionAndAccountId() {
        UUID specifiedAccount = UUID.randomUUID();
        Order order1 = new Order(specifiedAccount, new BigDecimal(20), new BigDecimal(9), OrderAction.BUY);
        Order order2 = new Order(specifiedAccount, new BigDecimal(10), new BigDecimal(7), OrderAction.BUY);
        Order order3 = new Order(UUID.randomUUID(),new BigDecimal( 20), new BigDecimal(9), OrderAction.BUY);
        Order order4 = new Order(UUID.randomUUID(),new BigDecimal( 10), new BigDecimal(10), OrderAction.BUY);
        Order order5 = new Order(UUID.randomUUID(),new BigDecimal( 30), new BigDecimal(19), OrderAction.BUY);
        Order order6 = new Order(specifiedAccount, new BigDecimal(40), new BigDecimal(100), OrderAction.SELL);

        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4, order5, order6)
        ));

        OrderbookItem obi1 = new OrderbookItem(new BigDecimal(20), new BigDecimal(9));
        OrderbookItem obi2 = new OrderbookItem(new BigDecimal(10), new BigDecimal(7));
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi1, obi2)
        );

        assertThat(orderService.getOrderbook(OrderAction.BUY, specifiedAccount))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateOrderDepthWithBuyAction() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(20), new BigDecimal(9), OrderAction.BUY);
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(10), new BigDecimal(7), OrderAction.BUY);
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(20), new BigDecimal(9), OrderAction.BUY);
        Order order4 = new Order(UUID.randomUUID(), new BigDecimal(10), new BigDecimal(10), OrderAction.BUY);
        Order order5 = new Order(UUID.randomUUID(), new BigDecimal(30), new BigDecimal(19), OrderAction.BUY);
        Order order6 = new Order(UUID.randomUUID(), new BigDecimal(40), new BigDecimal(100), OrderAction.SELL);

        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4, order5, order6)
        ));

        OrderbookItem obi1 = new OrderbookItem(new BigDecimal(30), new BigDecimal(19));
        OrderbookItem obi2 = new OrderbookItem(new BigDecimal(20), new BigDecimal(37));
        OrderbookItem obi3 = new OrderbookItem(new BigDecimal(10), new BigDecimal(54));
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi1, obi2, obi3)
        );

        assertThat(orderService.getOrderDepth(OrderAction.BUY))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersAscendingByPrice() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(3), new BigDecimal(10), OrderAction.BUY);
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(4), new BigDecimal(10), OrderAction.BUY);
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(2), new BigDecimal(10), OrderAction.BUY);
        Order order4 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY);

        ArrayList<Order> unsorted = new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4)
        );

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(order4, order3, order1, order2)
        );

        assertThat(OrderService.sortAsc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersAscendingByPriceThenDatetime() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(3), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 18, 0, 0));
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 16, 0, 0));
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 17, 0, 0));
        Order order4 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 15, 0, 0));

        ArrayList<Order> unsorted = new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4)
        );

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(order4, order2, order3, order1)
        );

        assertThat(OrderService.sortAsc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersDescendingByPrice() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(3), new BigDecimal(10), OrderAction.BUY);
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(4), new BigDecimal(10), OrderAction.BUY);
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(2), new BigDecimal(10), OrderAction.BUY);
        Order order4 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY);

        ArrayList<Order> unsorted = new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4)
        );

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(order2, order1, order3, order4)
        );

        assertThat(OrderService.sortDesc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersDescendingByPriceThenAscendingByDatetime() {
        Order order1 = new Order(UUID.randomUUID(), new BigDecimal(3), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 18, 0, 0));
        Order order2 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 16, 0, 0));
        Order order3 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 17, 0, 0));
        Order order4 = new Order(UUID.randomUUID(), new BigDecimal(1), new BigDecimal(10), OrderAction.BUY, LocalDateTime.of(2000, Month.JANUARY, 15, 0, 0));

        ArrayList<Order> unsorted = new ArrayList<>(
                Arrays.asList(order1, order2, order3, order4)
        );

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(order1, order4, order2, order3)
        );

        assertThat(OrderService.sortDesc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
