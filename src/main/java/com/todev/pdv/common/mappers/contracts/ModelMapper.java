package com.todev.pdv.common.mappers.contracts;

import com.todev.pdv.common.dtos.*;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.models.User;

import java.util.List;

public interface ModelMapper {
    User toModel(UserRequestBody requestBody);

    UserResponseBody toResponseBody(User user);

    Product toModel(ProductRequestBody requestBody);

    ProductResponseBody toResponseBody(Product product);

    Sale toModel(SaleRequestBody requestBody);

    SaleResponseBody toResponseBody(Sale sale);

    SaleDetailsResponseBody toResponseBody(User user, List<SaleItem> items, List<Product> products);

    SaleItem toModel(SaleItemRequestBody requestBody);

    SaleItemResponseBody toResponseBody(SaleItem item, Product product);

}
