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
    public User toModel(UserRequest body) {
        return new UserBuilder()
                .builder()
                .name(body.name())
                .login(body.login())
                .password(body.password())
                .role(Role.valueOf(body.role()))
                .build();

    }

    @Override
    public UserResponse toDTO(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getDeletedAt()
        );
    }

    @Override
    public Product toModel(ProductRequest body) {
        return new ProductBuilder()
                .builder()
                .description(body.description())
                .amount(body.amount())
                .price(body.price())
                .build();
    }

    @Override
    public ProductResponse toDTO(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getDescription(),
                product.getAmount(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getDeletedAt()
        );
    }

    @Override
    public Sale toModel(SaleRequest body) {
        return new SaleBuilder()
                .builder()
                .paymentMethod(PaymentMethod.valueOf(body.paymentMethod()))
                .discountPercentage(body.discount())
                .build();
    }

    @Override
    public SaleResponse toDTO(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getPaymentMethod(),
                sale.getDiscount(),
                sale.getTotal(),
                sale.getCreatedAt(),
                sale.getDeletedAt()
        );
    }

    @Override
    public SaleDetailsResponse toDTO(User user, List<SaleItem> items, List<Product> products) {
        var responseItems = new ArrayList<SaleItemResponse>();

        for (int i = 0; i < items.size(); i++) {
            responseItems.add(toDTO(items.get(i), products.get(i)));
        }

        return new SaleDetailsResponse(
                user.getName(),
                responseItems
        );
    }

    @Override
    public SaleItem toModel(SaleItemRequest body) {
        return new SaleItemBuilder()
                .builder()
                .productId(body.productId())
                .amount(body.amount())
                .build();
    }

    @Override
    public SaleItemResponse toDTO(SaleItem item, Product product) {
        return new SaleItemResponse(
                item.getProductId(),
                product.getDescription(),
                item.getAmount(),
                item.getPrice()
        );
    }
}
