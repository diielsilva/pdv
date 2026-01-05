package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.ProductRequest;
import com.todev.pdv.common.dtos.ProductResponse;
import com.todev.pdv.core.builders.ProductBuilder;
import com.todev.pdv.core.models.Product;

import java.time.LocalDateTime;

public final class ProductFactory {
    private static final String DESCRIPTION = "Samsung Galaxy S20";
    private static final int AMOUNT = 10;
    private static final double PRICE = 1750.9;
    private static final LocalDateTime DATE = LocalDateTime.now();

    private ProductFactory() {
    }

    public static Product getProduct() {
        return new ProductBuilder()
                .builder()
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .price(PRICE)
                .createdAt(DATE)
                .build();
    }

    public static Product getSavedProduct() {
        return new ProductBuilder()
                .builder()
                .id(1)
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .price(PRICE)
                .createdAt(DATE)
                .build();
    }

    public static Product getInactiveProduct() {
        return new ProductBuilder()
                .builder()
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .price(PRICE)
                .createdAt(DATE)
                .deletedAt(DATE)
                .build();
    }

    public static Product getInactiveSavedProduct() {
        return new ProductBuilder()
                .builder()
                .id(1)
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .price(PRICE)
                .createdAt(DATE)
                .deletedAt(DATE)
                .build();
    }

    public static ProductRequest getRequestDTO() {
        return new ProductRequest(DESCRIPTION, AMOUNT, PRICE);
    }

    public static ProductResponse getResponseDTO() {
        return new ProductResponse(1, DESCRIPTION, AMOUNT, PRICE, DATE, null);
    }
}
