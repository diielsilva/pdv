package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.ProductRequest;
import com.todev.pdv.common.dtos.ProductResponse;
import com.todev.pdv.core.models.Product;

import java.time.LocalDateTime;

public final class ProductFactory {
    private ProductFactory() {
    }

    public static Product getProduct() {
        return new Product(
                null,
                "Samsung Galaxy S20",
                10,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }

    public static Product getSavedProduct() {
        return new Product(
                1,
                "Samsung Galaxy S20",
                10,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }

    public static Product getInactiveProduct() {
        return new Product(
                null,
                "Samsung Galaxy S20",
                10,
                1750.90,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Product getInactiveSavedProduct() {
        return new Product(
                1,
                "Samsung Galaxy S20",
                10,
                1750.90,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static ProductRequest getRequestDTO() {
        return new ProductRequest(
                "Samsung Galaxy S20",
                10,
                1750.90
        );
    }

    public static ProductResponse getResponseDTO() {
        return new ProductResponse(
                1,
                "Samsung Galaxy S20",
                10,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }
}
