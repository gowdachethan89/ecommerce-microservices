package com.ecommerce.order.service;

import com.ecommerce.order.model.OrderEntity;
import com.ecommerce.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final RestTemplate restTemplate;
    private final OrderRepository repository;

    private final String inventoryServiceUrl;

    public OrderServiceImpl(RestTemplate restTemplate, OrderRepository repository, @Value("${inventory.service.url}") String inventoryServiceUrl) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.inventoryServiceUrl = inventoryServiceUrl;
    }

    @Override
    @Transactional
    public Map<String, Object> placeOrder(PlaceOrderRequest request) {
        // 1. Check inventory
        String inventoryUrl = inventoryServiceUrl + "/inventory/" + request.getProductId();

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
        String reserveUrl = inventoryServiceUrl + "/inventory/update";
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

        // Extract productName from fetched batches (InventoryResponseDto now serialized as Map)
        String productName = "";
        if (batches instanceof Map) {
            Object pn = ((Map<?, ?>) batches).get("productName");
            if (pn != null) productName = pn.toString();
        } else if (batches instanceof List) {
            // backward compatibility: if inventory returned list of ProductBatch entities
            List<?> list = (List<?>) batches;
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                Object pn = ((Map<?, ?>) list.get(0)).get("productName");
                if (pn != null) productName = pn.toString();
            }
        }

        // Extract reservedFromBatchIds from reserveResponse
        List<Object> reservedIds = new ArrayList<>();
        if (reserveResponse != null) {
            Object rid = reserveResponse.get("reservedFromBatchIds");
            if (rid instanceof List) {
                reservedIds = (List<Object>) rid;
            }
        }

        // Persist order with productName
        OrderEntity order = new OrderEntity(request.getProductId(), productName, request.getQuantity(), "PLACED", LocalDate.now());
        repository.save(order);

        // Build response exactly as assignment sample
        Map<String, Object> resp = new HashMap<>();
        resp.put("orderId", order.getOrderId());
        resp.put("productId", request.getProductId());
        resp.put("productName", productName);
        resp.put("quantity", request.getQuantity());
        resp.put("status", "PLACED");
        resp.put("reservedFromBatchIds", reservedIds);
        resp.put("message", "Order placed. Inventory reserved.");
        return resp;
    }
}
