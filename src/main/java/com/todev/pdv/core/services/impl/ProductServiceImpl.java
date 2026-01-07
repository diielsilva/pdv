package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.ProductRequestBody;
import com.todev.pdv.common.dtos.ProductResponseBody;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.exceptions.ConstraintConflictException;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.services.contracts.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductProvider productProvider;
    private final ModelMapper modelMapper;
    private static final String ZONE_ID = "America/Sao_Paulo";

    public ProductServiceImpl(ProductProvider productProvider, ModelMapper modelMapper) {
        this.productProvider = productProvider;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public ProductResponseBody save(ProductRequestBody requestDTO) {
        var product = modelMapper.toModel(requestDTO);

        if (isDescriptionInUse(product.getDescription())) {
            throw new ConstraintConflictException(String.format("A descrição: %s já está em uso!", product.getDescription()));
        }

        product.setCreatedAt(LocalDateTime.now(ZoneId.of(ZONE_ID)));
        productProvider.save(product);
        return modelMapper.toResponseBody(product);
    }

    @Override
    public Page<ProductResponseBody> findActive(Pageable pageable) {
        var products = productProvider.findActive(pageable);
        return products.map(modelMapper::toResponseBody);
    }

    @Override
    public Page<ProductResponseBody> findInactive(Pageable pageable) {
        var products = productProvider.findInactive(pageable);
        return products.map(modelMapper::toResponseBody);
    }

    @Override
    public Page<ProductResponseBody> findActiveByDescriptionContaining(String description, Pageable pageable) {
        var products = productProvider.findActiveByDescriptionContaining(description, pageable);
        return products.map(modelMapper::toResponseBody);
    }

    @Override
    public Page<ProductResponseBody> findInactiveByDescriptionContaining(String description, Pageable pageable) {
        var products = productProvider.findInactiveByDescriptionContaining(description, pageable);
        return products.map(modelMapper::toResponseBody);
    }

    @Override
    public ProductResponseBody findActiveById(Integer id) {
        var product = productProvider.findActiveById(id);
        return modelMapper.toResponseBody(product);
    }

    @Override
    public ProductResponseBody findInactiveById(Integer id) {
        var product = productProvider.findInactiveById(id);
        return modelMapper.toResponseBody(product);
    }

    @Transactional
    @Override
    public ProductResponseBody update(Integer id, ProductRequestBody requestDTO) {
        var savedProduct = productProvider.findActiveById(id);
        var product = modelMapper.toModel(requestDTO);
        var newDescription = product.getDescription();

        if (isDescriptionInUse(newDescription) && !newDescription.equals(savedProduct.getDescription())) {
            throw new ConstraintConflictException(String.format("A descrição: %s já está em uso!", newDescription));
        }

        savedProduct.setDescription(product.getDescription());
        savedProduct.setAmount(product.getAmount());
        savedProduct.setPrice(product.getPrice());
        productProvider.save(savedProduct);
        return modelMapper.toResponseBody(savedProduct);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        var product = productProvider.findActiveById(id);
        product.setDeletedAt(LocalDateTime.now(ZoneId.of(ZONE_ID)));
        productProvider.save(product);
    }

    @Transactional
    @Override
    public void reactivate(Integer id) {
        var product = productProvider.findInactiveById(id);
        product.setDeletedAt(null);
        productProvider.save(product);
    }

    private boolean isDescriptionInUse(String description) {
        return productProvider
                .findByDescription(description)
                .isPresent();
    }
}
