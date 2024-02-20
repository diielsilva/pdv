package com.todev.pdv.core.repositories;

import com.todev.pdv.factories.ProductFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void findByDeletedAtIsNull_ProductsShouldBeReturned_WhenHaveActiveProducts() {
        productRepository.save(ProductFactory.getProduct());
        var products = productRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findByDeletedAtIsNull_ProductsShouldNotBeReturned_WhenDoNotHaveActiveProducts() {
        productRepository.save(ProductFactory.getInactiveProduct());
        var products = productRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));
        assertTrue(products.isEmpty());
    }

    @Test
    void findByDeletedAtIsNotNull_ProductsShouldBeReturned_WhenHaveInactiveProducts() {
        productRepository.save(ProductFactory.getInactiveProduct());
        var products = productRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findByDeletedAtIsNotNull_ProductsShouldNotBeReturned_WhenDoNotHaveInactiveProducts() {
        productRepository.save(ProductFactory.getProduct());
        var products = productRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));
        assertTrue(products.isEmpty());
    }

    @Test
    void findByDescriptionContainingAndDeletedAtIsNull_ProductsShouldBeReturned_WhenHaveActiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getProduct());
        var products = productRepository.findByDescriptionContainingAndDeletedAtIsNull("Gal", PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findByDescriptionContainingAndDeletedAtIsNull_ProductsShouldNotBeReturned_WhenDoNotHaveActiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getInactiveProduct());
        var products = productRepository.findByDescriptionContainingAndDeletedAtIsNull("Gal", PageRequest.of(0, 5));
        assertTrue(products.isEmpty());
    }

    @Test
    void findByDescriptionContainingAndDeletedAtIsNotNull_ProductsShouldBeReturned_WhenHaveInactiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getInactiveProduct());
        var products = productRepository.findByDescriptionContainingAndDeletedAtIsNotNull("S", PageRequest.of(0, 5));
        assertEquals(1, products.getContent().size());
    }

    @Test
    void findByDescriptionContainingAndDeletedAtIsNotNull_ProductsShouldNotBeReturned_WhenDoNotHaveInactiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getProduct());
        var products = productRepository.findByDescriptionContainingAndDeletedAtIsNotNull("S20", PageRequest.of(0, 5));
        assertTrue(products.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNull_ProductShouldBeReturned_WhenIdWasFound() {
        var product = productRepository.save(ProductFactory.getProduct());
        var activeProductById = productRepository.findByIdAndDeletedAtIsNull(product.getId());
        assertTrue(activeProductById.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNull_ProductShouldNotBeReturned_WhenIdWasNotFound() {
        var inactiveProduct = productRepository.save(ProductFactory.getInactiveProduct());
        var activeProductById = productRepository.findByIdAndDeletedAtIsNull(inactiveProduct.getId());
        assertTrue(activeProductById.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_ProductShouldBeReturned_WhenIdWasFound() {
        var inactiveProduct = productRepository.save(ProductFactory.getInactiveProduct());
        var inactiveProductById = productRepository.findByIdAndDeletedAtIsNotNull(inactiveProduct.getId());
        assertTrue(inactiveProductById.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_ProductShouldNotBeReturned_WhenIdWasNotFound() {
        var activeProduct = productRepository.save(ProductFactory.getProduct());
        var inactiveProductById = productRepository.findByIdAndDeletedAtIsNotNull(activeProduct.getId());
        assertTrue(inactiveProductById.isEmpty());
    }

    @Test
    void findByDescription_ProductShouldBeReturned_WhenDescriptionWasFound() {
        productRepository.save(ProductFactory.getProduct());
        var product = productRepository.findByDescription("Samsung Galaxy S20");
        assertTrue(product.isPresent());
    }

    @Test
    void findByDescription_ProductShouldNotBeReturned_WhenDescriptionWasNotFound() {
        productRepository.save(ProductFactory.getProduct());
        var product = productRepository.findByDescription("Galaxy S20");
        assertTrue(product.isEmpty());
    }
}