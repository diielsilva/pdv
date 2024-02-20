package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.exceptions.ModelNotFoundException;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.repositories.SaleRepository;
import com.todev.pdv.factories.SaleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.when;

@ExtendWith(SpringExtension.class)
class SaleProviderImplTest {
    @InjectMocks
    private SaleProviderImpl saleProvider;

    @Mock
    private SaleRepository saleRepository;

    @BeforeEach
    void setUpSaleRepository() {
        when(saleRepository.save(any(Sale.class)))
                .thenReturn(SaleFactory.getSavedSale());

        when(saleRepository.findByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SaleFactory.getSavedSale())));

        when(saleRepository.findByDeletedAtIsNotNull(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SaleFactory.getInactiveSavedSale())));

        when(saleRepository.findByCreatedAtBetweenAndDeletedAtIsNull(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(SaleFactory.getSavedSale()));

        when(saleRepository.findByCreatedAtBetweenAndDeletedAtIsNotNull(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(SaleFactory.getInactiveSavedSale()));

        when(saleRepository.findByIdAndDeletedAtIsNull(anyInt()))
                .thenReturn(Optional.of(SaleFactory.getSavedSale()));

        when(saleRepository.findByIdAndDeletedAtIsNotNull(anyInt()))
                .thenReturn(Optional.of(SaleFactory.getSavedSale()));

        when(saleRepository.findById(anyInt()))
                .thenReturn(Optional.of(SaleFactory.getSavedSale()));
    }

    @Test
    void save_SaleShouldBeSaved_WhenValidSaleWasReceived() {
        assertDoesNotThrow(() -> saleProvider.save(SaleFactory.getSale()));
    }

    @Test
    void findActive_SalesShouldBeReturned_WhenHaveActiveSales() {
        var sales = saleProvider.findActive(PageRequest.of(0, 5));
        assertEquals(1, sales.getContent().size());
    }

    @Test
    void findInactive_SalesShouldBeReturned_WhenHaveInactiveSales() {
        var sales = saleProvider.findInactive(PageRequest.of(0, 5));
        assertEquals(1, sales.getContent().size());
    }

    @Test
    void findActiveByDate_SalesShouldBeReturned_WhenHaveActiveSalesWithSelectedDate() {
        var sales = saleProvider.findActiveByDate(LocalDateTime.now(), LocalDateTime.now());
        assertEquals(1, sales.size());
    }

    @Test
    void findInactiveByDate_SalesShouldBeReturned_WhenHaveInactiveSalesWithSelectedDate() {
        var sales = saleProvider.findInactiveByDate(LocalDateTime.now(), LocalDateTime.now());
        assertEquals(1, sales.size());
    }

    @Test
    void findActiveById_SaleShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleProvider.findActiveById(1));
    }

    @Test
    void findActiveById_SaleShouldNotBeReturned_WhenIdWasNotFound() {
        when(saleRepository.findByIdAndDeletedAtIsNull(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> saleProvider.findActiveById(0));
    }

    @Test
    void findInactiveById_SaleShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleProvider.findInactiveById(1));
    }

    @Test
    void findInactiveById_SaleShouldNotBeReturned_WhenIdWasNotFound() {
        when(saleRepository.findByIdAndDeletedAtIsNotNull(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> saleProvider.findInactiveById(0));
    }

    @Test
    void findById_SaleShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleProvider.findById(1));
    }

    @Test
    void findById_SaleShouldNotBeReturned_WhenIdWasNotFound() {
        when(saleRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> saleProvider.findById(1));
    }
}