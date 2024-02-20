package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.common.dtos.UserResponse;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.CredentialsFactory;
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

import static com.todev.pdv.core.enums.Role.SELLER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder BCryptEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void save_UserShouldBeSaved_WhenValidUserWasReceivedAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users",
                POST,
                new HttpEntity<>(UserFactory.getSellerWithoutIssues(), httpHeaders),
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
    void save_UserShouldBeSaved_WhenValidUserWasReceivedAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users",
                POST,
                new HttpEntity<>(UserFactory.getSellerWithoutIssues(), httpHeaders),
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
    void save_UserShouldNotBeSaved_WhenAdminUserWasReceivedAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users",
                POST,
                new HttpEntity<>(UserFactory.getAdminWithoutIssues(), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("Não é possível cadastrar usuários do tipo ADMIN!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_UserShouldNotBeSaved_WhenLoginIsInUseAndOnlineUserIsAManager() {
        userRepository.save(UserFactory.getSeller());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users",
                POST,
                new HttpEntity<>(UserFactory.getSellerWithoutIssues(), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(CONFLICT, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O login: seller já está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void save_UserShouldNotBeSaved_WhenReceivedUserHasAnInvalidRoleAndOnlineUserIsAnAdmin() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users",
                POST,
                new HttpEntity<>(UserFactory.getSellerWithAnInvalidRole(), httpHeaders),
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
    void save_UserShouldNotBeSaved_WhenReceivedUserHasNoValuesAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users",
                POST,
                new HttpEntity<>(UserFactory.getUserWithoutValues(), httpHeaders),
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users",
                POST,
                new HttpEntity<>(UserFactory.getSellerWithoutIssues(), httpHeaders),
                ErrorResponse.class
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActive_UsersShouldBeReturned_WhenHaveActiveUsersAndOnlineUserIsAnAdmin() {
        userRepository.saveAll(List.of(UserFactory.getManager(), UserFactory.getSeller()));
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                });
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(3, httpResponse.getBody().getContent().size());
            assertNull(httpResponse.getBody().getContent().get(0).deletedAt());
            assertNull(httpResponse.getBody().getContent().get(1).deletedAt());
            assertNull(httpResponse.getBody().getContent().get(2).deletedAt());
        });
    }

    @Test
    void findActive_UsersShouldBeReturned_WhenHaveActiveUsersAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/active",
                GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<PageableResponse<UserResponse>>() {
                });
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(1, httpResponse.getBody().getContent().size());
            assertNull(httpResponse.getBody().getContent().get(0).deletedAt());
        });
    }

    @Test
    void findActive_UsersShouldNotBeReturned_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/active",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findInactive_UsersShouldBeReturned_WhenHaveInactiveUsersAndOnlineUserIsAnAdmin() {
        userRepository.save(UserFactory.getInactiveSeller());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive",
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
    void findInactive_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsersAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveByNameContaining_UsersShouldBeReturned_WhenHaveActiveUsersWithNameContainingAndOnlineUserIsAnAdmin() {
        userRepository.saveAll(List.of(UserFactory.getManager(), UserFactory.getSeller()));
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users/active/search?name={name}",
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
    void findActiveByNameContaining_UsersShouldNotBeReturned_WhenDoNotHaveActiveUsersWithNameContainingAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/active/search?name={name}",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/active/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, "Man");
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findInactiveByNameContaining_UsersShouldBeReturned_WhenHaveInactiveUsersByNameContainingAndOnlineUserIsAnAdmin() {
        userRepository.save(UserFactory.getInactiveSeller());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive/search?name={name}",
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
    void findInactiveByNameContaining_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsersByNameContainingAndOnlineUserIsAManger() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive/search?name={name}",
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
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive/search?name={name}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, "ll");
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findActiveById_UserShouldBeReturned_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var user = userRepository.save(UserFactory.getSeller());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange(
                "/users/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                UserResponse.class, user.getId());
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(user.getId(), httpResponse.getBody().id());
            assertNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void findActiveById_UserShouldNotBeReturned_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 0);
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void findActiveById_UserShouldNotBeReturned_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/active/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 1
        );
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void findInactiveById_UserShouldBeReturned_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var user = userRepository.save(UserFactory.getInactiveManager());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                UserResponse.class, user.getId());
        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals(user.getId(), httpResponse.getBody().id());
            assertNotNull(httpResponse.getBody().deletedAt());
        });
    }

    @Test
    void findInactiveById_UserShouldNotBeReturned_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 0);
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void findInactiveById_UserShouldNotBeReturned_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/inactive/{id}",
                GET,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 1);
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void update_UserShouldBeUpdated_WhenValidUserWasReceivedAndLoginWasNotInUseAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange(
                "/users",
                PUT,
                new HttpEntity<>(UserFactory.getSellerWithoutIssues(), httpHeaders),
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
    void update_UserShouldBeUpdated_WhenLoginWasInUseBySameUserAndOnlineUserIsASeller() {
        var user = UserFactory.getSeller();
        user.setPassword(BCryptEncoder.encode(user.getPassword()));
        userRepository.save(user);
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users",
                PUT,
                new HttpEntity<>(UserFactory.getSellerWithoutIssues(), httpHeaders),
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
    void update_UserShouldNotBeUpdated_WhenLoginWasInUseByAnotherUser() {
        userRepository.save(UserFactory.getManager());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users",
                PUT,
                new HttpEntity<>(UserFactory.getManagerWithoutIssues(), httpHeaders),
                ErrorResponse.class
        );
        assertAll(() -> {
            assertEquals(CONFLICT, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O login: manager já está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldBeDeleted_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var user = userRepository.save(UserFactory.getManager());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class, user.getId());
        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void delete_UserShouldBeDeleted_WhenIdWasFoundAndAndOnlineUserIsAManager() {
        var user = userRepository.save(UserFactory.getSeller());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class, user.getId());
        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenUserTriesToDeleteAnAdminAndOnlineUserIsAManager() {
        var user = userRepository.save(UserFactory.getAdmin());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, user.getId());
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: manager não possui permissão para remover o usuário selecionado!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserTriesToDeleteHimself() {
        var user = UserFactory.getManager();
        user.setPassword(BCryptEncoder.encode(user.getPassword()));
        userRepository.save(user);
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, user.getId());
        assertAll(() -> {
            assertEquals(BAD_REQUEST, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: manager está em uso!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 0);
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                DELETE,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 1);
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_UserShouldBeReactivated_WhenIdWasFoundAndOnlineUserIsAnAdmin() {
        var user = userRepository.save(UserFactory.getInactiveManager());
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getAdminCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                Void.class, user.getId());
        assertEquals(NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    void reactivate_UserShouldNotBeReactivated_WhenIdWasNotFoundAndOnlineUserIsAManager() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getManager());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getManagerCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 0);
        assertAll(() -> {
            assertEquals(NOT_FOUND, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertEquals("O usuário: 0 não foi encontrado!", httpResponse.getBody().message());
        });
    }

    @Test
    void reactivate_UserShouldNotBeReactivated_WhenOnlineUserIsASeller() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getSeller());
        var httpHeaders = LoginHelper.getAuthentication(restTemplate, CredentialsFactory.getSellerCredentials());
        var httpResponse = restTemplate.exchange("/users/{id}",
                PATCH,
                new HttpEntity<>(httpHeaders),
                ErrorResponse.class, 1);
        assertEquals(FORBIDDEN, httpResponse.getStatusCode());
    }
}