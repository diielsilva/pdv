package com.todev.pdv.common.dtos;

import com.todev.pdv.common.constraints.contracts.Price;
import com.todev.pdv.common.constraints.contracts.ProductAmount;
import jakarta.validation.constraints.NotBlank;

public record ProductRequest(
        @NotBlank(message = "A descrição não pode ser nula!")
        String description,
        @ProductAmount
        Integer amount,
        @Price
        Double price) {
}
