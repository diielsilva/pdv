package com.todev.pdv.core.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "products")
public class Product {
    @Id
    private Integer id;
    private String description;
    private Integer amount;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public Product(Integer id, String description, Integer amount, Double price, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.price = price;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
