package com.todev.pdv.core.providers.contracts;

import com.todev.pdv.core.models.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleProvider {
    Sale save(Sale sale);

    Page<Sale> findActive(Pageable pageable);

    Page<Sale> findInactive(Pageable pageable);

    List<Sale> findActiveByDate(LocalDateTime start, LocalDateTime end);

    List<Sale> findInactiveByDate(LocalDateTime start, LocalDateTime end);

    Sale findActiveById(Integer id);

    Sale findInactiveById(Integer id);

    Sale findById(Integer id);
}
