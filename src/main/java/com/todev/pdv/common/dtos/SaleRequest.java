package com.todev.pdv.common.dtos;

import com.todev.pdv.common.constraints.contracts.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleRequest(
        @PaymentMethod
        String paymentMethod,

        @NotNull(message = "O desconto não pode ser nulo")
        @Min(message = "O desconto deve ser maior ou igual a zero!", value = 0)
        @Max(message = "O desconto deve ser menor ou igual a cem!", value = 100)
        Integer discount,

        @NotNull(message = "Os itens da venda não podem ser nulos!")
        @NotEmpty(message = "A venda deve conter ao menos um item!")
        @Valid
        List<SaleItemRequest> items) {
}
