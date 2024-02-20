package com.todev.pdv.common.mappers.impl;

import com.todev.pdv.common.dtos.*;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
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
    public User toModel(UserRequest userRequest) {
        return new User(
                null,
                userRequest.name(),
                userRequest.login(),
                userRequest.password(),
                Role.valueOf(userRequest.role()),
                null,
                null
        );
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
    public Product toModel(ProductRequest productRequest) {
        return new Product(
                null,
                productRequest.description(),
                productRequest.amount(),
                productRequest.price(),
                null,
                null
        );
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
    public Sale toModel(SaleRequest saleRequest) {
        return new Sale(
                null,
                null,
                PaymentMethod.valueOf(saleRequest.paymentMethod()),
                saleRequest.discount(),
                null,
                null,
                null
        );
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
    public SaleReportResponse toDTO(Integer discount, Double total, Double totalWithDiscount, List<SaleItem> items, List<Product> products) {
        var responseItems = new ArrayList<SaleItemResponse>();

        for (int i = 0; i < items.size(); i++) {
            responseItems.add(toDTO(items.get(i), products.get(i)));
        }

        return new SaleReportResponse(
                discount,
                total,
                totalWithDiscount,
                responseItems
        );
    }

    @Override
    public SaleItem toModel(SaleItemRequest saleItemRequest) {
        return new SaleItem(
                null,
                null,
                saleItemRequest.productId(),
                saleItemRequest.amount(),
                null,
                null,
                null
        );
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
