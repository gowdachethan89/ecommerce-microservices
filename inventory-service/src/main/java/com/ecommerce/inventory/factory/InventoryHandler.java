package com.ecommerce.inventory.factory;

import com.ecommerce.inventory.model.ProductBatch;
import com.ecommerce.inventory.repository.ProductBatchRepository;

import java.util.Map;

public interface InventoryHandler {
    /**
     * Reserve quantity across batches and persist changes. Returns a map with details: reservedBatchIds, message
     */
    Map<String, Object> reserve(ProductBatchRepository repository, Long productId, int quantity);
}

