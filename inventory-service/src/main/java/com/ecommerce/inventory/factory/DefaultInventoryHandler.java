package com.ecommerce.inventory.factory;

import com.ecommerce.inventory.model.ProductBatch;
import com.ecommerce.inventory.repository.ProductBatchRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("defaultInventoryHandler")
public class DefaultInventoryHandler implements InventoryHandler {

    @Override
    public Map<String, Object> reserve(ProductBatchRepository repository, Long productId, int quantity) {
        List<ProductBatch> batches = repository.findByProductIdOrderByExpiryDateAsc(productId);
        int remaining = quantity;
        List<Long> reservedBatchIds = new ArrayList<>();

        for (ProductBatch b : batches) {
            if (remaining <= 0) break;
            if (b.getQuantity() == null || b.getQuantity() <= 0) continue;
            int take = Math.min(b.getQuantity(), remaining);
            b.setQuantity(b.getQuantity() - take);
            remaining -= take;
            reservedBatchIds.add(b.getBatchId());
            repository.save(b);
        }

        Map<String, Object> result = new HashMap<>();
        if (remaining > 0) {
            result.put("success", false);
            result.put("message", "Insufficient inventory. Remaining=" + remaining);
        } else {
            result.put("success", true);
            result.put("message", "Reserved");
        }
        result.put("reservedFromBatchIds", reservedBatchIds);
        return result;
    }
}
