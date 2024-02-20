package com.todev.pdv.core.providers.contracts;


import com.todev.pdv.core.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductProvider {
    Product save(Product product);

    Page<Product> findActive(Pageable pageable);

    Page<Product> findInactive(Pageable pageable);

    Page<Product> findActiveByDescriptionContaining(String description, Pageable pageable);

    Page<Product> findInactiveByDescriptionContaining(String description, Pageable pageable);

    Product findActiveById(Integer id);

    Product findInactiveById(Integer id);

    Product findById(Integer id);

    Optional<Product> findByDescription(String description);
}
