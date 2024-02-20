package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.providers.contracts.SaleItemProvider;
import com.todev.pdv.core.repositories.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaleItemProviderImpl implements SaleItemProvider {
    private final SaleItemRepository saleItemRepository;

    @Override
    public SaleItem save(SaleItem item) {
        return saleItemRepository.save(item);
    }

    @Override
    public List<SaleItem> findBySaleId(Integer saleId) {
        return saleItemRepository.findBySaleId(saleId);
    }
}
