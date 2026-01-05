package com.todev.pdv.core.builders;

import com.todev.pdv.core.enums.PaymentMethod;
import com.todev.pdv.core.models.Sale;

public class SaleBuilder extends BaseBuilder<Sale> {
    @Override
    public SaleBuilder builder() {
        instance = new Sale();
        return this;
    }

    public SaleBuilder id(int id) {
        instance.setId(id);
        return this;
    }

    public SaleBuilder userId(int id) {
        instance.setUserId(id);
        return this;
    }

    public SaleBuilder paymentMethod(PaymentMethod paymentMethod) {
        instance.setPaymentMethod(paymentMethod);
        return this;
    }

    public SaleBuilder discountPercentage(int percentage) {
        instance.setDiscount(percentage);
        return this;
    }

    public SaleBuilder total(double total) {
        instance.setTotal(total);
        return this;
    }

    @Override
    public Sale build() {
        Sale sale = instance;
        instance = new Sale();
        return sale;
    }
}
