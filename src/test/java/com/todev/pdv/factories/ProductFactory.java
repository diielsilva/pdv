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

    public static ProductRequest getProductWithoutIssues() {
        return new ProductRequest(
                "Samsung Galaxy S20",
                10,
                1750.90
        );
    }

    public static ProductRequest getProductWithAmountEqualsToMinusOne() {
        return new ProductRequest(
                "Samsung Galaxy S20",
                -1,
                1750.90
        );
    }

    public static ProductRequest getProductWithPriceEqualsToMinusOne() {
        return new ProductRequest(
                "Samsung Galaxy S20",
                10,
                -1.0
        );
    }

    public static ProductRequest getProductWithoutValues() {
        return new ProductRequest(
                null,
                null,
                null
        );
    }

    public static ProductResponse getProductResponse() {
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
