package com.ecommerce.order.service;

import java.util.Map;

public interface OrderService {
    Map<String, Object> placeOrder(PlaceOrderRequest request);
}
