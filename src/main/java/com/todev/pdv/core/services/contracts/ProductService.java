package com.todev.pdv.core.services.contracts;

import com.todev.pdv.common.dtos.ProductRequestBody;
import com.todev.pdv.common.dtos.ProductResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseBody save(ProductRequestBody requestDTO);

    Page<ProductResponseBody> findActive(Pageable pageable);

    Page<ProductResponseBody> findInactive(Pageable pageable);

    Page<ProductResponseBody> findActiveByDescriptionContaining(String description, Pageable pageable);

    Page<ProductResponseBody> findInactiveByDescriptionContaining(String description, Pageable pageable);

    ProductResponseBody findActiveById(Integer id);

    ProductResponseBody findInactiveById(Integer id);

    ProductResponseBody update(Integer id, ProductRequestBody requestDTO);

    void delete(Integer id);

    void reactivate(Integer id);
}
