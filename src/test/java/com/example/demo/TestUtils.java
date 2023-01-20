package com.example.demo;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.models.Trade;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TestUtils {
    public static Order makeOrder(float price, float quantity, String strAction) {
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(UUID.randomUUID().toString(), // unique username
                new BigDecimal(price), new BigDecimal(quantity), action);
    }

    public static Order makeOrder(String username, float price, float quantity, String strAction) {
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(username, new BigDecimal(price), new BigDecimal(quantity), action);
    }

    public static Order makeOrder(float price, float quantity, String strAction, int datetimeRank) {
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(UUID.randomUUID().toString(), // unique username
                new BigDecimal(price),
                new BigDecimal(quantity),
                action,
                (long) datetimeRank);
    }

    public static UUID uuidFromString(String s) {
        return UUID.nameUUIDFromBytes(s.getBytes());
    }

    public static OrderbookItem makeOrderbookItem(float price, float quantity) {
        return new OrderbookItem(new BigDecimal(price), new BigDecimal(quantity));
    }

    public static Trade randomTrade() {
        return new Trade(
                UUID.randomUUID().toString(), // unique username
                UUID.randomUUID(),
                UUID.randomUUID().toString(), // unique username
                UUID.randomUUID(),
                BigDecimal.valueOf(Math.random()),
                BigDecimal.valueOf(Math.random()),
                Math.round(Math.random() * 100));
    }

    static List<Trade> makeRandomTradebook() {
        return List.of(
                TestUtils.randomTrade(),
                TestUtils.randomTrade()
        );
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
