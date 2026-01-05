package com.todev.pdv.core.builders;

import com.todev.pdv.core.models.Product;

public class ProductBuilder extends BaseBuilder<Product> {
    @Override
    public ProductBuilder builder() {
        instance = new Product();
        return this;
    }

    public ProductBuilder id(int id) {
        instance.setId(id);
        return this;
    }

    public ProductBuilder description(String description) {
        instance.setDescription(description);
        return this;
    }

    public ProductBuilder amount(int amount) {
        instance.setAmount(amount);
        return this;
    }

    public ProductBuilder price(double price) {
        instance.setPrice(price);
        return this;
    }

    @Override
    public Product build() {
        Product product = instance;
        instance = new Product();
        return product;
    }
}
