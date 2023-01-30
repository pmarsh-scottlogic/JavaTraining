package com.example.demo.WebAndSecurity;

import com.example.demo.Matcher.TestUtils;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.*;
import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import org.junit.jupiter.api.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

// In this test suite we test what happens once the client has a successful JWT.
// So we could mock JwtTokenFilter and authorize all tokens.
// But in particular we need a token that contains a known username, so we can protect against accessing information
// of a user who is no the authenticated user.
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PrivateEndpointsWithTokenTest {
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

        testUser1 = new AppUser(null, "testName1", "testUsername1", "testPassword1", new ArrayList<>());
        testUser2 = new AppUser(null, "testName2", "testUsername2", "testPassword2", new ArrayList<>());

        userService.saveUser(testUser1);
        userService.saveUser(testUser2);
    }

    static List<OrderbookItem> testOrderbook1() {
        return List.of(
                TestUtils.makeOrderbookItem(1, 1),
                TestUtils.makeOrderbookItem(2, 2)
        );
    }

    @Test
    public void itShouldAllowAccessToPrivateBuyOrdersWithValidJWT() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY, testUser1.getUsername());

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUser1.getUsername())
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPrivateSellOrdersWithValidJWT() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.SELL, testUser1.getUsername());

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/sell/" + testUser1.getUsername())
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldNotAllowAccessToPrivateBuyOrdersWithValidJWTButWrongAccount() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY, testUser2.getUsername());

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUser2.getUsername())
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldNotAllowAccessToPrivateSellOrdersWithValidJWTButWrongAccount() throws Exception {
        // placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        doReturn(testOrderbook1()).when(orderService).getOrderbook(OrderAction.BUY, testUser2.getUsername());

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/sell/" + testUser1.getUsername())
                                .header("Authorization", validJWT))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void ItShouldAllowCreatingAnOrderWithValidJWT() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        OrderObj newOrder = TestUtils.makeOrder(testUser1.getUsername(), 1 ,1, "b");

        NewOrderParams newOrderParams = new NewOrderParams(
                newOrder.getUser().getUsername(),
                newOrder.getPrice().doubleValue(),
                newOrder.getQuantity().doubleValue(),
                "buy"
        );

        // mock return values
        OrderbookItem expectedOrderbookItem = new OrderbookItem(newOrder.getPrice(), newOrder.getQuantity());
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderbook(OrderAction.BUY);
        doReturn(List.of()).when(orderService).getOrderbook(OrderAction.SELL);
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderbook(OrderAction.BUY, newOrderParams.getUsername());
        doReturn(List.of()).when(orderService).getOrderbook(OrderAction.SELL, newOrderParams.getUsername());
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
    void ItShouldCheckNewOrderHasSameUsernameAsJWT() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                "anotherUsername", 1, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", validJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
