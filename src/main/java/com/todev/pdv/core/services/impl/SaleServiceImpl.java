package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.SaleDetailsResponse;
import com.todev.pdv.common.dtos.SaleRequest;
import com.todev.pdv.common.dtos.SaleResponse;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.exceptions.DuplicatedItemException;
import com.todev.pdv.core.exceptions.NotEnoughStockException;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.providers.contracts.SaleItemProvider;
import com.todev.pdv.core.providers.contracts.SaleProvider;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.core.services.contracts.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private final SaleProvider saleProvider;
    private final SaleItemProvider saleItemProvider;
    private final ProductProvider productProvider;
    private final UserProvider userProvider;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public SaleResponse save(String onlineUser, SaleRequest requestDTO) {
        var sale = modelMapper.toModel(requestDTO);
        var user = userProvider.findActiveByLogin(onlineUser);
        var items = requestDTO.items().stream().map(modelMapper::toModel).toList();
        var total = 0.0;

        if (hasDuplicatedProductIds(items)) {
            throw new DuplicatedItemException("A venda não pode ter itens duplicados!");
        }

        sale.setUserId(user.getId());
        sale.setTotal(total);
        sale.setCreatedAt(LocalDateTime.now());
        saleProvider.save(sale);

        for (SaleItem item : items) {
            var product = productProvider.findActiveById(item.getProductId());

            if (item.getAmount() > product.getAmount()) {
                throw new NotEnoughStockException(String.format("O produto: %s não possui estoque suficiente!", product.getId()));
            }

            total += item.getAmount() * product.getPrice();
            product.setAmount(product.getAmount() - item.getAmount());
            item.setSaleId(sale.getId());
            item.setCreatedAt(LocalDateTime.now());
            item.setPrice(product.getPrice());
            productProvider.save(product);
            saleItemProvider.save(item);
        }

        sale.setTotal(total);
        saleProvider.save(sale);

        return modelMapper.toDTO(sale);
    }

    @Override
    public Page<SaleResponse> findActive(Pageable pageable) {
        var sales = saleProvider.findActive(pageable);
        return sales.map(modelMapper::toDTO);
    }

    @Override
    public Page<SaleResponse> findInactive(Pageable pageable) {
        var sales = saleProvider.findInactive(pageable);
        return sales.map(modelMapper::toDTO);
    }

    @Override
    public List<SaleResponse> findActiveByDate(LocalDateTime date) {
        var start = date.withHour(0).withMinute(0).withSecond(0);
        var end = date.withHour(23).withMinute(59).withSecond(59);
        var sales = saleProvider.findActiveByDate(start, end);
        return sales.stream().map(modelMapper::toDTO).toList();
    }

    @Override
    public List<SaleResponse> findInactiveByDate(LocalDateTime date) {
        var start = date.withHour(0).withMinute(0).withSecond(0);
        var end = date.withHour(23).withMinute(59).withSecond(59);
        var sales = saleProvider.findInactiveByDate(start, end);
        return sales.stream().map(modelMapper::toDTO).toList();
    }

    @Override
    public SaleResponse findActiveById(Integer id) {
        var sale = saleProvider.findActiveById(id);
        return modelMapper.toDTO(sale);
    }

    @Override
    public SaleResponse findInactiveById(Integer id) {
        var sale = saleProvider.findInactiveById(id);
        return modelMapper.toDTO(sale);
    }

    @Override
    public SaleDetailsResponse details(Integer id) {
        var sale = saleProvider.findById(id);
        var user = userProvider.findById(sale.getUserId());
        var items = saleItemProvider.findBySaleId(sale.getId());
        var products = new ArrayList<Product>();

        items.forEach(item -> {
            var product = productProvider.findById(item.getProductId());
            products.add(product);
        });

        return modelMapper.toDTO(user, items, products);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        var sale = saleProvider.findActiveById(id);
        var saleItems = saleItemProvider.findBySaleId(sale.getId());

        saleItems.forEach(item -> {
            var product = productProvider.findById(item.getProductId());
            product.setAmount(product.getAmount() + item.getAmount());
            item.setDeletedAt(LocalDateTime.now());

            productProvider.save(product);
            saleItemProvider.save(item);
        });

        sale.setDeletedAt(LocalDateTime.now());
        saleProvider.save(sale);
    }

    @Transactional
    @Override
    public void reactivate(Integer id) {
        var sale = saleProvider.findInactiveById(id);
        var saleItems = saleItemProvider.findBySaleId(sale.getId());

        saleItems.forEach(item -> {
            var product = productProvider.findById(item.getProductId());

            if (item.getAmount() > product.getAmount()) {
                throw new NotEnoughStockException(String.format("O produto: %s não possui estoque suficiente!", product.getId()));
            }

            product.setAmount(product.getAmount() - item.getAmount());
            productProvider.save(product);

            item.setDeletedAt(null);
            saleItemProvider.save(item);
        });

        sale.setDeletedAt(null);

        saleProvider.save(sale);
    }

    private boolean hasDuplicatedProductIds(List<SaleItem> items) {
        for (int i = 0; i < items.size(); i++) {
            var productId = items.get(i).getProductId();

            for (int j = i + 1; j < items.size(); j++) {

                if (productId.equals(items.get(j).getProductId())) {
                    return true;
                }

            }
        }

        return false;
    }
}
