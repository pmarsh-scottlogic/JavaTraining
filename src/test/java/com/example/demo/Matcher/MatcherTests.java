package com.example.demo.Matcher;

import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        OrderObj newOrder = TestUtils.makeOrder(1, 1, "b");
        matcher.match(newOrder);
        Mockito.verify(orderService).add(newOrder);
    }

    @Test
    void ItShouldAddAnOrderIfThereAreNoEligibleOrders() {
        OrderObj newOrder = TestUtils.makeOrder("account1", 5, 10, "s");

        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(
                Arrays.asList(
                        TestUtils.makeOrder(4, 10 ,"b"), // incompatible price
                        TestUtils.makeOrder(6, 10, "s"), // incompatible action
                        TestUtils.makeOrder("account1", 6, 10, "b") // incompatible account
                )));

        matcher.match(newOrder);

        Mockito.verify(orderService).add(newOrder);
    }

    @Test
    void ItShouldMatchABasicSellOrder() {
        // setup mock returns
        OrderObj existingOrder = TestUtils.makeOrder(4, 10, "b");
        Mockito.when(orderService.get()).thenReturn(new ArrayList<>(List.of(existingOrder)));

        // run match
        OrderObj newOrder = TestUtils.makeOrder(4, 10, "s");
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(new BigDecimal(10));
        assertThat(createdTrade.getValue().getUsernameBuyer()).isEqualTo(existingOrder.getUsername());
        assertThat(createdTrade.getValue().getUsernameSeller()).isEqualTo(newOrder.getUsername());
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getOrderId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getOrderId());
    }

    @Test
    void ItShouldAddToTheOrderbookWhenThereIsExcessQuantity() {
        // setup mock returns
        OrderObj existingOrder = TestUtils.makeOrder(4, 10, "b");
        Mockito.when(orderService.get())
                .thenReturn(new ArrayList<>(List.of(existingOrder)))
                .thenReturn(new ArrayList<>(List.of()));

        // run match
        OrderObj newOrder = TestUtils.makeOrder(4, 12, "s");
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(new BigDecimal(10));
        assertThat(createdTrade.getValue().getUsernameBuyer()).isEqualTo(existingOrder.getUsername());
        assertThat(createdTrade.getValue().getUsernameSeller()).isEqualTo(newOrder.getUsername());
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getOrderId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getOrderId());

        // capture the created order object
        ArgumentCaptor<OrderObj> createdOrder = ArgumentCaptor.forClass(OrderObj.class);
        Mockito.verify(orderService).add(createdOrder.capture());

        // check properties of created order object that was added to orderService
        assertThat(createdOrder.getValue().getOrderId()).isEqualTo(newOrder.getOrderId());
        assertThat(createdOrder.getValue().getUsername()).isEqualTo(newOrder.getUsername());
        assertThat(createdOrder.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdOrder.getValue().getQuantity()).isEqualTo(new BigDecimal(2));
        assertThat(createdOrder.getValue().getDatetime()).isEqualTo(newOrder.getDatetime());
        assertThat(createdOrder.getValue().getAction()).isEqualTo(OrderAction.SELL);
    }

    @Test
    void ItShouldUpdateTheQuantityOfMatchedExistingOrderWhenThereIsExcess() {
        // setup mock returns
        OrderObj existingOrder = TestUtils.makeOrder(4, 10 ,"b");
        Mockito.when(orderService.get())
                .thenReturn(new ArrayList<>(List.of(existingOrder)))
                .thenReturn(new ArrayList<>(List.of()));

        // run match
        OrderObj newOrder = TestUtils.makeOrder(4, 8, "s");
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(new BigDecimal(8));
        assertThat(createdTrade.getValue().getUsernameBuyer()).isEqualTo(existingOrder.getUsername());
        assertThat(createdTrade.getValue().getUsernameSeller()).isEqualTo(newOrder.getUsername());
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getOrderId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getOrderId());

        // check quantity of existing order was updated
        assertThat(existingOrder.getQuantity()).isEqualTo(new BigDecimal(2));
        assertThat(newOrder.getQuantity()).isEqualTo(new BigDecimal(0));
    }
}
