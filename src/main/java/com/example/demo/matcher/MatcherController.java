package com.example.demo.matcher;

import com.example.demo.matcher.models.*;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.token.JwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

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

    private static String getUsernameFromAuthHeader(String authHeader) {
        String token = authHeader.split(" ")[1].trim();
        return JwtTokenUtil.getSubject(token).split(",")[1];
    }

    @GetMapping(value = "/private/orderbook/buy/{username}")
    public ResponseEntity<List<OrderbookItem>> orderbook_buy(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            if (!getUsernameFromAuthHeader(authHeader).equals(username))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return ResponseEntity.ok(orderService.getOrderbook(OrderAction.BUY, username));
        }
        catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/public/orderbook/sell")
    public ResponseEntity<List<OrderbookItem>> orderbook_sell() {
        return ResponseEntity.ok(orderService.getOrderbook(OrderAction.SELL));
    }

    @GetMapping(value = "/private/orderbook/sell/{username}")
    public ResponseEntity<List<OrderbookItem>> orderbook_sell(@PathVariable String username, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            if (!getUsernameFromAuthHeader(authHeader).equals(username))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return ResponseEntity.ok(orderService.getOrderbook(OrderAction.SELL, username));
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
    public ResponseEntity<MakeOrderReturn> makeOrder(@Valid @RequestBody NewOrderParams newOrderParams, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try{
            if (!getUsernameFromAuthHeader(authHeader).equals(newOrderParams.getUsername()))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        OrderObj newOrder = new OrderObj(
                newOrderParams.getUsername(),
                BigDecimal.valueOf(newOrderParams.getPrice()),
                BigDecimal.valueOf(newOrderParams.getQuantity()),
                newOrderParams.getAction().equals("buy") ? OrderAction.BUY : OrderAction.SELL
                );

        matcher.match(newOrder);

        return ResponseEntity.ok(new MakeOrderReturn(
                orderService.getOrderbook(OrderAction.BUY),
                orderService.getOrderbook(OrderAction.SELL),
                orderService.getOrderbook(OrderAction.BUY, newOrderParams.getUsername()),
                orderService.getOrderbook(OrderAction.SELL, newOrder.getUsername()),
                tradeService.getRecent(),
                orderService.getOrderDepth(OrderAction.BUY),
                orderService.getOrderDepth(OrderAction.SELL)
                ));
    }
}
