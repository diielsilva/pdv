package com.todev.pdv.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "sales_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleItem {
    @Id
    private Integer id;
    private Integer saleId;
    private Integer productId;
    private Integer amount;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
