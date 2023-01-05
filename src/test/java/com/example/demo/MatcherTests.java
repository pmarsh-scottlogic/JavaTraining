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
}
