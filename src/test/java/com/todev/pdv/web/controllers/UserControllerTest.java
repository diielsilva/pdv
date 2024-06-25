package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.dtos.UserResponse;
import com.todev.pdv.core.models.User;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.CredentialsFactory;
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

import static com.todev.pdv.core.enums.Role.SELLER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private TestRestTemplate apiClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityHelper securityHelper;

    private User user;

    @BeforeEach
    void setUp() {
        securityHelper.createUser(UserFactory.getAdmin());
        securityHelper.createUser(UserFactory.getManager());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void save_UserShouldBeSaved() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = UserFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/users",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                UserResponse.class
        );

        assertAll(() -> {
            assertEquals(CREATED, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertNotNull(httpResponse.getBody().id());
            assertEquals("Seller", httpResponse.getBody().name());
            assertEquals(SELLER, httpResponse.getBody().role());
            assertNotNull(httpResponse.getBody().createdAt());
            assertNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void save_UserShouldBeSaved_WhenOnlineUserIsAManager() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = UserFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/users",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                UserResponse.class
        );

        assertAll(() -> {
            assertEquals(CREATED, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertNotNull(httpResponse.getBody().id());
            assertEquals("Seller", httpResponse.getBody().name());
            assertEquals(SELLER, httpResponse.getBody().role());
            assertNotNull(httpResponse.getBody().createdAt());
            assertNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void save_UserShouldNotBeSaved_WhenAdminUserWasReceived() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = new UserRequest("Admin", "admin", "12345", "ADMIN");
        var httpResponse = apiClient.exchange("/users",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("Não é possível cadastrar usuários do tipo ADMIN!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_UserShouldNotBeSaved_WhenLoginIsInUse() {
        userRepository.save(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = UserFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/users",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(CONFLICT, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O login: seller já está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_UserShouldNotBeSaved_WhenReceivedUserHasAnInvalidRole() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var requestBody = new UserRequest("Seller", "seller", "12345", "SELLERS");
        var httpResponse = apiClient.exchange("/users",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().details().size());
            assertEquals("O papel do usuário é inválido!", httpResponse.getBody().details().stream().toList().get(0));
        });
    }

    @Test
    void save_UserShouldNotBeSaved_WhenAnEmptyUserWasReceived() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = new UserRequest(null, null, null, null);
        var httpResponse = apiClient.exchange("/users",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(5, httpResponse.getBody().details().size());
        });
    }

    @Test
    void save_UserShouldNotBeSaved_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = UserFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/users",
                POST,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActive_UsersShouldBeReturned_WhenHaveActiveUsers() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/users/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                });

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(2, httpResponse.getBody().getContent().size());
            assertNull(httpResponse.getBody().getContent().get(0).deletedAt());
            assertNull(httpResponse.getBody().getContent().get(1).deletedAt());
        });
    }

    @Test
    void findActive_UsersShouldBeReturned_WhenHaveActiveUsersAndOnlineUserIsAManager() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                });

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(2, httpResponse.getBody().getContent().size());
            assertNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findActive_UsersShouldNotBeReturned_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/active",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findInactive_UsersShouldBeReturned_WhenHaveInactiveUsers() {
        userRepository.save(UserFactory.getInactiveSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/users/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
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
    void findInactive_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsers() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                });

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactive_UsersShouldNotBeReturned_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveByNameContaining_UsersShouldBeReturned_WhenHaveActiveUsersWithNameContaining() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/users/active/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                }, "Man");

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().getContent().size());
            assertEquals("Manager", httpResponse.getBody().getContent().get(0).name());
        });
    }

    @Test
    void findActiveByNameContaining_UsersShouldNotBeReturned_WhenDoNotHaveActiveUsersWithNameContaining() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/active/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                }, "Employee");

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().getContent().isEmpty());
        });
    }

    @Test
    void findActiveByNameContaining_UsersShouldNotBeReturned_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/active/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, "Man");

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findInactiveByNameContaining_UsersShouldBeReturned_WhenHaveInactiveUsersWithNameContaining() {
        userRepository.save(UserFactory.getInactiveSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/users/inactive/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                }, "ll");

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().getContent().size());
            assertNotNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findInactiveByNameContaining_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsersWithNameContaining() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/inactive/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                }, "man");

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertTrue(httpResponse.getBody().isEmpty());
        });
    }

    @Test
    void findInactiveByNameContaining_UsersShouldNotBeReturned_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/inactive/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, "ll");

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveById_UserShouldBeReturned_WhenIdWasFound() {
        var user = userRepository.save(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange(
                "/users/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                UserResponse.class,
                user.getId()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(user.getId(), httpResponse.getBody().id());
            assertNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void findActiveById_UserShouldNotBeReturned_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void findActiveById_UserShouldNotBeReturned_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findInactiveById_UserShouldBeReturned_WhenIdWasFound() {
        var user = userRepository.save(UserFactory.getInactiveSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/users/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                UserResponse.class,
                user.getId()
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(user.getId(), httpResponse.getBody().id());
            assertNotNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void findInactiveById_UserShouldNotBeReturned_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void findInactiveById_UserShouldNotBeReturned_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 1);

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void update_UserShouldBeUpdated() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var requestBody = UserFactory.getRequestDTO();
        var httpResponse = apiClient.exchange(
                "/users",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                UserResponse.class
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("Seller", httpResponse.getBody().name());
            assertEquals(SELLER, httpResponse.getBody().role());
        });
    }

    @Test
    void update_UserShouldBeUpdated_WhenLoginWasInUseBySameUser() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = UserFactory.getRequestDTO();
        var httpResponse = apiClient.exchange("/users",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                UserResponse.class
        );

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("Seller", httpResponse.getBody().name());
            assertEquals(SELLER, httpResponse.getBody().role());
        });
    }

    @Test
    void update_UserShouldNotBeUpdated_WhenLoginIsInUseByAnotherUser() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var requestBody = new UserRequest("Manager", "manager", "12345", "MANAGER");
        var httpResponse = apiClient.exchange("/users",
                PUT,
                new HttpEntity<>(requestBody, httpHeaders),
                ErrorResponse.class
        );

        assertAll(() -> {
            assertEquals(CONFLICT, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O login: manager já está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldBeDeleted_WhenIdWasFound() {
        var user = userRepository.save(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class,
                user.getId()
        );

        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void delete_UserShouldBeDeleted_WhenIdWasFoundAndAndOnlineUserIsAManager() {
        var user = userRepository.save(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class,
                user.getId()
        );

        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenUserTriesToDeleteAnAdmin() {
        userRepository.deleteAll();
        securityHelper.createUser(UserFactory.getManager());
        var user = userRepository.save(UserFactory.getAdmin());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                user.getId()
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: manager não possui permissão para remover o usuário selecionado!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserTriesToDeleteHimself() {
        userRepository.deleteAll();
        securityHelper.createUser(UserFactory.getManager());
        var users = userRepository.findAll();
        users.forEach(current -> user = current);

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                user.getId()
        );

        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: manager está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                1
        );

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_UserShouldBeReactivated_WhenIdWasFound() {
        user = userRepository.save(UserFactory.getInactiveSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getAdmin());
        var httpResponse = apiClient.exchange("/users/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                Void.class,
                user.getId()
        );

        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_UserShouldNotBeReactivated_WhenIdWasNotFound() {
        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getManager());
        var httpResponse = apiClient.exchange("/users/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class,
                0
        );

        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_UserShouldNotBeReactivated_WhenOnlineUserIsASeller() {
        securityHelper.createUser(UserFactory.getSeller());

        var httpHeaders = securityHelper.authenticate(CredentialsFactory.getSeller());
        var httpResponse = apiClient.exchange("/users/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 1);

        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }
}