package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.exceptions.ModelNotFoundException;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.repositories.ProductRepository;
import com.todev.pdv.factories.ProductFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;

@ExtendWith(SpringExtension.class)
class ProductProviderImplTest {
    @InjectMocks
    private ProductProviderImpl productProvider;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUpProductRepository() {
        when(productRepository.save(any(Product.class)))
                .thenReturn(ProductFactory.getSavedProduct());

        when(productRepository.findByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getSavedProduct())));

        when(productRepository.findByDeletedAtIsNotNull(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getInactiveSavedProduct())));

        when(productRepository.findByDescriptionContainingAndDeletedAtIsNull(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getSavedProduct())));

        when(productRepository.findByDescriptionContainingAndDeletedAtIsNotNull(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getInactiveSavedProduct())));

        when(productRepository.findByIdAndDeletedAtIsNull(anyInt()))
                .thenReturn(Optional.of(ProductFactory.getSavedProduct()));

        when(productRepository.findByIdAndDeletedAtIsNotNull(anyInt()))
                .thenReturn(Optional.of(ProductFactory.getInactiveSavedProduct()));

        when(productRepository.findById(anyInt()))
                .thenReturn(Optional.of(ProductFactory.getSavedProduct()));

        when(productRepository.findByDescription(anyString()))
                .thenReturn(Optional.of(ProductFactory.getSavedProduct()));
    }

    @Test
    void save_ProductShouldBeSaved_WhenValidProductWasReceived() {
        assertDoesNotThrow(() -> productProvider.save(ProductFactory.getProduct()));
    }

    @Test
    void findActive_ProductsShouldBeReturned_WhenHaveActiveProducts() {
        var products = productProvider.findActive(PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findInactive_ProductsShouldBeReturned_WhenHaveInactiveProducts() {
        var products = productProvider.findInactive(PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findActiveByDescriptionContaining_ProductsShouldBeReturned_WhenHaveActiveProductsWithDescriptionContaining() {
        var products = productProvider.findActiveByDescriptionContaining("Gal", PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findInactiveByDescriptionContaining_ProductsShouldBeReturned_WhenHaveInactiveProductsWithDescriptionContaining() {
        var products = productProvider.findInactiveByDescriptionContaining("Gal", PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findActiveById_ProductShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> productProvider.findActiveById(1));
    }

    @Test
    void findActiveById_ProductShouldNotBeReturned_WhenIdWasNotFound() {
        when(productRepository.findByIdAndDeletedAtIsNull(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> productProvider.findActiveById(1));
    }

    @Test
    void findInactiveById_ProductShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> productProvider.findInactiveById(1));
    }

    @Test
    void findInactiveById_ProductShouldNotBeReturned_WhenIdWasNotFound() {
        when(productRepository.findByIdAndDeletedAtIsNotNull(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> productProvider.findInactiveById(0));
    }

    @Test
    void findById_ProductShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> productProvider.findById(1));
    }

    @Test
    void findById_ProductShouldNotBeReturned_WhenIdWasNotFound() {
        when(productRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> productProvider.findById(0));
    }

    @Test
    void findByDescription_ProductShouldBeReturned_WhenDescriptionWasFound() {
        var productByDescription = productProvider.findByDescription("Samsung Galaxy S20");
        assertTrue(productByDescription.isPresent());
    }
}