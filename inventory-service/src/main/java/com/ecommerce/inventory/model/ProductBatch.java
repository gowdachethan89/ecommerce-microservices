package com.ecommerce.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "product_batch")
public class ProductBatch {

    @Id
    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    public ProductBatch() {}

    public ProductBatch(Long batchId, Long productId, String productName, Integer quantity, LocalDate expiryDate) {
        this.batchId = batchId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}

