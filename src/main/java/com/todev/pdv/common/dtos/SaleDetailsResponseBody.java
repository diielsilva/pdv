package com.todev.pdv.common.dtos;

import java.util.List;

public record SaleDetailsResponseBody(String sellerName, List<SaleItemResponseBody> items) {
}
