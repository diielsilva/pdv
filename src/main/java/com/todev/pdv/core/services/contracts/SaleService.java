package com.todev.pdv.core.services.contracts;

import com.todev.pdv.common.dtos.SaleDetailsResponse;
import com.todev.pdv.common.dtos.SaleRequest;
import com.todev.pdv.common.dtos.SaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleService {
    SaleResponse save(String onlineUser, SaleRequest requestDTO);

    Page<SaleResponse> findActive(Pageable pageable);

    Page<SaleResponse> findInactive(Pageable pageable);

    List<SaleResponse> findActiveByDate(LocalDateTime date);

    List<SaleResponse> findInactiveByDate(LocalDateTime date);

    SaleResponse findActiveById(Integer id);

    SaleResponse findInactiveById(Integer id);

    SaleDetailsResponse details(Integer id);

    void delete(Integer id);

    void reactivate(Integer id);
}
