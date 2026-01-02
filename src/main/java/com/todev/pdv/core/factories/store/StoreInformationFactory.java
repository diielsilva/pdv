package com.todev.pdv.core.factories.store;

public class StoreInformationFactory {
    private StoreInformationFactory() {
    }

    public static StoreInformation create(String location) {
        String store = location == null ? "vitoria" : location;

        if (store.equalsIgnoreCase("vitoria")) {
            return new VitoriaStoreInformation();
        }

        return new CarpinaStoreInformation();
    }

}
