package com.example.demo.WebAndSecurity;

import com.example.demo.Matcher.TestUtils;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.*;
import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.service.UserService;
import com.example.demo.security.token.JwtTokenUtil;
import com.example.demo.security.userInfo.AppUser;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private static final String FAKE_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1LEphbWVzIiwiaXNzIjoiTWF0Y2hlcl9CYWNrZW5kIiwiZXhwIjoxNjc1MDc3MzcwfQ.ZDaSHkgobhAYaTwRDcurPY3VS-lhvM4V5Ya7oPHcOi0";

    private AppUser testUser1;
    private AppUser testUser2;

    @BeforeEach
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        List<AppUser> y = userService.getUsers();
        List<String> z = y.stream().map(a -> a.getUsername()).collect(Collectors.toList());

        testUser1 = new AppUser(null, "testName1", "testUsername1", "testPassword1", new ArrayList<>());
        testUser2 = new AppUser(null, "testName2", "testUsername2", "testPassword2", new ArrayList<>());
        userService.saveUser(testUser1);
        userService.saveUser(testUser2);

        // mock security
        Mockito.when(jwtTokenUtil.validateAccessToken(FAKE_JWT)).thenReturn(true);
        Mockito.when(jwtTokenUtil.getSubject(FAKE_JWT)).thenReturn("0," + testUser1.getUsername());
    }

    @AfterEach
    public void cleanup() {
        userService.deleteAll();
    }

    static List<OrderbookItem> testOrderbook1() {
        return List.of(
                TestUtils.makeOrderbookItem(1, 1),
                TestUtils.makeOrderbookItem(2, 2)
        );
    }

    @Test
    public void itShouldAllowAccessToPrivateBuyOrdersWithValidJWT() throws Exception {
        // mock orderService
        doReturn(testOrderbook1()).when(orderService).getOrderbook(eq(OrderAction.BUY), anyString());

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/")
                                .header("Authorization", "Bearer: " + FAKE_JWT))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPrivateSellOrdersWithValidJWT() throws Exception {
        // mock orderService
        doReturn(testOrderbook1()).when(orderService).getOrderbook(eq(OrderAction.SELL), anyString());

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/sell/" + testUser1.getUsername())
                                .header("Authorization", "Bearer: " + FAKE_JWT))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(testOrderbook1()));
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void ItShouldAllowCreatingAnOrderWithValidJWT() throws Exception {
        OrderObj newOrder = TestUtils.makeOrder(testUser1, 1 ,1, "b");



        NewOrderParams newOrderParams = new NewOrderParams(
                newOrder.getPrice().doubleValue(),
                newOrder.getQuantity().doubleValue(),
                "buy"
        );

        // mock return values
        OrderbookItem expectedOrderbookItem = new OrderbookItem(newOrder.getPrice(), newOrder.getQuantity());
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderbook(OrderAction.BUY);
        doReturn(List.of()).when(orderService).getOrderbook(OrderAction.SELL);
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderbook(eq(OrderAction.BUY), anyString());
        doReturn(List.of()).when(orderService).getOrderbook(eq(OrderAction.SELL), anyString());
        doReturn(List.of()).when(tradeService).getRecent();
        doReturn(List.of(expectedOrderbookItem)).when(orderService).getOrderDepth(OrderAction.BUY);
        doReturn(List.of()).when(orderService).getOrderDepth(OrderAction.SELL);


        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", "Bearer: " + FAKE_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        // check result
        NewOrderReturn expectedReturn = new NewOrderReturn(
                List.of(expectedOrderbookItem),
                List.of(),
                List.of(expectedOrderbookItem),
                List.of(),
                List.of(),
                List.of(expectedOrderbookItem),
                List.of()
        );

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentAsString()).isEqualTo(TestUtils.asJsonString(expectedReturn));
    }
}
