package com.todev.pdv.core.factories.store;

public class StoreInformationFactory {
    private StoreInformationFactory() {
    }

    public static StoreInformation getInstance(String currentLocation) {
        if (currentLocation.equalsIgnoreCase("vitoria")) {
            return new VitoriaStoreInformation();
        }

        return new CarpinaStoreInformation();
    }

}
