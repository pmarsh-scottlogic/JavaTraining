package com.example.demo.Matcher;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.models.OrderObj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;

@RunWith(SpringRunner.class)
public class OrderServiceTests {

    @SpyBean
    OrderService orderService;

    @Test
    void ItShouldAddOrders() {
        OrderObj order1 = TestUtils.makeOrder(1, 1, "b");
        OrderObj order2 = TestUtils.makeOrder(1, 1, "s");

        orderService.add(order1);
        orderService.add(order2);

        assertThat(orderService.get()).isEqualTo(Arrays.asList(order1, order2));
    }

    @Test
    void ItShouldRemoveOrders() {
        OrderObj order1 = TestUtils.makeOrder(1, 1, "b");
        OrderObj order2 = TestUtils.makeOrder(1, 1 ,"s");
        OrderObj order3 = TestUtils.makeOrder(1, 1 ,"s");

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

    public static List<OrderObj> testOrderSet1(String primaryAction) {
        String secondaryAction = primaryAction.equals("b") ? "s" : "b";

        List<OrderObj> testOrders = new ArrayList<>();

        testOrders.add(TestUtils.makeOrder("account1", 20, 9, primaryAction));
        testOrders.add(TestUtils.makeOrder("account1", 10, 7, primaryAction));
        testOrders.add(TestUtils.makeOrder("account2", 20, 9, primaryAction));
        testOrders.add(TestUtils.makeOrder("account3", 10, 10, primaryAction));
        testOrders.add(TestUtils.makeOrder("account4", 30, 19, primaryAction));
        testOrders.add(TestUtils.makeOrder("account1", 40, 100, secondaryAction));

        return testOrders;
    }

    @Test
    void ItShouldGenerateAnOrderbookWithBuyAction() {
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                testOrderSet1("b")
        ));

        OrderbookItem obi1 = TestUtils.makeOrderbookItem(30, 19);
        OrderbookItem obi2 = TestUtils.makeOrderbookItem(20, 18);
        OrderbookItem obi3 = TestUtils.makeOrderbookItem(10, 17);
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

        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(
                        TestUtils.makeOrderbookItem(10, 17),
                        TestUtils.makeOrderbookItem(20, 18),
                        TestUtils.makeOrderbookItem(30, 19)
                ));

        assertThat(orderService.getOrderbook(OrderAction.SELL))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithBuyActionAndUsername() {
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                testOrderSet1("b")
        ));

        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(
                        TestUtils.makeOrderbookItem(20, 9),
                        TestUtils.makeOrderbookItem(10, 7)
                ));

        assertThat(orderService.getOrderbook(OrderAction.BUY, "account1"))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateOrderDepthWithBuyAction() {
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                testOrderSet1("b")
        ));

        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(
                        TestUtils.makeOrderbookItem(30, 19),
                        TestUtils.makeOrderbookItem(20, 37),
                        TestUtils.makeOrderbookItem(10, 54)
                ));

        assertThat(orderService.getOrderDepth(OrderAction.BUY))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    public static List<OrderObj> testOrderSet2() {
        List<OrderObj> testOrders = new ArrayList<>();

        testOrders.add(TestUtils.makeOrder(3, 10, "b"));
        testOrders.add(TestUtils.makeOrder(4, 10, "b"));
        testOrders.add(TestUtils.makeOrder(2, 10, "b"));
        testOrders.add(TestUtils.makeOrder(1, 10, "b"));

        return testOrders;
    }

    public static List<OrderObj> testOrderSet3() {
        List<OrderObj> testOrders = new ArrayList<>();

        testOrders.add(TestUtils.makeOrder(3, 10, "b", 4));
        testOrders.add(TestUtils.makeOrder(1, 10, "b", 2));
        testOrders.add(TestUtils.makeOrder(1, 10, "b", 3));
        testOrders.add(TestUtils.makeOrder(1, 10, "b", 1));

        return testOrders;
    }

    @Test
    void ItShouldSortOrdersAscendingByPrice() {
        ArrayList<OrderObj> unsorted = new ArrayList<>(testOrderSet2());

        ArrayList<OrderObj> expected = new ArrayList<>(
                Arrays.asList(
                        unsorted.get(3),
                        unsorted.get(2),
                        unsorted.get(0),
                        unsorted.get(1)));

        assertThat(OrderService.sortAsc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersAscendingByPriceThenDatetime() {
        ArrayList<OrderObj> unsorted = new ArrayList<>(
                testOrderSet3()
        );

        ArrayList<OrderObj> expected = new ArrayList<>(
                Arrays.asList(
                        unsorted.get(3),
                        unsorted.get(1),
                        unsorted.get(2),
                        unsorted.get(0)
                ));

        assertThat(OrderService.sortAsc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersDescendingByPrice() {
        ArrayList<OrderObj> unsorted = new ArrayList<>(testOrderSet2());

        ArrayList<OrderObj> expected = new ArrayList<>(
                Arrays.asList(unsorted.get(1),
                        unsorted.get(0),
                        unsorted.get(2),
                        unsorted.get(3)
                ));

        assertThat(OrderService.sortDesc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldSortOrdersDescendingByPriceThenAscendingByDatetime() {
        ArrayList<OrderObj> unsorted = new ArrayList<>(testOrderSet3());

        ArrayList<OrderObj> expected = new ArrayList<>(
                Arrays.asList(
                        unsorted.get(0),
                        unsorted.get(3),
                        unsorted.get(1),
                        unsorted.get(2)
                ));

        assertThat(OrderService.sortDesc(unsorted))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
