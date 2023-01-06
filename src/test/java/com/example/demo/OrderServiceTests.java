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

    private static Order makeOrder(float price, float quantity, String strAction, int datetimeRank) {
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(UUID.randomUUID(),
                new BigDecimal(price),
                new BigDecimal(quantity),
                action,
                LocalDateTime.of(2000, Month.JANUARY, 18, 0, 0).plusDays(datetimeRank));
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

    public static List<Order> testOrderSet2() {
        List<Order> testOrders = new ArrayList<>();

        testOrders.add(makeOrder(3, 10, "b"));
        testOrders.add(makeOrder(4, 10, "b"));
        testOrders.add(makeOrder(2, 10, "b"));
        testOrders.add(makeOrder(1, 10, "b"));

        return testOrders;
    }

    public static List<Order> testOrderSet3() {
        List<Order> testOrders = new ArrayList<>();

        testOrders.add(makeOrder(3, 10, "b", 4));
        testOrders.add(makeOrder(1, 10, "b", 2));
        testOrders.add(makeOrder(1, 10, "b", 3));
        testOrders.add(makeOrder(1, 10, "b", 1));

        return testOrders;
    }

    @Test
    void ItShouldSortOrdersAscendingByPrice() {
        ArrayList<Order> unsorted = new ArrayList<>(testOrderSet2());

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(unsorted.get(3), unsorted.get(2), unsorted.get(0), unsorted.get(1))
        );

        assertThat(OrderService.sortAsc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersAscendingByPriceThenDatetime() {
        ArrayList<Order> unsorted = new ArrayList<>(
                testOrderSet3()
        );

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(unsorted.get(3), unsorted.get(1) ,unsorted.get(2) ,unsorted.get(0))
        );

        assertThat(OrderService.sortAsc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersDescendingByPrice() {
        ArrayList<Order> unsorted = new ArrayList<>(testOrderSet2());

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(unsorted.get(1), unsorted.get(0), unsorted.get(2), unsorted.get(3))
        );

        assertThat(OrderService.sortDesc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersDescendingByPriceThenAscendingByDatetime() {
        ArrayList<Order> unsorted = new ArrayList<>(testOrderSet3());

        ArrayList<Order> expected = new ArrayList<>(
                Arrays.asList(unsorted.get(0), unsorted.get(3) ,unsorted.get(1) ,unsorted.get(2))
        );

        assertThat(OrderService.sortDesc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
