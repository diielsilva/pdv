package com.todev.pdv.core.providers.contracts;

import com.todev.pdv.core.models.SaleItem;

import java.util.List;

public interface SaleItemProvider {
    SaleItem save(SaleItem item);

    List<SaleItem> findBySaleId(Integer saleId);
}
