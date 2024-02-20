package com.todev.pdv.core.services.contracts;

import com.todev.pdv.common.dtos.ProductRequest;
import com.todev.pdv.common.dtos.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse save(ProductRequest requestDTO);

    Page<ProductResponse> findActive(Pageable pageable);

    Page<ProductResponse> findInactive(Pageable pageable);

    Page<ProductResponse> findActiveByDescriptionContaining(String description, Pageable pageable);

    Page<ProductResponse> findInactiveByDescriptionContaining(String description, Pageable pageable);

    ProductResponse findActiveById(Integer id);

    ProductResponse findInactiveById(Integer id);

    ProductResponse update(Integer id, ProductRequest requestDTO);

    void delete(Integer id);

    void reactivate(Integer id);
}
