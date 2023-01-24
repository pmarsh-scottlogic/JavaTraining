package com.example.demo.matcher;

import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class InitialiseFakeMarket {
    @Autowired
    private UserService userService;
    private ArrayList<String> usernames = new ArrayList<>();
    public OrderObj fakeOrder() {
        Random rd = new Random();
        boolean bool = rd.nextBoolean();
        return new OrderObj(
                usernames.get((int) Math.floor(Math.random() * usernames.size())),
                BigDecimal.valueOf(Math.random()),
                BigDecimal.valueOf(Math.random()),
                bool ? OrderAction.BUY : OrderAction.SELL
        );
    }

    public void fillMatcher(Matcher matcher) {
        getUsernames();
        int n = 1000;
        for (int i = 0 ; i < n; i++) {
            matcher.match(fakeOrder());
        }
    }

    public void getUsernames() {
        usernames = new ArrayList<>(userService.getUsers().stream().map(AppUser::getUsername).collect(Collectors.toList()));
    }
}
