package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.*;
import com.todev.pdv.core.models.OnlineSale;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.repositories.*;
import com.todev.pdv.factories.*;
import com.todev.pdv.helpers.SecurityHelper;
import com.todev.pdv.wrappers.PageableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class SaleControllerTest {
    @Autowired
    private TestRestTemplate apiClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private OnlineSaleRepository onlineSaleRepository;

    @Autowired
    private OnlineSaleDeliveryRepository deliveryRepository;

    private Product product;

    private Sale sale;

    private SaleItem saleItem;

    @BeforeEach
    void setUp() {
        product = productRepository.save(ProductFactory.getProduct());
        securityHelper.createUser(UserFactory.getAdmin());
        securityHelper.createUser(UserFactory.getManager());
        securityHelper.createUser(UserFactory.getSeller());
    }

    @AfterEach
    void tearDown() {
        saleItemRepository.deleteAll();
        deliveryRepository.deleteAll();
        onlineSaleRepository.deleteAll();
        saleRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void save_SaleShouldBeSaved() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = SaleFactory.getRequestDTO(product.getId());
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                SaleResponseBody.class
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
    void save_OnlineSaleWithoutDeliveryShouldBeSaved() {
        HttpHeaders requestHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        SaleRequestBody requestBody = SaleFactory.getRequestDTO(product.getId());
        var httpResponse = apiClient.exchange(
                "/sales/online?hasDelivery={hasDelivery}",
                POST,
                new HttpEntity<>(requestBody, requestHeaders),
                SaleResponseBody.class,
                false
        );

        Iterable<OnlineSale> onlineSales = onlineSaleRepository.findAll();

        assertAll(() -> {
            assertEquals(CREATED, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            onlineSales.forEach(onlineSale -> {
                assertEquals(onlineSale.getSaleId(), httpResponse.getBody().id());
            });
            assertEquals(1L, onlineSaleRepository.count());
            assertEquals(0L, deliveryRepository.count());
        });
    }

    @Test
    void save_OnlineSaleWithDeliveryShouldBeSaved() {
        HttpHeaders requestHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        SaleRequestBody requestBody = SaleFactory.getRequestDTO(product.getId());
        var httpResponse = apiClient.exchange(
                "/sales/online?hasDelivery={hasDelivery}",
                POST,
                new HttpEntity<>(requestBody, requestHeaders),
                SaleResponseBody.class,
                true
        );

        Iterable<OnlineSale> onlineSales = onlineSaleRepository.findAll();

        assertAll(() -> {
            assertEquals(CREATED, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            onlineSales.forEach(onlineSale -> {
                assertEquals(onlineSale.getSaleId(), httpResponse.getBody().id());
            });
            assertEquals(1L, onlineSaleRepository.count());
            assertEquals(1L, deliveryRepository.count());
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasDuplicatedProducts() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new SaleRequestBody("CARD", 0,
                List.of(new SaleItemRequestBody(product.getId(), 1),
                        new SaleItemRequestBody(product.getId(), 1)
                ));
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponseBody.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda não pode ter itens duplicados!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasProductsWithNotEnoughStock() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new SaleRequestBody("CARD", 0,
                List.of(new SaleItemRequestBody(product.getId(), 100)
                ));
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponseBody.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(String.format("O produto: %s não possui estoque suficiente!", product.getId()), httpResponse.getBody().message());
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleDoesNotHaveItems() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new SaleRequestBody("CARD", 0, List.of());
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponseBody.class
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
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new SaleRequestBody("CARDS", 0,
                List.of(new SaleItemRequestBody(product.getId(), 1)
                ));
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponseBody.class
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
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new SaleRequestBody("CARD", -1,
                List.of(new SaleItemRequestBody(product.getId(), 1)
                ));
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponseBody.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("O desconto deve ser maior ou igual a zero!",
                    httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasItemsWithAmountEqualsToZero() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new SaleRequestBody("CARD", 0,
                List.of(new SaleItemRequestBody(product.getId(), 0)));
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponseBody.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("A quantidade do item deve ser maior ou igual a um!",
                    httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_SaleShouldNotBeSaved_WhenReceivedSaleHasEmptyValues() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new SaleRequestBody(null, null, null);
        var httpResponse = apiClient.exchange("/sales",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponseBody.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(4, httpResponse.getBody().details().size());
        });
    }

    @Test
    void findActive_SalesShouldBeReturned_WhenHaveActiveSales() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange(
                "/sales/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponseBody>>() {
                }
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3, httpResponse.getBody().getContent().size());
            assertNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findActive_SalesShouldNotBeReturned_WhenDoNotHaveActiveSales() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/sales/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponseBody>>() {
                }
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactive_SalesShouldBeReturned_WhenHaveInactiveSales() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getInactiveSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange(
                "/sales/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponseBody>>() {
                }
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3, httpResponse.getBody().getContent().size());
            assertNotNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findInactive_SalesShouldNotBeReturned_WhenDoNotHaveInactiveSales() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange(
                "/sales/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<SaleResponseBody>>() {
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
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/sales/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void details_SaleDetailsShouldBeReturned_WhenIdWasFound() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);

            var item = SaleItemFactory.getSaleItem();
            item.setSaleId(sale.getId());
            item.setProductId(product.getId());
            saleItemRepository.save(item);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/sales/details/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                SaleDetailsResponseBody.class,
                sale.getId());

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().items().size());
            assertEquals("Samsung Galaxy S20", httpResponse.getBody().items().get(0).productDescription());
            assertNotNull(httpResponse.getBody().items().get(0).id());
            assertEquals(1, httpResponse.getBody().items().get(0).amount());
            assertEquals(1750.90, httpResponse.getBody().items().get(0).price());
        });
    }

    @Test
    void details_SaleDetailsShouldNotBeReturned_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/sales/details/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class,
                0);

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda: 0 não foi encontrada!", httpResponse.getBody().message());
        });
    }

    @Test
    void findActiveBySelectedDate_SalesShouldBeReturn_WhenHaveActiveSalesWithSelectedDate() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange(
                "/sales/active/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponseBody>>() {
                },
                LocalDateTime.now()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3, httpResponse.getBody().size());
            assertNull(httpResponse.getBody().get(0).deletedAt());
        });
    }

    @Test
    void findActiveBySelectedDate_SalesShouldNotBeReturned_WhenDoNotHaveActiveSalesWithSelectedDate() {
        var selectedDate = LocalDateTime.now().plusDays(1L);
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange(
                "/sales/active/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponseBody>>() {
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
    void findInactiveBySelectedDate_SalesShouldBeReturned_WhenHaveInactiveSalesWithSelectedDate() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getInactiveSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/sales/inactive/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponseBody>>() {
                },
                LocalDateTime.now()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3, httpResponse.getBody().size());
            assertNotNull(httpResponse.getBody().get(0).deletedAt());
        });
    }

    @Test
    void findInactiveBySelectedDate_SalesShouldNotBeReturned_WhenDoNotInactiveSalesWithSelectedDate() {
        var selectedSale = LocalDateTime.now().plusDays(1L);
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getInactiveSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange(
                "/sales/inactive/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<List<SaleResponseBody>>() {
                },
                selectedSale
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactiveBySelectedDate_SalesShouldNotBeReturned_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/sales/inactive/search?date={date}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class,
                LocalDateTime.now()
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void delete_SaleShouldBeDeleted_WhenIdWasFound() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);

            saleItem = SaleItemFactory.getSaleItem();
            saleItem.setSaleId(sale.getId());
            saleItem.setProductId(product.getId());
            saleItemRepository.save(saleItem);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/sales/{id}",
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
    void delete_SaleShouldNotBeDeleted_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/sales/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda: 0 não foi encontrada!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_SaleShouldNotBeDeleted_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/sales/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class,
                0
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void reactive_SaleShouldBeReactivated_WhenIdWasFound() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getInactiveSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);

            saleItem = SaleItemFactory.getInactiveSaleItem();
            saleItem.setSaleId(sale.getId());
            saleItem.setProductId(product.getId());
            saleItemRepository.save(saleItem);
        });

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/sales/{id}",
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
    void reactivate_SaleShouldNotBeReactivated_WhenDoesNotHaveEnoughStock() {
        var users = userRepository.findAll();
        users.forEach(user -> {
            sale = SaleFactory.getInactiveSale();
            sale.setUserId(user.getId());
            saleRepository.save(sale);

            saleItem = SaleItemFactory.getInactiveSaleItem();
            saleItem.setSaleId(sale.getId());
            saleItem.setProductId(product.getId());
            saleItem.setAmount(100);
            saleItemRepository.save(saleItem);
        });


        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/sales/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class,
                sale.getId()
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(String.format("O produto: %s não possui estoque suficiente!", product.getId()), httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_SaleShouldNotBeReactivated_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/sales/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A venda: 0 não foi encontrada!", httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_SaleShouldNotBeReactivated_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/sales/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponseBody.class,
                0
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

}