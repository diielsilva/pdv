package com.todev.pdv.core.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "sales_items")
public class SaleItem {
    @Id
    private Integer id;
    private Integer saleId;
    private Integer productId;
    private Integer amount;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public SaleItem(Integer id, Integer saleId, Integer productId, Integer amount, Double price, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.amount = amount;
        this.price = price;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public SaleItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
