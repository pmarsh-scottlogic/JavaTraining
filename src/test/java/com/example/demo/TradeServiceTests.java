package com.example.demo;

import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class TradeServiceTests {
    TradeService tradeService;
    @BeforeEach
    void InitialiseTradeService() {
        tradeService = new TradeService();
    }

    @Test
    void ItShouldAddAddTrades() {
        Trade trade1 = TestUtils.randomTrade();
        Trade trade2 = TestUtils.randomTrade();

        tradeService.add(trade1);
        tradeService.add(trade2);

        assertThat(tradeService.getTrades()).isEqualTo(Arrays.asList(trade1, trade2));
    }
}
