package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.ProductRequest;
import com.todev.pdv.common.dtos.ProductResponse;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.exceptions.ConstraintConflictException;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.services.contracts.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductProvider productProvider;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public ProductResponse save(ProductRequest requestDTO) {
        var product = modelMapper.toModel(requestDTO);

        if (isDescriptionInUse(product.getDescription())) {
            throw new ConstraintConflictException(String.format("A descrição: %s já está em uso!", product.getDescription()));
        }

        product.setCreatedAt(LocalDateTime.now());
        productProvider.save(product);
        return modelMapper.toDTO(product);
    }

    @Override
    public Page<ProductResponse> findActive(Pageable pageable) {
        var products = productProvider.findActive(pageable);
        return products.map(modelMapper::toDTO);
    }

    @Override
    public Page<ProductResponse> findInactive(Pageable pageable) {
        var products = productProvider.findInactive(pageable);
        return products.map(modelMapper::toDTO);
    }

    @Override
    public Page<ProductResponse> findActiveByDescriptionContaining(String description, Pageable pageable) {
        var products = productProvider.findActiveByDescriptionContaining(description, pageable);
        return products.map(modelMapper::toDTO);
    }

    @Override
    public Page<ProductResponse> findInactiveByDescriptionContaining(String description, Pageable pageable) {
        var products = productProvider.findInactiveByDescriptionContaining(description, pageable);
        return products.map(modelMapper::toDTO);
    }

    @Override
    public ProductResponse findActiveById(Integer id) {
        var product = productProvider.findActiveById(id);
        return modelMapper.toDTO(product);
    }

    @Override
    public ProductResponse findInactiveById(Integer id) {
        var product = productProvider.findInactiveById(id);
        return modelMapper.toDTO(product);
    }

    @Transactional
    @Override
    public ProductResponse update(Integer id, ProductRequest requestDTO) {
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
        return modelMapper.toDTO(savedProduct);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        var product = productProvider.findActiveById(id);
        product.setDeletedAt(LocalDateTime.now());
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
