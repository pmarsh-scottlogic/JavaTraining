package com.example.demo.matcher;

import com.example.demo.matcher.models.Order;
import com.example.demo.matcher.models.OrderAction;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public class InitialiseFakeMarket {
    public static Order fakeOrder() {
        Random rd = new Random();
        boolean bool = rd.nextBoolean();
        return new Order(
                UUID.randomUUID(),
                BigDecimal.valueOf(Math.random()),
                BigDecimal.valueOf(Math.random()),
                bool ? OrderAction.BUY : OrderAction.SELL
        );
    }

    public static void fillMatcher(Matcher matcher) {
        int n = 1000;
        for (int i = 0 ; i < n; i++) {
            matcher.match(fakeOrder());
        }
    }
}
