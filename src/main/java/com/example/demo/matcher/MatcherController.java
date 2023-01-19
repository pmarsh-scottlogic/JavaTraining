package com.example.demo.matcher;

import com.example.demo.matcher.models.*;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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

    @GetMapping(value = "/public/orderbook/buy")
    public ResponseEntity<List<OrderbookItem>> orderbook_buy() {
        return ResponseEntity.ok(orderService.getOrderbook(OrderAction.BUY));
    }

    @GetMapping(value = "/private/orderbook/buy/{accountId}")
    public ResponseEntity<List<OrderbookItem>> orderbook_buy(@PathVariable String accountId) {
        try {
            UUID accountUuid = UUID.fromString(accountId);
            return ResponseEntity.ok(orderService.getOrderbook(OrderAction.BUY, UUID.fromString(accountId)));
        }
        catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/public/orderbook/sell")
    public ResponseEntity<List<OrderbookItem>> orderbook_sell() {
        return ResponseEntity.ok(orderService.getOrderbook(OrderAction.SELL));
    }

    @GetMapping(value = "/private/orderbook/sell/{accountId}")
    public ResponseEntity<List<OrderbookItem>> orderbook_sell(@PathVariable String accountId) {
        try {
            UUID accountUuid = UUID.fromString(accountId);
            return ResponseEntity.ok(orderService.getOrderbook(OrderAction.SELL, accountUuid));
        }
        catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/public/orderbook/depth/buy")
    public ResponseEntity<List<OrderbookItem>> orderdepth_buy() {
        return ResponseEntity.ok(orderService.getOrderDepth(OrderAction.BUY));
    }

    @GetMapping(value = "/public/orderbook/depth/sell")
    public ResponseEntity<List<OrderbookItem>> orderdepth_sell() {
        return ResponseEntity.ok(orderService.getOrderDepth(OrderAction.SELL));
    }

    @GetMapping(value = "/public/tradebook")
    public ResponseEntity<List<Trade>> tradebook() {
        return ResponseEntity.ok(tradeService.getRecent());
    }

    @PostMapping(value="/private/make/order")
    public ResponseEntity<MakeOrderReturn> makeOrder(@Valid @RequestBody NewOrderParams newOrderParams) {
        Order newOrder = new Order(
                UUID.fromString(newOrderParams.getAccount()),
                BigDecimal.valueOf(newOrderParams.getPrice()),
                BigDecimal.valueOf(newOrderParams.getQuantity()),
                newOrderParams.getAction().equals("buy") ? OrderAction.BUY : OrderAction.SELL
                );

        matcher.match(newOrder);

        return ResponseEntity.ok(new MakeOrderReturn(
                orderService.getOrderbook(OrderAction.BUY),
                orderService.getOrderbook(OrderAction.SELL),
                orderService.getOrderbook(OrderAction.BUY, UUID.fromString(newOrderParams.getAccount())),
                orderService.getOrderbook(OrderAction.SELL, UUID.fromString(newOrderParams.getAccount())),
                tradeService.getRecent(),
                orderService.getOrderDepth(OrderAction.BUY),
                orderService.getOrderDepth(OrderAction.SELL)
                ));
    }
}
