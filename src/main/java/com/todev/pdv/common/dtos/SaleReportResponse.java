package com.todev.pdv.common.dtos;

import java.util.List;

public record SaleReportResponse(Integer discount,
                                 Double total,
                                 Double totalWithDiscount,
                                 List<SaleItemResponse> items) {
}
