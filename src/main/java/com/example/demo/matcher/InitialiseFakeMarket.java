package com.example.demo.matcher;

import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public class InitialiseFakeMarket {
    public static OrderObj fakeOrder() {
        Random rd = new Random();
        boolean bool = rd.nextBoolean();
        return new OrderObj(
                UUID.randomUUID().toString(), // These should be usernames, but usernames should be unique, so we're using uuids as usernames
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
