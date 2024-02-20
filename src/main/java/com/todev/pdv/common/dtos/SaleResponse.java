package com.todev.pdv.common.dtos;

import com.todev.pdv.core.enums.PaymentMethod;

import java.time.LocalDateTime;

public record SaleResponse(Integer id,
                           PaymentMethod paymentMethod,
                           Integer discount,
                           Double total,
                           LocalDateTime createdAt,
                           LocalDateTime deletedAt) {
}
