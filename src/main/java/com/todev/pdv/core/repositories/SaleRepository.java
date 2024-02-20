package com.todev.pdv.core.repositories;

import com.todev.pdv.core.models.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends CrudRepository<Sale, Integer> {
    Page<Sale> findByDeletedAtIsNull(Pageable pageable);

    Page<Sale> findByDeletedAtIsNotNull(Pageable pageable);

    List<Sale> findByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime start, LocalDateTime end);

    List<Sale> findByCreatedAtBetweenAndDeletedAtIsNotNull(LocalDateTime start, LocalDateTime end);

    Optional<Sale> findByIdAndDeletedAtIsNull(Integer id);

    Optional<Sale> findByIdAndDeletedAtIsNotNull(Integer id);
}
