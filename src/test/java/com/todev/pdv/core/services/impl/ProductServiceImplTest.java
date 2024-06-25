package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.ProductRequest;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.exceptions.ConstraintConflictException;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.providers.contracts.ProductProvider;
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
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductProvider productProvider;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUpProductProvider() {
        when(productProvider.save(any(Product.class)))
                .thenReturn(ProductFactory.getSavedProduct());

        when(productProvider.findActive(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getSavedProduct())));

        when(productProvider.findInactive(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getInactiveSavedProduct())));

        when(productProvider.findActiveByDescriptionContaining(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getSavedProduct())));

        when(productProvider.findInactiveByDescriptionContaining(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ProductFactory.getInactiveSavedProduct())));

        when(productProvider.findActiveById(anyInt()))
                .thenReturn(ProductFactory.getSavedProduct());

        when(productProvider.findInactiveById(anyInt()))
                .thenReturn(ProductFactory.getInactiveSavedProduct());

        when(productProvider.findByDescription(anyString()))
                .thenReturn(Optional.empty());
    }

    @BeforeEach
    void setUpModelMapper() {
        when(modelMapper.toModel(any(ProductRequest.class)))
                .thenReturn(ProductFactory.getProduct());

        when(modelMapper.toDTO(any(Product.class)))
                .thenReturn(ProductFactory.getResponseDTO());
    }

    @Test
    void save_ProductShouldBeSaved_WhenValidProductWasReceived() {
        assertDoesNotThrow(() -> productService.save(ProductFactory.getRequestDTO()));
    }

    @Test
    void save_ProductShouldNotBeSave_WhenDescriptionIsInUse() {
        when(productProvider.findByDescription(anyString()))
                .thenReturn(Optional.of(ProductFactory.getSavedProduct()));
        var product = ProductFactory.getRequestDTO();
        assertThrows(ConstraintConflictException.class, () -> productService.save(product));
    }

    @Test
    void findActive_ProductsShouldBeReturned_WhenHaveActiveProducts() {
        var products = productService.findActive(PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findInactive_ProductsShouldBeReturned_WhenHaveInactiveProducts() {
        var products = productService.findInactive(PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findActiveByNameContaining_ProductsShouldBeReturned_WhenHaveActiveProductsWithDescriptionContaining() {
        var products = productService.findActiveByDescriptionContaining("Gal", PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findInactiveByDescriptionContaining_ProductsShouldBeReturned_WhenHaveInactiveProductsWithDescriptionContaining() {
        var products = productService.findInactiveByDescriptionContaining("Gal", PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findActiveById_ProductShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> productService.findActiveById(1));
    }

    @Test
    void findInactiveById_ProductShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> productService.findInactiveById(1));
    }

    @Test
    void update_ProductShouldBeUpdated_WhenValidProductWasReceived() {
        var product = ProductFactory.getRequestDTO();
        assertDoesNotThrow(() -> productService.update(1, product));
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenDescriptionIsInUseByAnotherProduct() {
        when(productProvider.findByDescription(anyString()))
                .thenReturn(Optional.of(ProductFactory.getSavedProduct()));
        when(modelMapper.toModel(any(ProductRequest.class)))
                .thenReturn(new Product(null, "iPhone XR", 10, 1800.00, null, null));
        var product = ProductFactory.getRequestDTO();
        assertThrows(ConstraintConflictException.class, () -> productService.update(1, product));
    }

    @Test
    void delete_ProductShouldBeDeleted_WhenIdWasFound() {
        assertDoesNotThrow(() -> productService.delete(1));
    }

    @Test
    void reactivate_ProductShouldBeReactivated_WhenIdWasFound() {
        assertDoesNotThrow(() -> productService.reactivate(1));
    }
}