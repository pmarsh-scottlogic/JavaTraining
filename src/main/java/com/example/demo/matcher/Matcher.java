package com.example.demo.matcher;

import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.models.Trade;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service @AllArgsConstructor
public class Matcher {
    @Autowired private final OrderService orderService;
    @Autowired private final TradeService tradeService;

    public void match(OrderObj newOrder) {
        OrderObj matchedOrder;

        do {
            matchedOrder = getMatchingOrder(newOrder);
            if (matchedOrder == null) break;

            OrderObj buyOrder = newOrder.getAction() == OrderAction.BUY? newOrder : matchedOrder;
            OrderObj sellOrder = newOrder.getAction() == OrderAction.SELL? newOrder : matchedOrder;

            BigDecimal tradePrice = matchedOrder.getPrice();
            BigDecimal tradeQuantity = newOrder.getQuantity().min(matchedOrder.getQuantity());

            tradeService.add(new Trade(buyOrder.getUser().getUsername(),
                    buyOrder.getOrderId(),
                    sellOrder.getUser().getUsername(),
                    sellOrder.getOrderId(),
                    tradePrice,
                    tradeQuantity,
                    Instant.now().toEpochMilli()));

            newOrder.setQuantity(newOrder.getQuantity().subtract(tradeQuantity));
            matchedOrder.setQuantity(matchedOrder.getQuantity().subtract(tradeQuantity));

            if (matchedOrder.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                orderService.remove(matchedOrder);
            }
        } while(newOrder.getQuantity().compareTo(BigDecimal.ZERO) > 0);

        if (newOrder.getQuantity().compareTo(BigDecimal.ZERO) > 0) orderService.add(newOrder);
    }

    private OrderObj getMatchingOrder(OrderObj newOrder) {
        // match the new order to the best order of opposite action (sorted by price and then time)
        List<OrderObj> eligibleOrders = orderService.getEligibleOrders(newOrder);
        return eligibleOrders.size() == 0? null : eligibleOrders.get(0);
    }
}
