package com.todev.pdv.core.builders;

import com.todev.pdv.core.enums.OnlineSalePlatform;
import com.todev.pdv.core.models.OnlineSale;

public class OnlineSaleBuilder extends BaseBuilder<OnlineSale>{
    @Override
    public OnlineSaleBuilder builder() {
        instance = new OnlineSale();
        return this;
    }

    public OnlineSaleBuilder saleId(int id) {
        instance.setSaleId(id);
        return this;
    }

    public OnlineSaleBuilder platform(OnlineSalePlatform platform) {
        instance.setPlatform(platform);
        return this;
    }

    @Override
    public OnlineSale build() {
        OnlineSale sale = instance;
        instance = new OnlineSale();
        return sale;
    }
}
