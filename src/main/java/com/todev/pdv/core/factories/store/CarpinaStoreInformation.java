package com.todev.pdv.core.factories.store;

public class CarpinaStoreInformation implements StoreInformation {
    private static final String MALL = "Shopping Carpina";
    private static final String LOCATION = "Rodovia PE-041 - KM 02 - Bairro Novo";
    private static final String PHONE = "(81) 99441-7171";
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
