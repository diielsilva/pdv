package com.todev.pdv.common.dtos;

import java.util.List;

public record SaleDetailsResponse(String sellerName,
                                  List<SaleItemResponse> items) {
}
