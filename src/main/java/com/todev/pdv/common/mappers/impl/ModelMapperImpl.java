package com.todev.pdv.common.mappers.impl;

import com.todev.pdv.common.dtos.*;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.builders.ProductBuilder;
import com.todev.pdv.core.builders.SaleBuilder;
import com.todev.pdv.core.builders.SaleItemBuilder;
import com.todev.pdv.core.builders.UserBuilder;
import com.todev.pdv.core.enums.PaymentMethod;
import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModelMapperImpl implements ModelMapper {

    @Override
    public User toModel(UserRequestBody requestBody) {
        return new UserBuilder()
                .builder()
                .name(requestBody.name())
                .login(requestBody.login())
                .password(requestBody.password())
                .role(Role.valueOf(requestBody.role()))
                .build();

    }

    @Override
    public UserResponseBody toResponseBody(User user) {
        return new UserResponseBody(
                user.getId(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getDeletedAt()
        );
    }

    @Override
    public Product toModel(ProductRequestBody requestBody) {
        return new ProductBuilder()
                .builder()
                .description(requestBody.description())
                .amount(requestBody.amount())
                .price(requestBody.price())
                .build();
    }

    @Override
    public ProductResponseBody toResponseBody(Product product) {
        return new ProductResponseBody(
                product.getId(),
                product.getDescription(),
                product.getAmount(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getDeletedAt()
        );
    }

    @Override
    public Sale toModel(SaleRequestBody body) {
        return new SaleBuilder()
                .builder()
                .paymentMethod(PaymentMethod.valueOf(body.paymentMethod()))
                .discountPercentage(body.discount())
                .build();
    }

    @Override
    public SaleResponseBody toResponseBody(Sale sale) {
        return new SaleResponseBody(
                sale.getId(),
                sale.getPaymentMethod(),
                sale.getDiscount(),
                sale.getTotal(),
                sale.getCreatedAt(),
                sale.getDeletedAt()
        );
    }

    @Override
    public SaleDetailsResponseBody toResponseBody(User user, List<SaleItem> items, List<Product> products) {
        var responseItems = new ArrayList<SaleItemResponseBody>();

        for (int i = 0; i < items.size(); i++) {
            responseItems.add(toResponseBody(items.get(i), products.get(i)));
        }

        return new SaleDetailsResponseBody(
                user.getName(),
                responseItems
        );
    }

    @Override
    public SaleItem toModel(SaleItemRequestBody body) {
        return new SaleItemBuilder()
                .builder()
                .productId(body.productId())
                .amount(body.amount())
                .build();
    }

    @Override
    public SaleItemResponseBody toResponseBody(SaleItem item, Product product) {
        return new SaleItemResponseBody(
                item.getProductId(),
                product.getDescription(),
                item.getAmount(),
                item.getPrice()
        );
    }
}
