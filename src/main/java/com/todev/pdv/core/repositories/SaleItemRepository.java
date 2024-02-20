package com.todev.pdv.core.repositories;

import com.todev.pdv.core.models.SaleItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends CrudRepository<SaleItem, Integer> {
    List<SaleItem> findBySaleId(Integer saleId);
}
