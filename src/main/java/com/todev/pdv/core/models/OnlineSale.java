package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.OnlineSalePlatform;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "online_sales")
public class OnlineSale {
    @Id
    private Integer saleId;
    private OnlineSalePlatform platform;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public OnlineSale(Integer saleId, OnlineSalePlatform platform, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.saleId = saleId;
        this.platform = platform;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public OnlineSale() {
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public OnlineSalePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(OnlineSalePlatform platform) {
        this.platform = platform;
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
