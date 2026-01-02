package com.todev.pdv.core.factories.store;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StoreInformationFactoryTest {

    @Test
    void create_VitoriaStoreInformationShouldBeReturned_WhenStoreLocationIsEqualsToVitoria() {
        StoreInformation store = StoreInformationFactory.create("vitoria");

        assertEquals(VitoriaStoreInformation.class, store.getClass());
    }

    @Test
    void create_CarpinaStoreInformationShouldBeReturned_WhenStoreLocationIsNotEqualsToVitoria() {
        StoreInformation store = StoreInformationFactory.create("carpina");

        assertEquals(CarpinaStoreInformation.class, store.getClass());
    }

    @Test
    void create_VitoriaStoreInformationShouldBeReturned_WhenStoreLocationIsNull() {
        StoreInformation store = StoreInformationFactory.create(null);

        assertEquals(VitoriaStoreInformation.class, store.getClass());
    }
}