package com.ecommerce.inventory.controller;

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

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getBatches(@PathVariable Long productId) {
        try {
            List<ProductBatch> batches = inventoryService.getBatchesForProduct(productId);
            return ResponseEntity.ok(batches);
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
