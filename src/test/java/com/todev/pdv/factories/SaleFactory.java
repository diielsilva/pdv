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

    public static SaleRequest getRequestDTO(Integer productId) {
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

    public static SaleResponse getResponseDTO() {
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
