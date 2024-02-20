package com.todev.pdv.common.mappers.contracts;

import com.todev.pdv.common.dtos.*;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.models.User;

import java.util.List;

public interface ModelMapper {
    User toModel(UserRequest userRequest);

    UserResponse toDTO(User user);

    Product toModel(ProductRequest productRequest);

    ProductResponse toDTO(Product product);

    Sale toModel(SaleRequest saleRequest);

    SaleResponse toDTO(Sale sale);

    SaleDetailsResponse toDTO(User user, List<SaleItem> items, List<Product> products);

    SaleReportResponse toDTO(Integer discount, Double total, Double totalWithDiscount, List<SaleItem> items, List<Product> products);

    SaleItem toModel(SaleItemRequest saleItemRequest);

    SaleItemResponse toDTO(SaleItem item, Product product);

}
