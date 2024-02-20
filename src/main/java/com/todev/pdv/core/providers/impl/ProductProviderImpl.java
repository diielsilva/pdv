package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.exceptions.ModelNotFoundException;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductProviderImpl implements ProductProvider {
    private final ProductRepository productRepository;
    private static final String ERROR_MESSAGE = "O produto: %s não foi encontrado!";

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Page<Product> findActive(Pageable pageable) {
        return productRepository.findByDeletedAtIsNull(pageable);
    }

    @Override
    public Page<Product> findInactive(Pageable pageable) {
        return productRepository.findByDeletedAtIsNotNull(pageable);
    }

    @Override
    public Page<Product> findActiveByDescriptionContaining(String description, Pageable pageable) {
        return productRepository.findByDescriptionContainingAndDeletedAtIsNull(description, pageable);
    }

    @Override
    public Page<Product> findInactiveByDescriptionContaining(String description, Pageable pageable) {
        return productRepository.findByDescriptionContainingAndDeletedAtIsNotNull(description, pageable);
    }

    @Override
    public Product findActiveById(Integer id) {
        return productRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public Product findInactiveById(Integer id) {
        return productRepository
                .findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public Product findById(Integer id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public Optional<Product> findByDescription(String description) {
        return productRepository.findByDescription(description);
    }
}
