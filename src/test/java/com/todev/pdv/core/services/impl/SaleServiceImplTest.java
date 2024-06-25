package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.SaleItemRequest;
import com.todev.pdv.common.dtos.SaleRequest;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.exceptions.DuplicatedItemException;
import com.todev.pdv.core.exceptions.NotEnoughStockException;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.providers.contracts.SaleItemProvider;
import com.todev.pdv.core.providers.contracts.SaleProvider;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.factories.ProductFactory;
import com.todev.pdv.factories.SaleFactory;
import com.todev.pdv.factories.SaleItemFactory;
import com.todev.pdv.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;

@ExtendWith(SpringExtension.class)
class SaleServiceImplTest {
    @InjectMocks
    private SaleServiceImpl saleService;

    @Mock
    private SaleProvider saleProvider;

    @Mock
    private SaleItemProvider saleItemProvider;

    @Mock
    private ProductProvider productProvider;

    @Mock
    private UserProvider userProvider;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUpSaleProvider() {
        when(saleProvider.save(any(Sale.class)))
                .thenReturn(SaleFactory.getSavedSale());

        when(saleProvider.findActive(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SaleFactory.getSavedSale())));

        when(saleProvider.findInactive(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(SaleFactory.getInactiveSavedSale())));

        when(saleProvider.findActiveByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(SaleFactory.getSavedSale()));

        when(saleProvider.findInactiveByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(SaleFactory.getInactiveSavedSale()));

        when(saleProvider.findActiveById(anyInt()))
                .thenReturn(SaleFactory.getSavedSale());

        when(saleProvider.findInactiveById(anyInt()))
                .thenReturn(SaleFactory.getInactiveSavedSale());

        when(saleProvider.findById(anyInt()))
                .thenReturn(SaleFactory.getSavedSale());
    }

    @BeforeEach
    void setUpSaleItemProvider() {
        when(saleItemProvider.save(any(SaleItem.class)))
                .thenReturn(SaleItemFactory.getSavedSaleItem());

        when(saleItemProvider.findBySaleId(anyInt()))
                .thenReturn(List.of(SaleItemFactory.getSavedSaleItem()));
    }

    @BeforeEach
    void setUpProductProvider() {
        when(productProvider.findActiveById(anyInt()))
                .thenReturn(ProductFactory.getSavedProduct());

        when(productProvider.findById(anyInt()))
                .thenReturn(ProductFactory.getSavedProduct());
    }

    @BeforeEach
    void setUpUserProvider() {
        when(userProvider.findActiveByLogin(anyString()))
                .thenReturn(UserFactory.getSavedSeller());

        when(userProvider.findById(anyInt()))
                .thenReturn(UserFactory.getSavedSeller());
    }

    @BeforeEach
    void setUpModelMapper() {
        when(modelMapper.toModel(any(SaleRequest.class)))
                .thenReturn(SaleFactory.getSale());

        when(modelMapper.toModel(any(SaleItemRequest.class)))
                .thenReturn(SaleItemFactory.getSaleItem());

        when(modelMapper.toDTO(any(Sale.class)))
                .thenReturn(SaleFactory.getResponseDTO());
    }

    @Test
    void save_SaleShouldBeSaved_WhenValidSaleWasReceived() {
        assertDoesNotThrow(() -> saleService.save("seller", SaleFactory.getRequestDTO(1)));
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasDuplicatedProducts() {
        var sale = SaleFactory.getSaleWithDuplicatedItems(1);
        assertThrows(DuplicatedItemException.class, () -> saleService.save("seller", sale));
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasItemsWithNotEnoughStock() {
        var item = SaleItemFactory.getSaleItem();
        item.setAmount(100);
        when(modelMapper.toModel(any(SaleItemRequest.class)))
                .thenReturn(item);
        var sale = SaleFactory.getSaleWithNotEnoughStockItems(1);
        assertThrows(NotEnoughStockException.class, () -> saleService.save("seller", sale));
    }

    @Test
    void findActive_SalesShouldBeReturned_WhenHaveActiveSales() {
        var sales = saleService.findActive(PageRequest.of(0, 5));
        assertEquals(1, sales.getContent().size());
    }

    @Test
    void findInactive_SalesShouldBeReturned_WhenHaveInactiveSales() {
        var sales = saleService.findInactive(PageRequest.of(0, 5));
        assertEquals(1, sales.getContent().size());
    }

    @Test
    void findActiveByDate_SalesShouldBeReturned_WhenHaveActiveSalesWithSelectedDate() {
        var sales = saleService.findActiveByDate(LocalDateTime.now());
        assertEquals(1, sales.size());
    }

    @Test
    void findInactiveByDate_SalesShouldBeReturned_WhenHaveInactiveSalesWithSelectedDate() {
        var sales = saleService.findInactiveByDate(LocalDateTime.now());
        assertEquals(1, sales.size());
    }

    @Test
    void findActiveById_SaleShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleService.findActiveById(1));
    }

    @Test
    void findInactiveById_SaleShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleService.findInactiveById(1));
    }

    @Test
    void details_SaleDetailsShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleService.details(1));
    }

    @Test
    void delete_SaleShouldBeDeleted_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleService.delete(1));
    }

    @Test
    void reactivate_SaleShouldBeReactivated_WhenIdWasFound() {
        assertDoesNotThrow(() -> saleService.reactivate(1));
    }

    @Test
    void reactivate_SaleShouldNotBeReactivated_WhenProductDoesNotHaveEnoughStock() {
        var product = ProductFactory.getSavedProduct();
        product.setAmount(0);
        when(productProvider.findById(anyInt()))
                .thenReturn(product);
        assertThrows(NotEnoughStockException.class, () -> saleService.reactivate(1));
    }
}