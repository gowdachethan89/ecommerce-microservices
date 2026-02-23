package com.ecommerce.inventory.model;

import java.time.LocalDate;

public class BatchDto {
    private Long batchId;
    private Integer quantity;
    private LocalDate expiryDate;

    public BatchDto() {}

    public BatchDto(Long batchId, Integer quantity, LocalDate expiryDate) {
        this.batchId = batchId;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}

