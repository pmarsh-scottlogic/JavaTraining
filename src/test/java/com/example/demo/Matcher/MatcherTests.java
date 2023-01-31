package com.example.demo.Matcher;

import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.repo.OrderRepo;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.userInfo.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("uninitialised")
@RunWith(SpringRunner.class)
@SpringBootTest
public class MatcherTests {
    @Autowired
    Matcher matcher;
    @MockBean
    OrderService orderService;
    @MockBean
    TradeService tradeService;

    @MockBean
    OrderRepo orderRepo;

    @BeforeEach
    void initialise() {

    }

    @Test
    void ItShouldAddAnOrderIfThereAreNoEligibleOrders() {

        OrderObj newOrder = TestUtils.makeOrder(5, 10, "s");

        Mockito.when(orderService.getEligibleOrders(newOrder)).thenReturn(List.of());

        matcher.match(newOrder);

        Mockito.verify(orderService).add(newOrder);
    }

    @Test
    void ItShouldMatchABasicSellOrder() {
        OrderObj newOrder = TestUtils.makeOrder(4, 10, "s");
        OrderObj existingOrder = TestUtils.makeOrder(4, 10, "b");

        // setup mock returns
        Mockito.when(orderService.getEligibleOrders(newOrder)).thenReturn(List.of(existingOrder));

        // run match
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(new BigDecimal(10));
        assertThat(createdTrade.getValue().getUsernameBuyer()).isEqualTo(existingOrder.getUser().getUsername());
        assertThat(createdTrade.getValue().getUsernameSeller()).isEqualTo(newOrder.getUser().getUsername());
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getOrderId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getOrderId());
    }

    @Test
    void ItShouldAddToTheOrderbookWhenThereIsExcessQuantity() {
        OrderObj newOrder = TestUtils.makeOrder(4, 12, "s");
        OrderObj existingOrder = TestUtils.makeOrder(4, 10, "b");

        // setup mock returns
        Mockito.when(orderService.getEligibleOrders(newOrder))
                .thenReturn(new ArrayList<>(List.of(existingOrder)))
                .thenReturn(new ArrayList<>(List.of()));

        // run match
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(new BigDecimal(10));
        assertThat(createdTrade.getValue().getUsernameBuyer()).isEqualTo(existingOrder.getUser().getUsername());
        assertThat(createdTrade.getValue().getUsernameSeller()).isEqualTo(newOrder.getUser().getUsername());
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getOrderId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getOrderId());

        // capture the created order object
        ArgumentCaptor<OrderObj> createdOrder = ArgumentCaptor.forClass(OrderObj.class);
        Mockito.verify(orderService).add(createdOrder.capture());

        // check properties of created order object that was added to orderService
        assertThat(createdOrder.getValue().getOrderId()).isEqualTo(newOrder.getOrderId());
        assertThat(createdOrder.getValue().getUser().getUsername()).isEqualTo(newOrder.getUser().getUsername());
        assertThat(createdOrder.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdOrder.getValue().getQuantity()).isEqualTo(new BigDecimal(2));
        assertThat(createdOrder.getValue().getDatetime()).isEqualTo(newOrder.getDatetime());
        assertThat(createdOrder.getValue().getAction()).isEqualTo(OrderAction.SELL);
    }

    @Test
    void ItShouldUpdateTheQuantityOfMatchedExistingOrderWhenThereIsExcess() {
        OrderObj newOrder = TestUtils.makeOrder(4, 8, "s");
        OrderObj existingOrder = TestUtils.makeOrder(4, 10 ,"b");

        // setup mock returns
        Mockito.when(orderService.getEligibleOrders(newOrder))
                .thenReturn(new ArrayList<>(List.of(existingOrder)))
                .thenReturn(new ArrayList<>(List.of()));

        // run match
        matcher.match(newOrder);

        // capture the created trade object
        ArgumentCaptor<Trade> createdTrade = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeService).add(createdTrade.capture());

        // check properties of created trade object that was added to tradeService
        assertThat(createdTrade.getValue().getPrice()).isEqualTo(new BigDecimal(4));
        assertThat(createdTrade.getValue().getQuantity()).isEqualTo(new BigDecimal(8));
        assertThat(createdTrade.getValue().getUsernameBuyer()).isEqualTo(existingOrder.getUser().getUsername());
        assertThat(createdTrade.getValue().getUsernameSeller()).isEqualTo(newOrder.getUser().getUsername());
        assertThat(createdTrade.getValue().getOrderIdBuy()).isEqualTo(existingOrder.getOrderId());
        assertThat(createdTrade.getValue().getOrderIdSell()).isEqualTo(newOrder.getOrderId());

        // check quantity of existing order was updated
        assertThat(existingOrder.getQuantity()).isEqualTo(new BigDecimal(2));
        assertThat(newOrder.getQuantity()).isEqualTo(new BigDecimal(0));
    }
}
