package com.todev.pdv.core.services.contracts;

import com.todev.pdv.common.dtos.SaleDetailsResponseBody;
import com.todev.pdv.common.dtos.SaleRequestBody;
import com.todev.pdv.common.dtos.SaleResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleService {
    SaleResponseBody save(String onlineUser, SaleRequestBody requestDTO);

    SaleResponseBody saveOnline(String onlineUser, boolean hasDelivery, SaleRequestBody body);

    Page<SaleResponseBody> findActive(Pageable pageable);

    Page<SaleResponseBody> findInactive(Pageable pageable);

    List<SaleResponseBody> findActiveByDate(LocalDateTime date);

    List<SaleResponseBody> findInactiveByDate(LocalDateTime date);

    SaleResponseBody findActiveById(Integer id);

    SaleResponseBody findInactiveById(Integer id);

    SaleDetailsResponseBody details(Integer id);

    void delete(Integer id);

    void reactivate(Integer id);
}
