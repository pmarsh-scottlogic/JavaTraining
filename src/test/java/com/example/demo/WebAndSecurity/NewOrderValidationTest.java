package com.example.demo.WebAndSecurity;

import com.example.demo.Matcher.TestUtils;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.NewOrderParams;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    private AppUser testUser1;

    @BeforeEach
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();

        testUser1 = new AppUser(null, "testName1", "testUsername1", "testPassword1", new ArrayList<>());

        userService.saveUser(testUser1);
    }
    @Test
    void ItShouldCheckNewOrderPriceIsNotTooSmall() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                testUser1.getUsername(), -1, 1, "buy"
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
    void ItShouldCheckNewOrderPriceIsNotTooLarge() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                testUser1.getUsername(), 1000000001, 1, "buy"
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
    void ItShouldCheckNewOrderQuantityIsNotTooSmall() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                testUser1.getUsername(), 1, -1, "buy"
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
    void ItShouldCheckNewOrderQuantityIsNotTooLarge() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                testUser1.getUsername(), 1, 1000000001, "buy"
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
    void ItShouldCheckNewOrderActionIsBuyOrSell() throws Exception {
        //placeholder
        String validJWT = "validJWT"; // todo: figure out how to pass a valid jwt in test context

        NewOrderParams newOrderParams = new NewOrderParams(
                testUser1.getUsername(), 1, 1, "badAction"
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
