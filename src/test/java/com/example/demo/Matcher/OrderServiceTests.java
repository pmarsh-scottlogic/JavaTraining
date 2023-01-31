package com.example.demo.Matcher;

import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.models.OrderObj;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("uninitialised")
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTests {
     @Autowired
     private OrderService orderService;

     @Autowired
     private UserService userService;

    private AppUser testUser1;
    private AppUser testUser2;

    @BeforeEach
    public void setup() throws Exception {

        testUser1 = new AppUser(null, "testName1", "testUsername1", "testPassword1", new ArrayList<>());
        testUser2 = new AppUser(null, "testName2", "testUsername2", "testPassword2", new ArrayList<>());

        userService.saveUser(testUser1);
        userService.saveUser(testUser2);
    }

    @AfterEach
    public void cleanup() {
        orderService.removeAll();
        userService.deleteAll();
    }

    @Test
    void ItShouldAddOrders()  {
        OrderObj order1 = TestUtils.makeOrder(testUser1, 1, 1, "b");
        OrderObj order2 = TestUtils.makeOrder(testUser1, 1, 1, "s");

        List<AppUser> y = userService.getUsers();

        orderService.add(order1);
        orderService.add(order2);

        assertThat(orderService.get())
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(order1, order2));
    }

    @Test
    void ItShouldRemoveOrders() {
        OrderObj order1 = TestUtils.makeOrder(testUser1,1, 1, "b");
        OrderObj order2 = TestUtils.makeOrder(testUser1,1, 1 ,"s");
        OrderObj order3 = TestUtils.makeOrder(testUser1,1, 1 ,"s");

        orderService.add(order1);
        orderService.add(order2);
        orderService.add(order3);

        orderService.remove(order2);
        assertThat(orderService.get())
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(order1, order3));

        orderService.remove(order3);
        assertThat(orderService.get())
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(List.of(order1));

        orderService.remove(order1);
        assertThat(orderService.get())
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(List.of());
    }

    @Test
    void ItShouldProduceListOfEligibleBuyOrders() {
        OrderObj newOrder = TestUtils.makeOrder(testUser1, 5, 10, "s");
        OrderObj compatibleOrder = TestUtils.makeOrder(testUser2, 5, 10, "b");

        populateOrderService(Arrays.asList(
                TestUtils.makeOrder(testUser2, 4, 10 ,"b"), // incompatible price
                TestUtils.makeOrder(testUser2, 5, 10, "s"), // incompatible action
                TestUtils.makeOrder(testUser1, 6, 10, "b"), // incompatible account
                compatibleOrder
        ));

        assertThat(orderService.getEligibleOrders(newOrder))
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(List.of(compatibleOrder));
    }

    @Test
    void ItShouldProduceListOfEligibleSellOrders() {
        OrderObj newOrder = TestUtils.makeOrder(testUser1, 5, 10, "b");
        OrderObj compatibleOrder = TestUtils.makeOrder(testUser2, 5, 10, "s");

        populateOrderService(Arrays.asList(
                TestUtils.makeOrder(testUser2, 6, 10 ,"s"), // incompatible price
                TestUtils.makeOrder(testUser2, 5, 10, "b"), // incompatible action
                TestUtils.makeOrder(testUser1, 4, 10, "s"), // incompatible account
                compatibleOrder
        ));

        assertThat(orderService.getEligibleOrders(newOrder))
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(List.of(compatibleOrder));
    }

    public List<OrderObj> testOrderSet1(String primaryAction) {
        String secondaryAction = primaryAction.equals("b") ? "s" : "b";

        List<OrderObj> testOrders = new ArrayList<>();

        testOrders.add(TestUtils.makeOrder(testUser1, 20, 9, primaryAction));
        testOrders.add(TestUtils.makeOrder(testUser1, 10, 7, primaryAction));
        testOrders.add(TestUtils.makeOrder(testUser2, 20, 9, primaryAction));
        testOrders.add(TestUtils.makeOrder(testUser2, 10, 10, primaryAction));
        testOrders.add(TestUtils.makeOrder(testUser2, 30, 19, primaryAction));
        testOrders.add(TestUtils.makeOrder(testUser1, 40, 100, secondaryAction));

        return testOrders;
    }

    void populateOrderService(List<OrderObj> orders) {
        orders.forEach(order -> orderService.add(order));
    }

    @Test
    void ItShouldGenerateAnOrderbookWithBuyAction() {
        populateOrderService(testOrderSet1("b"));

        OrderbookItem obi1 = TestUtils.makeOrderbookItem(30, 19);
        OrderbookItem obi2 = TestUtils.makeOrderbookItem(20, 18);
        OrderbookItem obi3 = TestUtils.makeOrderbookItem(10, 17);
        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(obi1, obi2, obi3)
        );

        assertThat(orderService.getOrderbook(OrderAction.BUY))
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithSellAction() {
        populateOrderService(testOrderSet1("s"));

        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(
                        TestUtils.makeOrderbookItem(10, 17),
                        TestUtils.makeOrderbookItem(20, 18),
                        TestUtils.makeOrderbookItem(30, 19)
                ));

        assertThat(orderService.getOrderbook(OrderAction.SELL))
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateAnOrderbookWithBuyActionAndUsername() {
        populateOrderService(testOrderSet1("b"));

        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(
                        TestUtils.makeOrderbookItem(20, 9),
                        TestUtils.makeOrderbookItem(10, 7)
                ));

        assertThat(orderService.getOrderbook(OrderAction.BUY, testUser1.getUsername()))
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void ItShouldGenerateOrderDepthWithBuyAction() {
        populateOrderService(testOrderSet1("b"));

        ArrayList<OrderbookItem> expected = new ArrayList<>(
                Arrays.asList(
                        TestUtils.makeOrderbookItem(30, 19),
                        TestUtils.makeOrderbookItem(20, 37),
                        TestUtils.makeOrderbookItem(10, 54)
                ));

        assertThat(orderService.getOrderDepth(OrderAction.BUY))
                .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
