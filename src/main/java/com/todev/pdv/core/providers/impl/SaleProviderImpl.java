package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.exceptions.ModelNotFoundException;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.providers.contracts.SaleProvider;
import com.todev.pdv.core.repositories.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SaleProviderImpl implements SaleProvider {
    private final SaleRepository saleRepository;
    private static final String ERROR_MESSAGE = "A venda: %s não foi encontrada!";

    @Override
    public Sale save(Sale sale) {
        return saleRepository.save(sale);
    }

    @Override
    public Page<Sale> findActive(Pageable pageable) {
        return saleRepository.findByDeletedAtIsNull(pageable);
    }

    @Override
    public Page<Sale> findInactive(Pageable pageable) {
        return saleRepository.findByDeletedAtIsNotNull(pageable);
    }

    @Override
    public List<Sale> findActiveByDate(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end);
    }

    @Override
    public List<Sale> findInactiveByDate(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByCreatedAtBetweenAndDeletedAtIsNotNull(start, end);
    }

    @Override
    public Sale findActiveById(Integer id) {
        return saleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public Sale findInactiveById(Integer id) {
        return saleRepository
                .findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public Sale findById(Integer id) {
        return saleRepository
                .findById(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }
}
