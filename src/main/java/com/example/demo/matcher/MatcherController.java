package com.example.demo.matcher;

import com.example.demo.matcher.models.*;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MatcherController {
    @Autowired
    private final Matcher matcher;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final TradeService tradeService;

    @GetMapping(value = "/orderbook/buy")
    public List<OrderbookItem> orderbook_buy() {
        return orderService.getOrderbook(OrderAction.BUY);
    }

    @GetMapping(value = "/orderbook/buy/{accountId}")
    public List<OrderbookItem> orderbook_buy(@PathVariable String accountId) {
        return orderService.getOrderbook(OrderAction.BUY, UUID.fromString(accountId));
    }

    @GetMapping(value = "/orderbook/sell")
    public List<OrderbookItem> orderbook_sell() {
        return orderService.getOrderbook(OrderAction.SELL);
    }

    @GetMapping(value = "/orderbook/sell/{accountId}")
    public List<OrderbookItem> orderbook_sell(@PathVariable String accountId) {
        return orderService.getOrderbook(OrderAction.SELL, UUID.fromString(accountId));
    }

    @GetMapping(value = "/orderbook/depth/buy")
    public List<OrderbookItem> orderdepth_buy() {
        return orderService.getOrderDepth(OrderAction.BUY);
    }

    @GetMapping(value = "/orderbook/depth/sell")
    public List<OrderbookItem> orderdepth_sell() {
        return orderService.getOrderDepth(OrderAction.SELL);
    }

    @GetMapping(value = "/tradebook")
    public List<Trade> tradebook() {
        return tradeService.getRecent();
    }

    @PostMapping(value="/make/order")
    public MakeOrderReturn makeOrder(@RequestBody NewOrderParams newOrderParams) {
        Order newOrder = new Order(
                UUID.fromString(newOrderParams.getAccount()),
                BigDecimal.valueOf(newOrderParams.getPrice()),
                BigDecimal.valueOf(newOrderParams.getQuantity()),
                newOrderParams.getAction().equals("buy") ? OrderAction.BUY : OrderAction.SELL
                );

        matcher.match(newOrder);

        return new MakeOrderReturn(
                orderService.getOrderbook(OrderAction.BUY),
                orderService.getOrderbook(OrderAction.SELL),
                orderService.getOrderbook(OrderAction.BUY, UUID.fromString(newOrderParams.getAccount())),
                orderService.getOrderbook(OrderAction.SELL, UUID.fromString(newOrderParams.getAccount())),
                tradeService.getRecent(),
                orderService.getOrderDepth(OrderAction.BUY),
                orderService.getOrderDepth(OrderAction.SELL)
                );
    }
}
