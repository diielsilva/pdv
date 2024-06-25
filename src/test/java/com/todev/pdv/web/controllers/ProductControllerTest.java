package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.common.dtos.ProductRequest;
import com.todev.pdv.common.dtos.ProductResponse;
import com.todev.pdv.core.models.Product;
import com.todev.pdv.core.repositories.ProductRepository;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.CredentialsFactory;
import com.todev.pdv.factories.ProductFactory;
import com.todev.pdv.factories.UserFactory;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerTest {
    @Autowired
    private TestRestTemplate apiClient;

    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        securityHelper.createUser(UserFactory.getAdmin());
        securityHelper.createUser(UserFactory.getManager());
        securityHelper.createUser(UserFactory.getSeller());
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void save_ProductShouldBeSaved() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ProductResponse.class
        );

        assertAll(() -> {
            assertEquals(CREATED, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertNotNull(httpResponse.getBody().id());
            assertEquals("Samsung Galaxy S20", httpResponse.getBody().description());
            assertEquals(10, httpResponse.getBody().amount());
            assertEquals(1750.90, httpResponse.getBody().price());
        });
    }

    @Test
    void save_ProductShouldNotBeSaved_WhenDescriptionIsInUse() {
        productRepository.save(ProductFactory.getProduct());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(CONFLICT, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A descrição: Samsung Galaxy S20 já está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_ProductShouldNotBeSaved_WhenProductWithAmountEqualsToMinusOneWasReceived() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = new ProductRequest("Samsung Galaxy S20", -1, 1750.9);
        var httpResponse = apiClient.exchange("/products",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
        });
    }

    @Test
    void save_ProductShouldNotBeSaved_WhenProductWithPriceEqualsToMinusOneWasReceived() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = new ProductRequest("Samsung Galaxy S20", 1, -1.0);
        var httpResponse = apiClient.exchange("/products",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("O preço deve ser maior ou igual a zero!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_ProductShouldNotBeSaved_WhenProductWithoutValuesWasReceived() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = new ProductRequest(null, null, null);
        var httpResponse = apiClient.exchange("/products",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3, httpResponse.getBody().details().size());
        });
    }

    @Test
    void save_ProductShouldNotBeSaved_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActive_ProductsShouldBeReturned_WhenHaveActiveProducts() {
        productRepository.save(ProductFactory.getProduct());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
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
    void findActive_ProductsShouldNotBeReturned_WhenDoNotHaveActiveProducts() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
                }
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactive_ProductsShouldBeReturned_WhenHaveInactiveProducts() {
        productRepository.save(ProductFactory.getInactiveProduct());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/products/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
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
    void findInactive_ProductsShouldNotBeReturned_WhenDoNotHaveInactiveProducts() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/products/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
                }
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactive_ProductsShouldNotBeReturned_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveByDescriptionContaining_ProductsShouldBeReturned_WhenHaveActiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getProduct());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/active/search?description={description}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
                },
                "Gal"
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().getContent().size());
            assertNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findActiveByDescriptionContaining_ProductsShouldNotBeReturned_WhenDoNotHaveActiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getProduct());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/active/search?description={description}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
                },
                "S21"
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactiveByDescriptionContaining_ProductsShouldBeReturned_WhenHaveInactiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getInactiveProduct());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/products/inactive/search?description={description}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
                },
                "S20"
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().getContent().size());
            assertNotNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findInactiveByDescriptionContaining_ProductsShouldNotBeReturned_WhenDoNotInactiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getProduct());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/products/inactive/search?description={description}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
                },
                "S20"
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactiveByDescriptionContaining_ProductsShouldNotBeReturned_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/inactive/search?description={description}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
                ,
                "S20"
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveById_ProductShouldBeReturned_WhenIdWasFound() {
        var product = productRepository.save(ProductFactory.getProduct());
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ProductResponse.class,
                product.getId()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(product.getId(), httpResponse.getBody().id());
            assertNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void findActiveById_ProductShouldNotBeReturned_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void findInactiveById_ProductShouldBeReturned_WhenIdWasFound() {
        var product = productRepository.save(ProductFactory.getInactiveProduct());
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/products/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ProductResponse.class,
                product.getId()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(product.getId(), httpResponse.getBody().id());
            assertNotNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void findInactiveById_ProductShouldNotBeReturned_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/products/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void findInactiveById_ProductShouldNotBeReturned_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void update_ProductShouldBeUpdated_WhenDescriptionIsNotInUse() {
        var product = new Product(null, "iPhone XR", 10, 1750.9, LocalDateTime.now(), null);
        productRepository.save(product);

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ProductResponse.class,
                product.getId()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertNotEquals(product.getDescription(), httpResponse.getBody().description());
            assertNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void update_ProductShouldBeUpdated_WhenDescriptionIsInUseBySameProduct() {
        var product = productRepository.save(ProductFactory.getProduct());
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ProductResponse.class,
                product.getId()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(product.getDescription(), httpResponse.getBody().description());
        });
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenDescriptionIsInUseByAnotherProduct() {
        var product = new Product(null, "iPhone XR", 10, 1750.9, LocalDateTime.now(), null);
        productRepository.saveAll(List.of(product, ProductFactory.getProduct()));

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class,
                product.getId()
        );

        assertAll(() -> {
            assertEquals(CONFLICT, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A descrição: Samsung Galaxy S20 já está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenReceivedProductHasAmountEqualsToMinus() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = new ProductRequest("iPhone XR", -1, 1750.9);
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class,
                1
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("A quantidade do produto deve ser maior ou igual a zero!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenReceivedProductHasPriceEqualsToMinusOne() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = new ProductRequest("Samsung S20 FE", 10, -1.0);
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class,
                1
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("O preço deve ser maior ou igual a zero!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenEmptyProductWasReceived() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = new ProductRequest(null, null, null);
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class,
                1
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3, httpResponse.getBody().details().size());
        });
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = ProductFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class,
                1
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void delete_ProductShouldBeDeleted_WhenIdWasFound() {
        var product = productRepository.save(ProductFactory.getProduct());
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/products/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class,
                product.getId()
        );

        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void delete_ProductShouldNotBeDeleted_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/products/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_ProductShouldNotBeDeleted_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_ProductShouldBeReactivated_WhenIdWasFound() {
        var product = productRepository.save(ProductFactory.getInactiveProduct());
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/products/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                Void.class,
                product.getId()
        );

        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_ProductShouldNotBeReactivated_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/products/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_ProductShouldNotBeReactivated_WhenOnlineUserIsASeller() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/products/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }
}