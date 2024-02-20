package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "sales")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sale {
    @Id
    private Integer id;
    private Integer userId;
    private PaymentMethod paymentMethod;
    private Integer discount;
    private Double total;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
