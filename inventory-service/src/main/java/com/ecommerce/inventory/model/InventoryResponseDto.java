package com.ecommerce.inventory.model;

import java.util.List;

public class InventoryResponseDto {
    private Long productId;
    private String productName;
    private List<BatchDto> batches;

    public InventoryResponseDto() {}

    public InventoryResponseDto(Long productId, String productName, List<BatchDto> batches) {
        this.productId = productId;
        this.productName = productName;
        this.batches = batches;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public List<BatchDto> getBatches() { return batches; }
    public void setBatches(List<BatchDto> batches) { this.batches = batches; }
}

