package com.todev.pdv.core.repositories;

import com.todev.pdv.core.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
    Page<Product> findByDeletedAtIsNull(Pageable pageable);

    Page<Product> findByDeletedAtIsNotNull(Pageable pageable);

    Page<Product> findByDescriptionContainingAndDeletedAtIsNull(String description, Pageable pageable);

    Page<Product> findByDescriptionContainingAndDeletedAtIsNotNull(String description, Pageable pageable);

    Optional<Product> findByIdAndDeletedAtIsNull(Integer id);

    Optional<Product> findByIdAndDeletedAtIsNotNull(Integer id);

    Optional<Product> findByDescription(String description);
}
