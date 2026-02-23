package com.ecommerce.inventory.service;

import com.ecommerce.inventory.factory.InventoryHandlerFactory;
import com.ecommerce.inventory.model.ProductBatch;
import com.ecommerce.inventory.repository.ProductBatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final ProductBatchRepository repository;
    private final InventoryHandlerFactory handlerFactory;

    public InventoryServiceImpl(ProductBatchRepository repository, InventoryHandlerFactory handlerFactory) {
        this.repository = repository;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public List<ProductBatch> getBatchesForProduct(Long productId) {
        try {
            return repository.findByProductIdOrderByExpiryDateAsc(productId);
        } catch (Exception ex) {
            log.error("Error fetching batches for product {}", productId, ex);
            // throw a runtime exception so controller can return a structured error
            throw new RuntimeException("Failed to fetch product batches: " + ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> updateInventory(Long productId, int quantity) {
        var handler = handlerFactory.getHandler("default");
        return handler.reserve(repository, productId, quantity);
    }
}
