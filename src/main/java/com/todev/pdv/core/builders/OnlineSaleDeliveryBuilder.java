package com.todev.pdv.core.builders;

import com.todev.pdv.core.enums.DeliveryPlatform;
import com.todev.pdv.core.models.OnlineSaleDelivery;

public class OnlineSaleDeliveryBuilder extends BaseBuilder<OnlineSaleDelivery>{
    @Override
    public OnlineSaleDeliveryBuilder builder() {
        instance = new OnlineSaleDelivery();
        return this;
    }

    public OnlineSaleDeliveryBuilder onlineSaleId(int id) {
        instance.setOnlineSaleId(id);
        return this;
    }

    public OnlineSaleDeliveryBuilder platform(DeliveryPlatform platform) {
        instance.setPlatform(platform);
        return this;
    }

    public OnlineSaleDeliveryBuilder price(double price) {
        instance.setPrice(price);
        return this;
    }

    @Override
    public OnlineSaleDelivery build() {
        OnlineSaleDelivery delivery = instance;
        instance = new OnlineSaleDelivery();
        return delivery;
    }
}
