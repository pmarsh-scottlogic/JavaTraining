package com.example.demo.matcher;

import com.example.demo.matcher.models.*;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<OrderbookItem>> orderbook_sell() {
        return new ResponseEntity<>(orderService.getOrderbook(OrderAction.SELL), HttpStatus.OK);
    }

    @GetMapping(value = "/orderbook/sell/{accountId}")
    public ResponseEntity<List<OrderbookItem>> orderbook_sell(@PathVariable String accountId) {
        return new ResponseEntity<>(orderService.getOrderbook(OrderAction.SELL, UUID.fromString(accountId)), HttpStatus.OK);
    }

    @GetMapping(value = "/orderbook/depth/buy")
    public ResponseEntity<List<OrderbookItem>> orderdepth_buy() {
        return new ResponseEntity<>(orderService.getOrderDepth(OrderAction.BUY), HttpStatus.OK);
    }

    @GetMapping(value = "/orderbook/depth/sell")
    public ResponseEntity<List<OrderbookItem>> orderdepth_sell() {
        return new ResponseEntity<>(orderService.getOrderDepth(OrderAction.SELL), HttpStatus.OK);
    }

    @GetMapping(value = "/tradebook")
    public ResponseEntity<List<Trade>> tradebook() {
        return new ResponseEntity<>(tradeService.getRecent(), HttpStatus.OK);
    }

    @PostMapping(value="/make/order")
    public ResponseEntity<MakeOrderReturn> makeOrder(@RequestBody NewOrderParams newOrderParams) {
        Order newOrder = new Order(
                UUID.fromString(newOrderParams.getAccount()),
                BigDecimal.valueOf(newOrderParams.getPrice()),
                BigDecimal.valueOf(newOrderParams.getQuantity()),
                newOrderParams.getAction().equals("buy") ? OrderAction.BUY : OrderAction.SELL
                );

        matcher.match(newOrder);

        return new ResponseEntity<>(new MakeOrderReturn(
                orderService.getOrderbook(OrderAction.BUY),
                orderService.getOrderbook(OrderAction.SELL),
                orderService.getOrderbook(OrderAction.BUY, UUID.fromString(newOrderParams.getAccount())),
                orderService.getOrderbook(OrderAction.SELL, UUID.fromString(newOrderParams.getAccount())),
                tradeService.getRecent(),
                orderService.getOrderDepth(OrderAction.BUY),
                orderService.getOrderDepth(OrderAction.SELL)
                ), HttpStatus.OK);
    }
}
