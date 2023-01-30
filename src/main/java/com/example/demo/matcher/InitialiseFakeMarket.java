package com.example.demo.matcher;

import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class InitialiseFakeMarket {
    @Autowired
    private UserService userService;
    private List<AppUser> users = new ArrayList<>();
    private static final  int NUM_OF_ORDERS_TO_MATCH = 1000;

    public OrderObj fakeOrder() {
        Random rd = new Random();
        boolean bool = rd.nextBoolean();
        return new OrderObj(
                users.get((int) Math.floor(Math.random() * users.size())),
                BigDecimal.valueOf(Math.random()),
                BigDecimal.valueOf(Math.random()),
                bool ? OrderAction.BUY : OrderAction.SELL
        );
    }

    public void fillMatcher(Matcher matcher) {
        getUsers();
        for (int i = 0 ; i < NUM_OF_ORDERS_TO_MATCH; i++) {
            matcher.match(fakeOrder());
        }
    }

    public void getUsers() {
        users = userService.getUsers();
    }
}
