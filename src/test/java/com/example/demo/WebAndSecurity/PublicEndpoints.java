package com.example.demo.WebAndSecurity;

import com.example.demo.Matcher.TestUtils;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.MatcherController;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.configuration.ApplicationSecurity;
import com.example.demo.security.filter.JwtTokenFilter;
import com.example.demo.security.repo.UserRepo;
import com.example.demo.security.service.UserService;
import com.example.demo.security.token.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ContextConfiguration(classes = { ApplicationSecurity.class, JwtTokenFilter.class, JwtTokenUtil.class, UserService.class })
@WebMvcTest(controllers = MatcherController.class)
public class PublicEndpoints {

    @Autowired
    private MockMvc mvc; // model view container, which handles the web stuff

    @MockBean
    private OrderService orderService;

    @MockBean
    private TradeService tradeService;

    @MockBean
    private Matcher matcher;

    @MockBean
    private UserRepo userRepo;

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
// yes
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
}
