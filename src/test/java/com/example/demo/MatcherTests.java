package com.example.demo;

import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
public class MatcherTests {
    Matcher matcher;
    OrderService orderService;
    TradeService tradeService;

    @BeforeEach
    void initialise() {
        orderService = Mockito.mock(OrderService.class);
        tradeService = Mockito.mock(TradeService.class);
        matcher = new Matcher(orderService, tradeService);
    }

    @Test
    void ItShouldAddAnOrderIfThereAreNoExistingOrders() {
        Order newOrder = new Order("account1", 1, 1, OrderAction.BUY);
        matcher.match(newOrder);
        Mockito.verify(orderService).add(newOrder);
    }

    @Test
    void ItShouldAddAnOrderIfThereAreNoEligibleOrders() {
        Order newOrder = new Order("account3", 5, 10, OrderAction.SELL);

        Order order1 = new Order("account1", 4, 10, OrderAction.BUY); // incompatable price
        Order order2 = new Order("account2", 6, 10, OrderAction.SELL); // incompatable action
        Order order3 = new Order("account3", 6, 10, OrderAction.BUY); // incompatable account

        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                Arrays.asList(order1, order2, order3)
        ));

        matcher.match(newOrder);

        Mockito.verify(orderService).add(newOrder);
    }
}
