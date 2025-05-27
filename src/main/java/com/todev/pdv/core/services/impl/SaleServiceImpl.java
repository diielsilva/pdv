package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.SaleDetailsResponse;
import com.todev.pdv.common.dtos.SaleRequest;
import com.todev.pdv.common.dtos.SaleResponse;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.enums.DeliveryPlatform;
import com.todev.pdv.core.enums.OnlineSalePlatform;
import com.todev.pdv.core.exceptions.DuplicatedItemException;
import com.todev.pdv.core.exceptions.NotEnoughStockException;
import com.todev.pdv.core.models.OnlineSale;
import com.todev.pdv.core.models.OnlineSaleDelivery;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.providers.contracts.SaleItemProvider;
import com.todev.pdv.core.providers.contracts.SaleProvider;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.core.repositories.OnlineSaleDeliveryRepository;
import com.todev.pdv.core.repositories.OnlineSaleRepository;
import com.todev.pdv.core.services.contracts.SaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SaleServiceImpl implements SaleService {
    private final SaleProvider saleProvider;
    private final SaleItemProvider saleItemProvider;
    private final ProductProvider productProvider;
    private final UserProvider userProvider;
    private final ModelMapper modelMapper;
    private final OnlineSaleRepository onlineSaleRepository;
    private final OnlineSaleDeliveryRepository deliveryRepository;
    private static final String ZONE_ID = "America/Sao_Paulo";

    public SaleServiceImpl(SaleProvider saleProvider, SaleItemProvider saleItemProvider, ProductProvider productProvider, UserProvider userProvider, ModelMapper modelMapper, OnlineSaleRepository onlineSaleRepository, OnlineSaleDeliveryRepository deliveryRepository) {
        this.saleProvider = saleProvider;
        this.saleItemProvider = saleItemProvider;
        this.productProvider = productProvider;
        this.userProvider = userProvider;
        this.modelMapper = modelMapper;
        this.onlineSaleRepository = onlineSaleRepository;
        this.deliveryRepository = deliveryRepository;
    }

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
        sale.setCreatedAt(LocalDateTime.now(ZoneId.of(ZONE_ID)));
        saleProvider.save(sale);

        for (SaleItem item : items) {
            var product = productProvider.findActiveById(item.getProductId());

            if (item.getAmount() > product.getAmount()) {
                throw new NotEnoughStockException(String.format("O produto: %s não possui estoque suficiente!", product.getId()));
            }

            total += item.getAmount() * product.getPrice();
            product.setAmount(product.getAmount() - item.getAmount());
            item.setSaleId(sale.getId());
            item.setCreatedAt(LocalDateTime.now(ZoneId.of(ZONE_ID)));
            item.setPrice(product.getPrice());
            productProvider.save(product);
            saleItemProvider.save(item);
        }

        sale.setTotal(total);
        saleProvider.save(sale);

        return modelMapper.toDTO(sale);
    }

    @Override
    public SaleResponse saveOnline(String onlineUser, boolean hasDelivery, SaleRequest body) {
        SaleResponse sale = save(onlineUser, body);
        OnlineSale onlineSale = new OnlineSale(sale.id(), OnlineSalePlatform.WHATSAPP, sale.createdAt(), sale.deletedAt());
        onlineSaleRepository.save(onlineSale) ;

        if(hasDelivery) {
            OnlineSaleDelivery delivery =
                    new OnlineSaleDelivery(onlineSale.getSaleId(), DeliveryPlatform.DELIVERY_99,
                            6D, onlineSale.getCreatedAt(), onlineSale.getUpdatedAt());
            deliveryRepository.save(delivery);
        }

        return sale;
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

    @Override
    public void delete(Integer id) {
        var sale = saleProvider.findActiveById(id);
        var saleItems = saleItemProvider.findBySaleId(sale.getId());

        saleItems.forEach(item -> {
            var product = productProvider.findById(item.getProductId());
            product.setAmount(product.getAmount() + item.getAmount());
            item.setDeletedAt(LocalDateTime.now(ZoneId.of(ZONE_ID)));

            productProvider.save(product);
            saleItemProvider.save(item);
        });

        sale.setDeletedAt(LocalDateTime.now(ZoneId.of(ZONE_ID)));
        saleProvider.save(sale);
    }

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
