package com.example.demo;

import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.*;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.authInfo.AuthRequest;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatcherControllerAndSecurityIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    private MockMvc mvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private TradeService tradeService;

    @MockBean
    private Matcher matcher;

    private AppUser testUser1;
    private AppUser testUser2;
    @BeforeEach
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();

        testUser1 = new AppUser(null, "testName", "testUsername", "testPassword", new ArrayList<>());
        testUser2 = new AppUser(null, "testName", "testUsername", "testPassword", new ArrayList<>());
        // these guys will need accountIds

        userService.saveUser(testUser1);
        userService.saveUser(testUser2);
    }

    // ========= Test that public endpoints are accessible and working =================================================

    static List<OrderbookItem> testOrderbook1() {
        return List.of(
                TestUtils.makeOrderbookItem(1, 1),
                TestUtils.makeOrderbookItem(2, 2)
        );
    }

    @Test
    public void itShouldAllowAccessToPublicBuyOrderbook() throws Exception {
        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/buy"))
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPublicSellOrderbook() throws Exception {
        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.SELL);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/sell"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPublicBuyOrderdepth() throws Exception {
        doReturn(testOrderbook1()).when(orderService).getOrderDepth(OrderAction.BUY);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/depth/buy"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPublicSellOrderdepth() throws Exception {
        doReturn(testOrderbook1()).when(orderService).getOrderDepth(OrderAction.SELL);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/depth/sell"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToTradebook() throws Exception {
        List<Trade> testTradebook = TestUtils.makeRandomTradebook();
        doReturn(testTradebook).when(tradeService).getRecent();

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/tradebook"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testTradebook));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    // ========= Test that private endpoints are inaccessible without a good token =====================================

    @Test
    public void itShouldNotAllowAccessToPrivateBuyOrdersWithoutAnyJWT() throws Exception {
        String testUser1Accountid = "GET THIS ONCE USERS HAVE ACCOUNTIDS";
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUser1Accountid))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldNotAllowAccessToPrivateSellOrdersWithoutAnyJWT() throws Exception {
        String testUser1Accountid = "GET THIS ONCE USERS HAVE ACCOUNTIDS";
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/Sell/" + testUser1Accountid))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void itShouldNotAllowCreationOfOrdersWithoutAnyJWT() throws Exception {
        Order newOrder = TestUtils.makeOrder(1 ,1, "b");

        NewOrderParams newOrderParams = new NewOrderParams(
                newOrder.getAccountId().toString(),
                newOrder.getPrice().doubleValue(),
                newOrder.getQuantity().doubleValue(),
                "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    // I may split the below three tests later in case we want to check for different responses depending on WHY the jwt is invalid
    // (badly formatted, expired, etc)

    @Test
    public void itShouldNotAllowAccessToPrivateBuyOrdersWithoutValidJWT() throws Exception {
        String testUser1Accountid = "GET THIS ONCE USERS HAVE ACCOUNTIDS";
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUser1Accountid)
                                .header("Authorization", "Bearer badtoken"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldNotAllowAccessToPrivateSellOrdersWithoutValidJWT() throws Exception {
        String testUser1Accountid = "GET THIS ONCE USERS HAVE ACCOUNTIDS";
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/sell/" + testUser1Accountid)
                                .header("Authorization", "Bearer badtoken"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void itShouldNotAllowCreationOfOrdersWithoutValidJWT() throws Exception {
        Order newOrder = TestUtils.makeOrder(1 ,1, "b");

        NewOrderParams newOrderParams = new NewOrderParams(
                newOrder.getAccountId().toString(),
                newOrder.getPrice().doubleValue(),
                newOrder.getQuantity().doubleValue(),
                "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", "Bearer badtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    // ======== Test logging in ========================================================================================

    @Test
    public void itShouldNotAllowLoginWithNoCredentials() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void itShouldNotAllowLoginWithBadCredentials() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest("badUsername", "badPassword"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldReturnTokenOnSuccessfulLogin() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest(testUser1.getUsername(), testUser1.getPassword()));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentAsString()).matches("[w-]*.[w-]*.[w-]*");
    }

    // ======= Tests once we have a valid JWT ==========================================================================

    @Test
    public void itShouldAllowAccessToPrivateBuyOrdersWithValidJWT() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context
        UUID testUserUUID1 = null; // get this once its implemented

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY, testUserUUID1);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUserUUID1)
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowPrivateToPrivateSellOrdersWithValidJWT() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context
        UUID testUserUUID1 = null; // get this once its implemented

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.SELL, testUserUUID1);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/sell/" + testUserUUID1)
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldNotAllowPrivateAccessToBuyOrdersWithValidJWTButWrongAccount() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context
        UUID testUserUUID1 = null; // get this once its implemented
        UUID testUserUUID2 = null;

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY, testUserUUID1);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUserUUID2)
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldNotAllowPrivateAccessToSellOrdersWithValidJWTButWrongAccount() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context
        UUID testUserUUID1 = null; // get this once its implemented
        UUID testUserUUID2 = null;

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY, testUserUUID1);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/sell/" + testUserUUID2)
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void ItShouldAllowCreatingAnOrderWithValidJWT() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

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
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
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

    // Test new order post request validation

    @Test
    void ItShouldCheckNewOrderHasValidUUID() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                "badId", 1, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("Bad UUID format");
    }

    @Test
    void ItShouldCheckPriceIsNotTooSmall() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), -1, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("price");
        assertThat(result.getResponse().getContentAsString()).contains("must be greater than or equal to");
    }

    @Test
    void ItShouldCheckPriceIsNotTooLarge() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1000000001, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("price");
        assertThat(result.getResponse().getContentAsString()).contains("must be less than or equal to");
    }

    @Test
    void ItShouldCheckQuantityIsNotTooSmall() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1, -1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("quantity");
        assertThat(result.getResponse().getContentAsString()).contains("must be greater than or equal to");
    }

    @Test
    void ItShouldCheckQuantityIsNotTooLarge() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1, 1000000001, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("quantity");
        assertThat(result.getResponse().getContentAsString()).contains("must be less than or equal to");
    }

    @Test
    void ItShouldCheckActionIsBuyOrSell() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                TestUtils.uuidFromString("account").toString(), 1, 1, "badAction"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("should be buy or sell");
    }
}
