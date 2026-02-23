package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.model.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {
    List<ProductBatch> findByProductIdOrderByExpiryDateAsc(Long productId);
}

