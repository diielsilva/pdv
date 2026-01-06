package com.todev.pdv.core.builders;

import com.todev.pdv.core.models.SaleItem;

public class SaleItemBuilder extends BaseBuilder<SaleItem> {
    @Override
    public SaleItemBuilder builder() {
        instance = new SaleItem();
        return this;
    }

    public SaleItemBuilder id(int id) {
        instance.setId(id);
        return this;
    }

    public SaleItemBuilder saleId(int id) {
        instance.setSaleId(id);
        return this;
    }

    public SaleItemBuilder productId(int id) {
        instance.setProductId(id);
        return this;
    }

    public SaleItemBuilder amount(int amount) {
        instance.setAmount(amount);
        return this;
    }

    public SaleItemBuilder price(double price) {
        instance.setPrice(price);
        return this;
    }

    @Override
    public SaleItem build() {
        return instance;
    }
}
