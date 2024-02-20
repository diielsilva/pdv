package com.todev.pdv.common.dtos;

public record SaleItemResponse(Integer id,
                               String productDescription,
                               Integer amount,
                               Double price) {
}
