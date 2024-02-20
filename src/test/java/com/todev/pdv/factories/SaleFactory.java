package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.SaleRequest;
import com.todev.pdv.common.dtos.SaleResponse;
import com.todev.pdv.core.enums.PaymentMethod;
import com.todev.pdv.core.models.Sale;

import java.time.LocalDateTime;
import java.util.List;

public final class SaleFactory {

    private SaleFactory() {
    }

    public static Sale getSale() {
        return new Sale(
                null,
                1,
                PaymentMethod.CARD,
                0,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }

    public static Sale getInactiveSale() {
        return new Sale(
                null,
                1,
                PaymentMethod.CARD,
                0,
                1750.90,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Sale getSavedSale() {
        return new Sale(
                1,
                1,
                PaymentMethod.CARD,
                0,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }

    public static Sale getInactiveSavedSale() {
        return new Sale(
                1,
                1,
                PaymentMethod.CARD,
                0,
                1750.90,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static SaleRequest getSaleWithoutIssues(Integer productId) {
        return new SaleRequest(
                "CARD",
                0,
                List.of(SaleItemFactory.getSaleItemWithoutIssues(productId))
        );
    }

    public static SaleRequest getSaleWithDuplicatedItems(Integer productId) {
        return new SaleRequest(
                "CARD",
                0,
                List.of(SaleItemFactory.getSaleItemWithoutIssues(productId), SaleItemFactory.getSaleItemWithoutIssues(productId))
        );
    }

    public static SaleRequest getSaleWithNotEnoughStockItems(Integer productId) {
        return new SaleRequest(
                "CARD",
                0,
                List.of(SaleItemFactory.getSaleItemWithNoEnoughStockProduct(productId))
        );
    }

    public static SaleRequest getSaleWithoutItems() {
        return new SaleRequest(
                "CARD",
                0,
                List.of()
        );
    }

    public static SaleRequest getSaleWithInvalidPaymentMethod(Integer productId) {
        return new SaleRequest(
                "CARDS",
                0,
                List.of(SaleItemFactory.getSaleItemWithoutIssues(productId))
        );
    }

    public static SaleRequest getSaleWithDiscountEqualsToMinusOne(Integer productId) {
        return new SaleRequest(
                "CARD",
                -1,
                List.of(SaleItemFactory.getSaleItemWithoutIssues(productId))
        );
    }

    public static SaleRequest getSaleWithItemsWithAmountEqualsToZero(Integer productId) {
        return new SaleRequest(
                "CARD",
                100,
                List.of(SaleItemFactory.getSaleItemWithAmountEqualsToZero(productId))
        );
    }

    public static SaleRequest getSaleWithoutValues() {
        return new SaleRequest(
                null,
                null,
                null
        );
    }

    public static SaleResponse getSaleResponse() {
        return new SaleResponse(
                1,
                PaymentMethod.CARD,
                0,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }
}
