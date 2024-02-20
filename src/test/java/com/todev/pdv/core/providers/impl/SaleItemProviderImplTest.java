package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.repositories.SaleItemRepository;
import com.todev.pdv.factories.SaleItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.when;

@ExtendWith(SpringExtension.class)
class SaleItemProviderImplTest {
    @InjectMocks
    private SaleItemProviderImpl saleItemProvider;

    @Mock
    private SaleItemRepository saleItemRepository;

    @BeforeEach
    void setUpSaleItemRepository() {
        when(saleItemRepository.save(any(SaleItem.class)))
                .thenReturn(SaleItemFactory.getSavedSaleItem());

        when(saleItemRepository.findBySaleId(anyInt()))
                .thenReturn(List.of(SaleItemFactory.getSavedSaleItem()));
    }

    @Test
    void save_SaleItemShouldBeSaved_WhenValidSaleItemWasReceived() {
        assertDoesNotThrow(() -> saleItemProvider.save(SaleItemFactory.getSaleItem()));
    }

    @Test
    void findBySaleId_SaleItemsShouldBeReturned_WhenIdWasFound() {
        var saleItems = saleItemProvider.findBySaleId(1);
        assertEquals(1, saleItems.size());
    }
}