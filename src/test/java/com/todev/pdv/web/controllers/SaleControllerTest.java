package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.common.dtos.SaleDetailsResponse;
import com.todev.pdv.common.dtos.SaleResponse;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.repositories.ProductRepository;
import com.todev.pdv.core.repositories.SaleItemRepository;
import com.todev.pdv.core.repositories.SaleRepository;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.*;
import com.todev.pdv.helpers.LoginHelper;
import com.todev.pdv.wrappers.PageableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class SaleControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private PasswordEncoder BCryptEncoder;

    private Product product;

    @BeforeEach
    void setUpProductRepository() {
        product = productRepository.save(ProductFactory.getProduct());
    }

    @AfterEach
    void tearDown() {
        saleItemRepository.deleteAll();
        saleRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void save_SaleShouldBeSaved_WhenValidSaleWasReceivedAndOnlineUseIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithoutIssues(product.getId()), httpHeaders),
                SaleResponse.class
        );
        var productById = productRepository.findByIdAndDeletedAtIsNull(product.getId());
        assertAll(() -> {
            assertEquals(CREATED, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3501.8, httpResponse.getBody().total());
            assertTrue(productById.isPresent());
            assertEquals(8, productById.get().getAmount());
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasDuplicatedProducts() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithDuplicatedItems(product.getId()), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda não pode ter itens duplicados!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasProductsWithNotEnoughStock() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithNotEnoughStockItems(product.getId()), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(String.format("O produto: %s não possui estoque suficiente!", product.getId()), httpResponse.getBody().message());
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleDoesNotHaveItems() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithoutItems(), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("A venda deve conter ao menos um item!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasAnInvalidPaymentMethod() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithInvalidPaymentMethod(product.getId()), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("O método de pagamento é inválido!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasAnInvalidDiscount() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithDiscountEqualsToMinusOne(product.getId()), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("O desconto deve ser maior ou igual a zero!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasItemsWithAmountEqualsToZero() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithItemsWithAmountEqualsToZero(product.getId()), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("A quantidade do item deve ser maior ou igual a um!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasEmptyValues() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales",
                POST,
                new HttpEntity<>(SaleFactory.getSaleWithoutValues(), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(4, httpResponse.getBody().details().size());
        });
    }

    @Test
    void findActive_SalesShouldBeReturned_WhenHaveActiveSales() {
        var user = UserFactory.getSeller();
        var sale = SaleFactory.getSale();
        userRepository.save(user);
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange(
                "/sales/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponse>>() {
                }
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().getContent().size());
            assertNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findActive_SalesShouldNotBeReturned_WhenDoNotHaveActiveSales() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponse>>() {
                }
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactive_SalesShouldBeReturned_WhenHaveInactiveSalesAndOnlineUserIsAnAdmin() {
        var user = UserFactory.getSeller();
        var sale = SaleFactory.getInactiveSale();
        userRepository.save(user);
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange(
                "/sales/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponse>>() {
                }
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().getContent().size());
            assertNotNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findInactive_SalesShouldNotBeReturned_WhenDoNotHaveInactiveSalesAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange(
                "/sales/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponse>>() {
                }
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactive_SalesShouldNotBeReturned_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void details_SaleDetailsShouldBeReturned_WhenIdWasFound() {
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var item = SaleItemFactory.getSaleItem();
        item.setSaleId(sale.getId());
        item.setProductId(product.getId());
        saleItemRepository.save(item);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/sales/details/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                SaleDetailsResponse.class,
                sale.getId());
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("Seller", httpResponse.getBody().sellerName());
            assertEquals(1, httpResponse.getBody().items().size());
            assertEquals("Samsung Galaxy S20", httpResponse.getBody().items().get(0).productDescription());
            assertNotNull(httpResponse.getBody().items().get(0).id());
            assertEquals(1, httpResponse.getBody().items().get(0).amount());
            assertEquals(1750.90, httpResponse.getBody().items().get(0).price());
        });
    }

    @Test
    void details_SaleDetailsShouldNotBeReturned_WhenIdWasNotFound() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales/details/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1);
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda: 1 não foi encontrada!", httpResponse.getBody().message());
        });
    }

    @Test
    void findActiveBySelectedDate_SalesShouldBeReturn_WhenHaveActiveSalesWithSelectedDate() {
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange(
                "/sales/active/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponse>>() {
                },
                LocalDateTime.now()
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().size());
            assertNull(httpResponse.getBody().get(0).deletedAt());
        });
    }

    @Test
    void findActiveBySelectedDate_SalesShouldNotBeReturned_WhenDoNotHaveActiveSalesWithSelectedDate() {
        var selectedDate = LocalDateTime.now().plusDays(1L);
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange(
                "/sales/active/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponse>>() {
                },
                selectedDate
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactiveBySelectedDate_SalesShouldBeReturned_WhenHaveInactiveSalesWithSelectedDateAndOnlineUserIsAnAdmin() {
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/sales/inactive/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponse>>() {
                },
                LocalDateTime.now()
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().size());
            assertNotNull(httpResponse.getBody().get(0).deletedAt());
        });
    }

    @Test
    void findInactiveBySelectedDate_SalesShouldNotBeReturned_WhenDoNotInactiveSalesWithSelectedDateAndOnlineUserIsAManager() {
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getInactiveSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange(
                "/sales/inactive/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponse>>() {
                },
                LocalDateTime.now().plusDays(1L)
        );
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactiveBySelectedDate_SalesShouldNotBeReturned_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales/inactive/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                LocalDateTime.now()
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void delete_SaleShouldBeDeleted_WhenIdWasFoundAndOnlineUserIsAManager() {
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getSale();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        var saleItem = SaleItemFactory.getSaleItem();
        saleItem.setSaleId(sale.getId());
        saleItem.setProductId(product.getId());
        saleItemRepository.save(saleItem);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/sales/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class,
                sale.getId()
        );
        assertAll(() -> {
            var saleItems = saleItemRepository.findBySaleId(sale.getId());
            assertEquals(NO_CONTENT, httpResponse.getStatusCode());
            assertEquals(1, saleItems.size());
            assertNotNull(saleItems.get(0).getDeletedAt());
        });
    }

    @Test
    void delete_SaleShouldNotBeDeleted_WhenIdWasNotFoundAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/sales/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda: 1 não foi encontrada!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_SaleShouldNotBeDeleted_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void reactive_SaleShouldBeReactivated_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getInactiveSale();
        var item = SaleItemFactory.getSaleItem();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        item.setSaleId(sale.getId());
        item.setProductId(product.getId());
        saleItemRepository.save(item);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/sales/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                Void.class,
                sale.getId()
        );
        assertAll(() -> {
            var saleItems = saleItemRepository.findBySaleId(sale.getId());
            assertEquals(NO_CONTENT, httpResponse.getStatusCode());
            assertEquals(1, saleItems.size());
            assertNull(saleItems.get(0).getDeletedAt());
        });
    }

    @Test
    void reactivate_SaleShouldNotBeReactivated_WhenDoesNotHaveEnoughStockAndOnlineUserIsAManager() {
        var user = userRepository.save(UserFactory.getSeller());
        var sale = SaleFactory.getInactiveSale();
        var item = SaleItemFactory.getSaleItem();
        sale.setUserId(user.getId());
        saleRepository.save(sale);
        item.setSaleId(sale.getId());
        item.setProductId(product.getId());
        item.setAmount(100);
        saleItemRepository.save(item);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/sales/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                sale.getId()
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(String.format("O produto: %s não possui estoque suficiente!", product.getId()), httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_SaleShouldNotBeReactivated_WhenIdWasNotFoundAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/sales/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 1);
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda: 1 não foi encontrada!", httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_SaleShouldNotBeReactivated_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/sales/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

}