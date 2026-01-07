package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.SaleRequestBody;
import com.todev.pdv.common.dtos.SaleResponseBody;
import com.todev.pdv.core.builders.SaleBuilder;
import com.todev.pdv.core.enums.PaymentMethod;
import com.todev.pdv.core.models.Sale;

import java.time.LocalDateTime;
import java.util.List;

public final class SaleFactory {
    private static final int ID = 1;
    private static final int USER_ID = 1;
    private static final PaymentMethod PAYMENT_METHOD = PaymentMethod.CARD;
    private static final int DISCOUNT_PERCENTAGE = 0;
    private static final double TOTAL = 1750.9;
    private static final LocalDateTime DATE = LocalDateTime.now();

    private SaleFactory() {
    }

    public static Sale getSale() {
        return new SaleBuilder()
                .builder()
                .userId(USER_ID)
                .paymentMethod(PAYMENT_METHOD)
                .discountPercentage(DISCOUNT_PERCENTAGE)
                .total(TOTAL)
                .createdAt(DATE)
                .build();
    }

    public static Sale getInactiveSale() {
        return new SaleBuilder()
                .builder()
                .userId(USER_ID)
                .paymentMethod(PAYMENT_METHOD)
                .discountPercentage(DISCOUNT_PERCENTAGE)
                .total(TOTAL)
                .createdAt(DATE)
                .deletedAt(DATE)
                .build();
    }

    public static Sale getSavedSale() {
        return new SaleBuilder()
                .builder()
                .id(ID)
                .userId(USER_ID)
                .paymentMethod(PAYMENT_METHOD)
                .discountPercentage(DISCOUNT_PERCENTAGE)
                .total(TOTAL)
                .createdAt(DATE)
                .build();
    }

    public static Sale getInactiveSavedSale() {
        return new SaleBuilder()
                .builder()
                .id(ID)
                .userId(USER_ID)
                .paymentMethod(PAYMENT_METHOD)
                .discountPercentage(DISCOUNT_PERCENTAGE)
                .total(TOTAL)
                .createdAt(DATE)
                .deletedAt(DATE)
                .build();
    }

    public static SaleRequestBody getRequestDTO(Integer productId) {
        return new SaleRequestBody(
                "CARD",
                0,
                List.of(SaleItemFactory.getSaleItemWithoutIssues(productId))
        );
    }

    public static SaleRequestBody getSaleWithDuplicatedItems(Integer productId) {
        return new SaleRequestBody(
                "CARD",
                0,
                List.of(SaleItemFactory.getSaleItemWithoutIssues(productId), SaleItemFactory.getSaleItemWithoutIssues(productId))
        );
    }

    public static SaleRequestBody getSaleWithNotEnoughStockItems(Integer productId) {
        return new SaleRequestBody(
                "CARD",
                0,
                List.of(SaleItemFactory.getSaleItemWithNoEnoughStockProduct(productId))
        );
    }

    public static SaleResponseBody getResponseDTO() {
        return new SaleResponseBody(
                1,
                PaymentMethod.CARD,
                0,
                1750.90,
                LocalDateTime.now(),
                null
        );
    }
}
