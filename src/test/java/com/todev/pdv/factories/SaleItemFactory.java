package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.SaleItemRequestBody;
import com.todev.pdv.core.builders.SaleItemBuilder;
import com.todev.pdv.core.models.SaleItem;

import java.time.LocalDateTime;

public final class SaleItemFactory {
    private static final int SALE_ID = 1;
    private static final int PRODUCT_ID = 2;
    private static final int AMOUNT = 1;
    private static final double PRICE = 1750.9;
    private static final LocalDateTime DATE = LocalDateTime.now();

    private SaleItemFactory() {
    }

    public static SaleItem getSaleItem() {
        return new SaleItemBuilder()
                .builder()
                .saleId(SALE_ID)
                .productId(PRODUCT_ID)
                .amount(AMOUNT)
                .price(PRICE)
                .createdAt(DATE)
                .build();
    }

    public static SaleItem getInactiveSaleItem() {
        return new SaleItemBuilder()
                .builder()
                .saleId(SALE_ID)
                .productId(PRODUCT_ID)
                .amount(AMOUNT)
                .price(PRICE)
                .createdAt(DATE)
                .deletedAt(DATE)
                .build();
    }

    public static SaleItem getSavedSaleItem() {
        return new SaleItemBuilder()
                .builder()
                .id(1)
                .saleId(SALE_ID)
                .productId(PRODUCT_ID)
                .amount(AMOUNT)
                .price(PRICE)
                .createdAt(DATE)
                .build();
    }

    public static SaleItemRequestBody getSaleItemWithoutIssues(Integer productId) {
        return new SaleItemRequestBody(
                productId,
                2
        );
    }

    public static SaleItemRequestBody getSaleItemWithNoEnoughStockProduct(Integer productId) {
        return new SaleItemRequestBody(
                productId,
                100
        );
    }

    public static SaleItemRequestBody getSaleItemWithAmountEqualsToZero(Integer productId) {
        return new SaleItemRequestBody(
                productId,
                0
        );
    }
}
