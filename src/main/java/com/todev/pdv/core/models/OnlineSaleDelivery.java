package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.DeliveryPlatform;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "online_sales_delivery")
public class OnlineSaleDelivery extends BaseModel {
    @Id
    private Integer onlineSaleId;
    private DeliveryPlatform platform;
    private Double price;

    public OnlineSaleDelivery(Integer onlineSaleId, DeliveryPlatform platform, Double price, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.onlineSaleId = onlineSaleId;
        this.platform = platform;
        this.price = price;
        setCreatedAt(createdAt);
        setDeletedAt(deletedAt);
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
}
