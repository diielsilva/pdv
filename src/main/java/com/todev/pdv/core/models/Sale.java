package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.PaymentMethod;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "sales")
public class Sale {
    @Id
    private Integer id;
    private Integer userId;
    private PaymentMethod paymentMethod;
    private Integer discount;
    private Double total;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public Sale(Integer id, Integer userId, PaymentMethod paymentMethod, Integer discount, Double total, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.discount = discount;
        this.total = total;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public Sale() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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
