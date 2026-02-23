package com.ecommerce.order.service;

import com.ecommerce.order.model.OrderEntity;
import com.ecommerce.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final RestTemplate restTemplate;
    private final OrderRepository repository;

    public OrderServiceImpl(RestTemplate restTemplate, OrderRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    @Override
    @Transactional
    public Map<String, Object> placeOrder(PlaceOrderRequest request) {
        // 1. Check inventory
        String inventoryUrl = "http://localhost:8081/inventory/" + request.getProductId();

        Object batches;
        try {
            ResponseEntity<Object> resp = restTemplate.getForEntity(inventoryUrl, Object.class);
            batches = resp.getBody();
        } catch (HttpStatusCodeException ex) {
            // Inventory service returned 4xx/5xx
            log.error("Inventory service error when fetching product {}: status={}, body={}", request.getProductId(), ex.getStatusCode(), ex.getResponseBodyAsString());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to fetch inventory: " + ex.getStatusCode());
            error.put("inventoryErrorBody", ex.getResponseBodyAsString());
            return error;
        } catch (Exception ex) {
            log.error("Error calling inventory service", ex);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error contacting inventory service: " + ex.getMessage());
            return error;
        }

        // For the sake of skeleton, we will attempt to reserve by calling update endpoint
        String reserveUrl = "http://localhost:8081/inventory/update";
        Map<String, Object> payload = new HashMap<>();
        payload.put("productId", request.getProductId());
        payload.put("quantity", request.getQuantity());

        Map<String, Object> reserveResponse;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.postForObject(reserveUrl, payload, Map.class);
            reserveResponse = resp;
        } catch (HttpStatusCodeException ex) {
            log.error("Inventory reserve failed: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to reserve inventory: " + ex.getStatusCode());
            error.put("inventoryErrorBody", ex.getResponseBodyAsString());
            return error;
        }

        OrderEntity order = new OrderEntity(request.getProductId(), "", request.getQuantity(), "PLACED", LocalDate.now());
        repository.save(order);

        Map<String, Object> resp = new HashMap<>();
        resp.put("orderId", order.getOrderId());
        resp.put("productId", request.getProductId());
        resp.put("quantity", request.getQuantity());
        resp.put("status", "PLACED");
        resp.put("inventoryResponse", reserveResponse);
        resp.put("message", "Order placed. Inventory reserve attempted.");
        resp.put("fetchedBatches", batches);
        return resp;
    }
}
