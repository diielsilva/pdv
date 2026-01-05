package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.OnlineSalePlatform;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "online_sales")
public class OnlineSale extends BaseModel {
    @Id
    private Integer saleId;
    private OnlineSalePlatform platform;

    public OnlineSale(Integer saleId, OnlineSalePlatform platform, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.saleId = saleId;
        this.platform = platform;
        setCreatedAt(createdAt);
        setDeletedAt(deletedAt);
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
}
