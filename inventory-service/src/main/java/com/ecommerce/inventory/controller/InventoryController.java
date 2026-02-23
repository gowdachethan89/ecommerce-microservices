package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.model.BatchDto;
import com.ecommerce.inventory.model.InventoryResponseDto;
import com.ecommerce.inventory.model.ProductBatch;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.service.UpdateInventoryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getBatches(@PathVariable("productId") Long productId) {
        try {
            List<ProductBatch> batches = inventoryService.getBatchesForProduct(productId);
            if (batches == null || batches.isEmpty()) {
                // return an empty wrapped response
                InventoryResponseDto empty = new InventoryResponseDto(productId, null, List.of());
                return ResponseEntity.ok(empty);
            }
            String productName = batches.get(0).getProductName();
            List<BatchDto> batchDtos = batches.stream()
                    .map(b -> new BatchDto(b.getBatchId(), b.getQuantity(), b.getExpiryDate()))
                    .collect(Collectors.toList());
            InventoryResponseDto resp = new InventoryResponseDto(productId, productName, batchDtos);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            log.error("Error fetching batches for product {}", productId, ex);
            Map<String, Object> err = new HashMap<>();
            err.put("timestamp", java.time.OffsetDateTime.now().toString());
            err.put("status", 500);
            err.put("error", "Internal Server Error");
            err.put("message", ex.getMessage());
            err.put("path", "/inventory/" + productId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(@RequestBody UpdateInventoryRequest request) {
        var result = inventoryService.updateInventory(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(result);
    }
}
