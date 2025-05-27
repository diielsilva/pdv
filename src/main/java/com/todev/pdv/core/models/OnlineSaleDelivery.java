package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.DeliveryPlatform;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "online_sales_delivery")
public class OnlineSaleDelivery {
    @Id
    private Integer onlineSaleId;
    private DeliveryPlatform platform;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public OnlineSaleDelivery(Integer onlineSaleId, DeliveryPlatform platform, Double price, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.onlineSaleId = onlineSaleId;
        this.platform = platform;
        this.price = price;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public OnlineSaleDelivery() {
    }

    public Integer getOnlineSaleId() {
        return onlineSaleId;
    }

    public void setOnlineSaleId(Integer onlineSaleId) {
        this.onlineSaleId = onlineSaleId;
    }

    public DeliveryPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(DeliveryPlatform platform) {
        this.platform = platform;
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
