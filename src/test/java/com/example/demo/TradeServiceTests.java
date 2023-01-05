package com.example.demo;

import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@SpringBootTest
public class TradeServiceTests {
    TradeService tradeService;
    @BeforeEach
    void InitialiseTradeService() {
        tradeService = new TradeService();
    }

    @Test
    void ItShouldAddAddTrades() {
        Trade trade1 = new Trade("account1", "order1", "account2", "order2", 1, 1, LocalDateTime.now());
        Trade trade2 = new Trade("account2", "order2", "account3", "order3", 1, 1, LocalDateTime.now());

        tradeService.add(trade1);
        tradeService.add(trade2);

        assertThat(tradeService.get()).isEqualTo(Arrays.asList(trade1, trade2));
    }
}
