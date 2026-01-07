package com.todev.pdv.common.dtos;

public record SaleItemResponseBody(
        Integer id,
        String productDescription,
        Integer amount,
        Double price
) {
}
