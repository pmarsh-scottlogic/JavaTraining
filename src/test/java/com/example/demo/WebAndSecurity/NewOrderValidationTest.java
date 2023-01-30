package com.example.demo.WebAndSecurity;

import com.example.demo.Matcher.TestUtils;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.NewOrderParams;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.service.UserService;
import com.example.demo.security.token.JwtTokenUtil;
import com.example.demo.security.userInfo.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

// ideally we'd not run the whole app, and only have the matchercontroller, without even the security layer.
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NewOrderValidationTest {

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

    @BeforeEach
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
    }
    @Test
    void ItShouldCheckNewOrderPriceIsNotTooSmall() throws Exception {
        Mockito.when(jwtTokenUtil.validateAccessToken(FAKE_JWT)).thenReturn(true);

        NewOrderParams newOrderParams = new NewOrderParams(
                "fakeUsername", -1, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer: " + FAKE_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("price");
        assertThat(result.getResponse().getContentAsString()).contains("must be greater than or equal to");
    }

    @Test
    void ItShouldCheckNewOrderPriceIsNotTooLarge() throws Exception {
        Mockito.when(jwtTokenUtil.validateAccessToken(FAKE_JWT)).thenReturn(true);

        NewOrderParams newOrderParams = new NewOrderParams(
                "fakeUsername", 1000000001, 1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", "Bearer: " + FAKE_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("price");
        assertThat(result.getResponse().getContentAsString()).contains("must be less than or equal to");
    }

    @Test
    void ItShouldCheckNewOrderQuantityIsNotTooSmall() throws Exception {
        Mockito.when(jwtTokenUtil.validateAccessToken(FAKE_JWT)).thenReturn(true);

        NewOrderParams newOrderParams = new NewOrderParams(
                "fakeUsername", 1, -1, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", "Bearer: " + FAKE_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("quantity");
        assertThat(result.getResponse().getContentAsString()).contains("must be greater than or equal to");
    }

    @Test
    void ItShouldCheckNewOrderQuantityIsNotTooLarge() throws Exception {
        Mockito.when(jwtTokenUtil.validateAccessToken(FAKE_JWT)).thenReturn(true);

        NewOrderParams newOrderParams = new NewOrderParams(
                "fakeUsername", 1, 1000000001, "buy"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", "Bearer: " + FAKE_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("quantity");
        assertThat(result.getResponse().getContentAsString()).contains("must be less than or equal to");
    }

    @Test
    void ItShouldCheckNewOrderActionIsBuyOrSell() throws Exception {
        Mockito.when(jwtTokenUtil.validateAccessToken(FAKE_JWT)).thenReturn(true);

        NewOrderParams newOrderParams = new NewOrderParams(
                "fakeUsername", 1, 1, "badAction"
        );

        // API call
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/private/make/order")
                        .header("Authorization", "Bearer: " + FAKE_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(newOrderParams)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("should be buy or sell");
    }
}
