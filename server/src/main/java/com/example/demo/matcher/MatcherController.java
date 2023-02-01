package com.example.demo.matcher;

import com.example.demo.matcher.models.*;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.service.UserService;
import com.example.demo.security.token.JwtTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class MatcherController {
    @Autowired
    private final Matcher matcher;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final TradeService tradeService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping(value = "/public/orderbook/buy")
    public ResponseEntity<List<OrderbookItem>> orderbook_buy() {
        return ResponseEntity.ok(orderService.getOrderbook(OrderAction.BUY));
    }

    private String getUsernameFromAuthHeader(String authHeader) {
        String token = authHeader.split(" ")[1].trim();
        return jwtTokenUtil.getSubject(token).split(",")[1];
    }

    @GetMapping(value = "/private/orderbook/buy")
    public ResponseEntity<List<OrderbookItem>> orderbook_buy(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String username = getUsernameFromAuthHeader(authHeader);
            return ResponseEntity.ok(orderService.getOrderbook(OrderAction.BUY, username));
        }
        catch(Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/public/orderbook/sell")
    public ResponseEntity<List<OrderbookItem>> orderbook_sell() {
        return ResponseEntity.ok(orderService.getOrderbook(OrderAction.SELL));
    }

    @GetMapping(value = "/private/orderbook/sell")
    public ResponseEntity<List<OrderbookItem>> orderbook_sell(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String username = getUsernameFromAuthHeader(authHeader);
            return ResponseEntity.ok(orderService.getOrderbook(OrderAction.SELL, username));
        }
        catch(Exception e) {
            log.error(e.toString());
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
    public ResponseEntity<NewOrderReturn> makeOrder(@Valid @RequestBody NewOrderParams newOrderParams, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String username;
        try{
            username = getUsernameFromAuthHeader(authHeader);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        OrderObj newOrder = new OrderObj(
                userService.getUser(username),
                BigDecimal.valueOf(newOrderParams.getPrice()),
                BigDecimal.valueOf(newOrderParams.getQuantity()),
                newOrderParams.getAction().equals("buy") ? OrderAction.BUY : OrderAction.SELL
        );

        matcher.match(newOrder);

        return ResponseEntity.ok(new NewOrderReturn(
                orderService.getOrderbook(OrderAction.BUY),
                orderService.getOrderbook(OrderAction.SELL),
                orderService.getOrderbook(OrderAction.BUY, username),
                orderService.getOrderbook(OrderAction.SELL, newOrder.getUser().getUsername()),
                tradeService.getRecent(),
                orderService.getOrderDepth(OrderAction.BUY),
                orderService.getOrderDepth(OrderAction.SELL)
                ));
    }
}
