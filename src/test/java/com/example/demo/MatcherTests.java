package com.example.demo;

import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void ItShouldMatchABasicSellOrder() {


        // setup mock returns
        Order existingOrder = new Order("account1", 4, 10, OrderAction.BUY);
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(List.of(existingOrder)));

        // run match
        Order newOrder = new Order("account2", 4, 10, OrderAction.SELL);
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(4);
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(10);
        assertThat(createdTrade.getValue().getAccountIdBuyer()).isEqualTo("account1");
        assertThat(createdTrade.getValue().getAccountIdSeller()).isEqualTo("account2");
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getId());
    }

    @Test
    void ItShouldAddToTheOrderbookWhenThereIsExcessQuantity() {
        // setup mock returns
        Order existingOrder = new Order("account1", 4, 10, OrderAction.BUY);
        Mockito.when(orderService.get())
                .thenReturn(new ArrayList<>(List.of(existingOrder)))
                .thenReturn(new ArrayList<>(List.of()));

        // run match
        Order newOrder = new Order("account2", 4, 12, OrderAction.SELL);
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(4);
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(10);
        assertThat(createdTrade.getValue().getAccountIdBuyer()).isEqualTo("account1");
        assertThat(createdTrade.getValue().getAccountIdSeller()).isEqualTo("account2");
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getId());

        // capture the created order object
        ArgumentCaptor<Order> createdOrder = ArgumentCaptor.forClass(Order.class);
        Mockito.verify(orderService).add(createdOrder.capture());

        // check properties of created order object that was added to orderService
        assertThat(createdOrder.getValue().getId()).isEqualTo(newOrder.getId());
        assertThat(createdOrder.getValue().getAccountId()).isEqualTo("account2");
        assertThat(createdOrder.getValue().getPrice()).isEqualTo(4);
        assertThat(createdOrder.getValue().getQuantity()).isEqualTo(2);
        assertThat(createdOrder.getValue().getDatetime()).isEqualTo(newOrder.getDatetime());
        assertThat(createdOrder.getValue().getAction()).isEqualTo(OrderAction.SELL);
    }

    @Test
    void ItShouldUpdateTheQuantityOfMatchedExistingOrderWhenThereIsExcess() {
        // setup mock returns
        Order existingOrder = new Order("account1", 4, 10, OrderAction.BUY);
        Mockito.when(orderService.get())
                .thenReturn(new ArrayList<>(List.of(existingOrder)))
                .thenReturn(new ArrayList<>(List.of()));

        // run match
        Order newOrder = new Order("account2", 4, 8, OrderAction.SELL);
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(4);
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(8);
        assertThat(createdTrade.getValue().getAccountIdBuyer()).isEqualTo("account1");
        assertThat(createdTrade.getValue().getAccountIdSeller()).isEqualTo("account2");
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getId());

        // check quantity of existing order was updated
        assertThat(existingOrder.getQuantity()).isEqualTo(2);
        assertThat(newOrder.getQuantity()).isEqualTo(0);
    }
}
