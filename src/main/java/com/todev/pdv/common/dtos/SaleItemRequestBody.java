package com.todev.pdv.common.dtos;

import com.todev.pdv.common.constraints.contracts.ItemAmount;
import jakarta.validation.constraints.NotNull;

public record SaleItemRequestBody(
        @NotNull(message = "O ID do produto não pode ser nulo!")
        Integer productId,
        @ItemAmount
        Integer amount
) {
}
