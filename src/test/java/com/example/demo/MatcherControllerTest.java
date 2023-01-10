package com.example.demo;

import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.MatcherController;
import com.example.demo.matcher.models.NewOrderParams;
import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
    }

    @Test
    void ItShouldReturnReturnEmptyListAtFirstRequest() throws Exception {
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get("/orderbook/buy"))
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

    }

    @Test
    void ItShouldCreateAnOrder() throws Exception {
        NewOrderParams newOrder = new NewOrderParams(
                UUID.randomUUID().toString(),
                1,
                1,
                "buy"
        );

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/make/order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(newOrder)))
                .andReturn();
        //assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}



