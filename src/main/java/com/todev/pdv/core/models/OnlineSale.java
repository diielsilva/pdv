package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.OnlineSalePlatform;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "online_sales")
public class OnlineSale {
    @Id
    private Integer saleId;
    private OnlineSalePlatform platform;

    public OnlineSale(Integer saleId, OnlineSalePlatform platform) {
        this.saleId = saleId;
        this.platform = platform;
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
