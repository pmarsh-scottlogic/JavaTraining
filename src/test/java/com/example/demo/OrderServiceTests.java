package com.example.demo;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
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
    @Spy
    OrderService orderService;

    public static List<Order> testOrderSet1(String primaryAction) {
        String secondaryAction = primaryAction.equals("b") ? "s" : "b";

        List<Order> testOrders = new ArrayList<>();

        testOrders.add(makeOrder("account1", 20, 9, primaryAction));
        testOrders.add(makeOrder("account1", 10, 7, primaryAction));
        testOrders.add(makeOrder("account2", 20, 9, primaryAction));
        testOrders.add(makeOrder("account3", 10, 10, primaryAction));
        testOrders.add(makeOrder("account4", 30, 19, primaryAction));
        testOrders.add(makeOrder("account1", 40, 100, secondaryAction));

        return testOrders;
    }

    @BeforeEach
    void InitialiseOrderService() {
        orderService = spy(new OrderService());
    }

    private static Order makeOrder(float price, float quantity, String strAction) {
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(UUID.randomUUID(), new BigDecimal(price), new BigDecimal(quantity), action);
    }

    private static Order makeOrder(String strAccount, float price, float quantity, String strAction) {
        UUID uuid = uuidFromString(strAccount);
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(uuid, new BigDecimal(price), new BigDecimal(quantity), action);
    }

    private static UUID uuidFromString(String s) {
        return UUID.nameUUIDFromBytes(s.getBytes());
    }

    private static OrderbookItem makeOrderbookItem(float price, float quantity) {
        return new OrderbookItem(new BigDecimal(price), new BigDecimal(quantity));
    }

    @Test
    void ItShouldAddOrders() {
        Order order1 = makeOrder(1, 1, "b");
        Order order2 = makeOrder(1, 1, "s");

        orderService.add(order1);
        orderService.add(order2);

        assertThat(orderService.get()).isEqualTo(Arrays.asList(order1, order2));
    }

    @Test
    void ItShouldRemoveOrders() {
        Order order1 = makeOrder(1, 1, "b");
        Order order2 = makeOrder(1, 1 ,"s");
        Order order3 = makeOrder(1, 1 ,"s");

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
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                testOrderSet1("b")
        ));

        OrderbookItem obi1 = makeOrderbookItem(30, 19);
        OrderbookItem obi2 = makeOrderbookItem(20, 18);
        OrderbookItem obi3 = makeOrderbookItem(10, 17);
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi1, obi2, obi3)
        );

        assertThat(orderService.getOrderbook(OrderAction.BUY))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithSellAction() {
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                testOrderSet1("s")
        ));

        OrderbookItem obi1 = makeOrderbookItem(30, 19);
        OrderbookItem obi2 = makeOrderbookItem(20, 18);
        OrderbookItem obi3 = makeOrderbookItem(10, 17);
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi3, obi2, obi1)
        );

        assertThat(orderService.getOrderbook(OrderAction.SELL))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithBuyActionAndAccountId() {
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                testOrderSet1("b")
        ));

        OrderbookItem obi1 = makeOrderbookItem(20, 9);
        OrderbookItem obi2 = makeOrderbookItem(10, 7);
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi1, obi2)
        );

        UUID specifiedAccount = uuidFromString("account1");
        assertThat(orderService.getOrderbook(OrderAction.BUY, specifiedAccount))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateOrderDepthWithBuyAction() {
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                testOrderSet1("b")
        ));

        OrderbookItem obi1 = makeOrderbookItem(30, 19);
        OrderbookItem obi2 = makeOrderbookItem(20, 37);
        OrderbookItem obi3 = makeOrderbookItem(10, 54);
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi1, obi2, obi3)
        );

        assertThat(orderService.getOrderDepth(OrderAction.BUY))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersAscendingByPrice() {
        Order order1 = makeOrder(3, 10, "b");
        Order order2 = makeOrder(4, 10, "b");
        Order order3 = makeOrder(2, 10, "b");
        Order order4 = makeOrder(1, 10, "b");

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
        Order order1 = makeOrder(3, 10, "b");
        Order order2 = makeOrder(4, 10, "b");
        Order order3 = makeOrder(2, 10, "b");
        Order order4 = makeOrder(1, 10, "b");

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
