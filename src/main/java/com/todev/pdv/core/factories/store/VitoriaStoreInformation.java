package com.todev.pdv.core.factories.store;

public class VitoriaStoreInformation implements StoreInformation {
    private static final String MALL = "Vitória Park Shopping";
    private static final String LOCATION = "Rua Henrique de Holanda - Nº 3000";
    private static final String PHONE = "(81) 99451-3987";
    private static final String CNPJ = "CNPJ 35.699.902/000010-42";

    @Override
    public String getMall() {
        return MALL;
    }

    @Override
    public String getLocation() {
        return LOCATION;
    }

    @Override
    public String getPhone() {
        return PHONE;
    }

    @Override
    public String getCnpj() {
        return CNPJ;
    }
}
