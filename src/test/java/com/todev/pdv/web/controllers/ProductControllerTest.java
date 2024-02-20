package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.common.dtos.ProductResponse;
import com.todev.pdv.core.repositories.ProductRepository;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.CredentialsFactory;
import com.todev.pdv.factories.ProductFactory;
import com.todev.pdv.factories.UserFactory;
import com.todev.pdv.helpers.LoginHelper;
import com.todev.pdv.wrappers.PageableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder BCryptEncoder;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void save_ProductShouldBeSaved_WhenValidProductWasReceivedAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products",
                POST,
                new HttpEntity<>(ProductFactory.getProductWithoutIssues(), httpHeaders),
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
    void save_ProductShouldNotBeSaved_WhenDescriptionIsInUseAndOnlineUserIsAManager() {
        productRepository.save(ProductFactory.getProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products",
                POST,
                new HttpEntity<>(ProductFactory.getProductWithoutIssues(), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(CONFLICT, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("A descrição: Samsung Galaxy S20 já está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_ProductShouldNotBeSaved_WhenProductWithAmountEqualsToMinusOneWasReceivedAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products",
                POST,
                new HttpEntity<>(ProductFactory.getProductWithAmountEqualsToMinusOne(), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
        });
    }

    @Test
    void save_ProductShouldNotBeSaved_WhenProductWithPriceEqualsToMinusOneWasReceivedAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products",
                POST,
                new HttpEntity<>(ProductFactory.getProductWithPriceEqualsToMinusOne(), httpHeaders),
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
    void save_ProductShouldNotBeSaved_WhenProductWithoutValuesWasReceivedAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products",
                POST,
                new HttpEntity<>(ProductFactory.getProductWithoutValues(), httpHeaders),
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products",
                POST,
                new HttpEntity<>(ProductFactory.getProduct(), httpHeaders),
                ErrorResponse.class
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActive_ProductsShouldBeReturned_WhenHaveActiveProducts() {
        productRepository.save(ProductFactory.getProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/active",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/active",
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
    void findInactive_ProductsShouldBeReturned_WhenHaveInactiveProductsAndOnlineUserIsAnAdmin() {
        productRepository.save(ProductFactory.getInactiveProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive",
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
    void findInactive_ProductsShouldNotBeReturned_WhenDoNotHaveInactiveProductsAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveByDescriptionContaining_ProductsShouldBeReturned_WhenHaveActiveProductsWithDescriptionContaining() {
        productRepository.save(ProductFactory.getProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/active/search?description={description}",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/active/search?description={description}",
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
    void findInactiveByDescriptionContaining_ProductsShouldBeReturned_WhenHaveInactiveProductsWithDescriptionContainingAndOnlineUserIsAnAdmin() {
        productRepository.save(ProductFactory.getInactiveProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive/search?description={description}",
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
    void findInactiveByDescriptionContaining_ProductsShouldNotBeReturned_WhenDoNotInactiveProductsWithDescriptionContainingAndOnlineUserIsAManager() {
        productRepository.save(ProductFactory.getProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive/search?description={description}",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive/search?description={description}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<ProductResponse>>() {
                },
                "S20"
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveById_ProductShouldBeReturned_WhenIdWasFound() {
        var product = productRepository.save(ProductFactory.getProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/active/{id}",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/active/{id}",
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
    void findInactiveById_ProductShouldBeReturned_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var product = productRepository.save(ProductFactory.getInactiveProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive/{id}",
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
    void findInactiveById_ProductShouldNotBeReturned_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive/{id}",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void update_ProductShouldBeUpdated_WhenDescriptionIsNotInUseOnlineUserIsAnAdmin() {
        var product = ProductFactory.getProduct();
        product.setDescription("iPhone XR");
        productRepository.save(product);
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithoutIssues(), httpHeaders),
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
    void update_ProductShouldBeUpdated_WhenDescriptionIsInUseBySameProductAndOnlineUserIsAManager() {
        var product = productRepository.save(ProductFactory.getProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithoutIssues(), httpHeaders),
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
    void update_ProductShouldNotBeUpdated_WhenDescriptionIsInUseByAnotherProductAndOnlineUserIsAManager() {
        var product = ProductFactory.getProduct();
        product.setDescription("iPhone XR");
        productRepository.saveAll(List.of(product, ProductFactory.getProduct()));
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithoutIssues(), httpHeaders),
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
    void update_ProductShouldNotBeUpdated_WhenReceivedProductHasAmountEqualsToMinusOneAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithAmountEqualsToMinusOne(), httpHeaders),
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
    void update_ProductShouldNotBeUpdated_WhenReceivedProductHasPriceEqualsToMinusOneAndUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithPriceEqualsToMinusOne(), httpHeaders),
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
    void update_ProductShouldNotBeUpdated_WhenProductWithoutValuesWasReceivedAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithoutValues(), httpHeaders),
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
    void update_ProductShouldNotBeUpdated_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithoutIssues(), httpHeaders),
                ErrorResponse.class,
                1
        );
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 1 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void update_ProductShouldNotBeUpdated_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PUT,
                new HttpEntity<>(ProductFactory.getProductWithoutIssues(), httpHeaders),
                ErrorResponse.class,
                1
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void delete_ProductShouldBeDeleted_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var product = productRepository.save(ProductFactory.getProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class,
                product.getId()
        );
        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void delete_ProductShouldNotBeDeleted_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 1 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_ProductShouldNotBeDeleted_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_ProductShouldBeReactivated_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var product = productRepository.save(ProductFactory.getInactiveProduct());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                Void.class,
                product.getId()
        );
        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_ProductShouldNotBeReactivated_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O produto: 1 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_ProductShouldNotBeReactivated_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/products/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }
}