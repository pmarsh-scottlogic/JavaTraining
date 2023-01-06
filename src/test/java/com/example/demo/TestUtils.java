package com.example.demo;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.OrderbookItem;
import com.example.demo.matcher.models.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

public class TestUtils {
    public static Order makeOrder(float price, float quantity, String strAction) {
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(UUID.randomUUID(), new BigDecimal(price), new BigDecimal(quantity), action);
    }

    public static Order makeOrder(String strAccount, float price, float quantity, String strAction) {
        UUID uuid = uuidFromString(strAccount);
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(uuid, new BigDecimal(price), new BigDecimal(quantity), action);
    }

    public static Order makeOrder(float price, float quantity, String strAction, int datetimeRank) {
        OrderAction action = strAction.equals("b") ? OrderAction.BUY : OrderAction.SELL;
        return new Order(UUID.randomUUID(),
                new BigDecimal(price),
                new BigDecimal(quantity),
                action,
                LocalDateTime.of(2000, Month.JANUARY, 18, 0, 0).plusDays(datetimeRank));
    }

    public static UUID uuidFromString(String s) {
        return UUID.nameUUIDFromBytes(s.getBytes());
    }

    public static OrderbookItem makeOrderbookItem(float price, float quantity) {
        return new OrderbookItem(new BigDecimal(price), new BigDecimal(quantity));
    }

    public static Trade randomTrade() {
        return new Trade(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal(Math.random()),
                new BigDecimal(Math.random()),
                LocalDateTime.now());
    }
}
