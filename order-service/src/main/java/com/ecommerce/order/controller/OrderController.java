package com.ecommerce.order.controller;

import com.ecommerce.order.service.OrderService;
import com.ecommerce.order.service.PlaceOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest req) {
        return ResponseEntity.ok(orderService.placeOrder(req));
    }
}

