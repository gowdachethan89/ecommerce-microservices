package com.ecommerce.inventory.service;

import com.ecommerce.inventory.model.ProductBatch;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    List<ProductBatch> getBatchesForProduct(Long productId);
    Map<String, Object> updateInventory(Long productId, int quantity);
}


