package com.example.demo;

import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.MatcherController;
import com.example.demo.matcher.models.*;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@WebMvcTest(controllers = MatcherController.class)
public class MatcherControllerTest {

    @Autowired
    private MockMvc mvc; // model view container, which handles the web stuff

    @MockBean
    private OrderService orderService;

    @MockBean
    private TradeService tradeService;

    @MockBean
    private Matcher matcher;

    @Test
    public void contextLoads() {
        assertThat(mvc).isNotNull();
    }

    static List<OrderbookItem> testOrderbook1() {
        return List.of(
                        TestUtils.makeOrderbookItem(1, 1),
                        TestUtils.makeOrderbookItem(2, 2)
        );
    }
    @Test
    void ItShouldReturnBuyOrderbook() throws Exception {

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get("/orderbook/buy"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldReturnSellOrderbook() throws Exception {
        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.SELL);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/sell"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldReturnBuyOrderbookWithAccountId() throws Exception {
        UUID accountUuid = TestUtils.uuidFromString("account1");

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY, accountUuid);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/buy/" + accountUuid))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldReturnSellOrderbookWithAccountId() throws Exception {
        UUID accountUuid = TestUtils.uuidFromString("account1");

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.SELL, accountUuid);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/sell/" + accountUuid))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldCheckBuyOrderbookWithBadAccountId() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/buy/" + "badAccountId"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("bad UUUID");

    }

    @Test
    void ItShouldNotifyBadAccountIdOnGetBuyAccount() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/buy/badId"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void ItShouldNotifyBadAccountIdOnGetSellAccount() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/sell/badId"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void ItShouldReturnBuyOrderdepth() throws Exception {
        doReturn(testOrderbook1()).when(orderService).getOrderDepth(OrderAction.BUY);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/depth/buy"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldReturnSellOrderdepth() throws Exception {
        doReturn(testOrderbook1()).when(orderService).getOrderDepth(OrderAction.SELL);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/orderbook/depth/sell"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }
    @Test
    void ItShouldReturnTradebook() throws Exception {
        List<Trade> testTradebook = TestUtils.makeRandomTradebook();
        doReturn(testTradebook).when(tradeService).getRecent();

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/tradebook/"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testTradebook));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldCreateAnOrder() throws Exception {
        Order newOrder = TestUtils.makeOrder(1 ,1, "b");

        NewOrderParams newOrderParams = new NewOrderParams(
                newOrder.getAccountId().toString(),
                newOrder.getPrice().doubleValue(),
                newOrder.getQuantity().doubleValue(),
                "buy"
        );

        // mock return values
        OrderbookItem expectedOrderbookItem = new OrderbookItem(newOrder.getPrice(), newOrder.getQuantity());
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderbook(OrderAction.BUY);
        doReturn(List.of()).when(orderService).getOrderbook(OrderAction.SELL);
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderbook(OrderAction.BUY, UUID.fromString(newOrderParams.getAccount()));
        doReturn(List.of()).when(orderService).getOrderbook(OrderAction.SELL, UUID.fromString(newOrderParams.getAccount()));
        doReturn(List.of()).when(tradeService).getRecent();
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderDepth(OrderAction.BUY);
        doReturn(List.of()).when(orderService).getOrderDepth(OrderAction.SELL);

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        // check result
        MakeOrderReturn expectedReturn = new MakeOrderReturn(
                List.of(expectedOrderbookItem),
                List.of(),
                List.of(expectedOrderbookItem),
                List.of(),
                List.of(),
                List.of(expectedOrderbookItem),
                List.of()
        );

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(expectedReturn));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldCheckNewOrderHasValidUUID() throws Exception {
        NewOrderParams newOrderParams = new NewOrderParams(
                "badId", 1, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("Bad UUID format");
    }

    @Test
    void ItShouldCheckPriceIsNotTooSmall() throws Exception {
        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), -1, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("price");
        assertThat(result.getResponse().getContentAsString()).contains("must be between");
    }

    @Test
    void ItShouldCheckPriceIsNotTooLarge() throws Exception {
        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1000000001, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("price");
        assertThat(result.getResponse().getContentAsString()).contains("must be between");
    }
    @Test
    void ItShouldCheckQuantityIsNotTooSmall() throws Exception {
        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1, -1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("quantity");
        assertThat(result.getResponse().getContentAsString()).contains("must be between");
    }

    @Test
    void ItShouldCheckQuantityIsNotTooLarge() throws Exception {
        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1, 1000000001, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("quantity");
        assertThat(result.getResponse().getContentAsString()).contains("must be between");
    }

    @Test
    void ItShouldCheckActionIsBuyOrSell() throws Exception {
        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1, 1, "badAction"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("should be buy or sell");
    }
}



