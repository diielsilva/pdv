package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.SaleItemRequest;
import com.todev.pdv.core.models.SaleItem;

import java.time.LocalDateTime;

public final class SaleItemFactory {

    private SaleItemFactory() {
    }

    public static SaleItem getSaleItem() {
        return new SaleItem(
                null,
                1,
                1,
                1,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }

    public static SaleItem getInactiveSaleItem() {
        return new SaleItem(
                null,
                1,
                1,
                1,
                1750.90,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static SaleItem getSavedSaleItem() {
        return new SaleItem(
                1,
                1,
                1,
                1,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }

    public static SaleItemRequest getSaleItemWithoutIssues(Integer productId) {
        return new SaleItemRequest(
                productId,
                2
        );
    }

    public static SaleItemRequest getSaleItemWithNoEnoughStockProduct(Integer productId) {
        return new SaleItemRequest(
                productId,
                100
        );
    }

    public static SaleItemRequest getSaleItemWithAmountEqualsToZero(Integer productId) {
        return new SaleItemRequest(
                productId,
                0
        );
    }


}
