package com.todev.pdv.core.repositories;

import com.todev.pdv.core.models.User;
import com.todev.pdv.factories.SaleFactory;
import com.todev.pdv.factories.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
class SaleRepositoryTest {
    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUpUserRepository() {
        user = userRepository.save(UserFactory.getSeller());
    }

    @AfterEach
    void tearDown() {
        saleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByDeletedAtIsNull_SalesShouldBeReturned_WhenHaveActiveSales() {
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var sales = saleRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));
        assertEquals(1, sales.getContent().size());
    }

    @Test
    void findByDeletedAtIsNull_SalesShouldNotBeReturned_WhenDoNotHaveActiveSales() {
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var sales = saleRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));
        assertTrue(sales.isEmpty());
    }

    @Test
    void findByDeletedAtIsNotNull_SalesShouldBeReturned_WhenHaveInactiveSales() {
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var sales = saleRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));
        assertEquals(1, sales.getContent().size());
    }

    @Test
    void findByDeletedAtIsNotNull_SalesShouldNotBeReturned_WhenDoNotHaveInactiveSales() {
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var sales = saleRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));
        assertTrue(sales.isEmpty());
    }

    @Test
    void findByCreatedAtBetweenAndDeletedAtIsNull_SalesShouldBeReturned_WhenHaveActiveSalesWithSelectedDate() {
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        var end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        var sales = saleRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end);
        assertEquals(1, sales.size());
    }

    @Test
    void findByCreatedAtBetweenAndDeletedAtIsNull_SalesShouldNotBeReturned_WhenDoNotHaveActiveSalesWithSelectedDate() {
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        var end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(0);
        var sales = saleRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end);
        assertTrue(sales.isEmpty());
    }

    @Test
    void findByCreatedAtBetweenAndDeletedAtIsNotNull_SalesShouldBeReturned_WhenHaveInactiveSalesWithSelectedDate() {
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        var end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        var sales = saleRepository.findByCreatedAtBetweenAndDeletedAtIsNotNull(start, end);
        assertEquals(1, sales.size());
    }

    @Test
    void findByCreatedAtBetweenAndDeletedAtIsNotNull_SalesShouldNotBeReturned_WhenDoNotHaveInactiveSalesWithSelectedDate() {
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        var end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        var sales = saleRepository.findByCreatedAtBetweenAndDeletedAtIsNotNull(start, end);
        assertTrue(sales.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNull_SaleShouldBeReturned_WhenIdWasFound() {
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var activeSaleById = saleRepository.findByIdAndDeletedAtIsNull(sale.getId());
        assertTrue(activeSaleById.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNull_SaleShouldNotBeReturned_WhenIdWasNotFound() {
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var activeSaleById = saleRepository.findByIdAndDeletedAtIsNull(sale.getId());
        assertTrue(activeSaleById.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_SaleShouldBeReturned_WhenIdWasFound() {
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var inactiveSaleById = saleRepository.findByIdAndDeletedAtIsNotNull(sale.getId());
        assertTrue(inactiveSaleById.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_SaleShouldNotBeReturned_WhenIdWasNotFound() {
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var inactiveSaleById = saleRepository.findByIdAndDeletedAtIsNotNull(sale.getId());
        assertTrue(inactiveSaleById.isEmpty());
    }
}