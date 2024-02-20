package com.todev.pdv.core.repositories;

import com.todev.pdv.core.models.Sale;
import com.todev.pdv.factories.ProductFactory;
import com.todev.pdv.factories.SaleFactory;
import com.todev.pdv.factories.SaleItemFactory;
import com.todev.pdv.factories.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
class SaleItemRepositoryTest {
    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private Sale sale;

    @BeforeEach
    void setUp() {
        var product = productRepository.save(ProductFactory.getProduct());
        var user = userRepository.save(UserFactory.getSeller());
        sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        sale = saleRepository.save(sale);
        var saleItem = SaleItemFactory.getSaleItem();
        saleItem.setSaleId(sale.getId());
        saleItem.setProductId(product.getId());
        saleItemRepository.save(saleItem);
    }

    @AfterEach
    void tearDown() {
        saleItemRepository.deleteAll();
        saleRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void findBySaleId_SaleItemsShouldBeReturned_WhenIdWasFound() {
        var saleItems = saleItemRepository.findBySaleId(sale.getId());
        assertEquals(1, saleItems.size());
    }

    @Test
    void findBySaleId_SaleItemsShouldNotBeReturned_WhenIdWasNotFound() {
        var saleItems = saleItemRepository.findBySaleId(0);
        assertTrue(saleItems.isEmpty());
    }
}