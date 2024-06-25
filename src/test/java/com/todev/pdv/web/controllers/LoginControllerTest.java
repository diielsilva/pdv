package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.CredentialsFactory;
import com.todev.pdv.factories.UserFactory;
import com.todev.pdv.helpers.SecurityHelper;
import com.todev.pdv.security.dtos.LoginRequest;
import com.todev.pdv.security.dtos.LoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static com.todev.pdv.core.enums.Role.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class LoginControllerTest {
    @Autowired
    private TestRestTemplate apiClient;

    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        securityHelper.createUser(UserFactory.getAdmin());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void login_UserShouldBeAuthenticated() {
        var requestBody = CredentialsFactory.getAdmin();
        var httpResponse = apiClient.postForEntity("/login", requestBody, LoginResponse.class);

        assertAll(() -> {
            assertEquals(OK, httpResponse.getStatusCode());
            assertNotNull(httpResponse.getBody());
            assertNotNull(httpResponse.getBody().token());
            assertEquals(ADMIN, httpResponse.getBody().role());
        });
    }

    @Test
    void login_UserShouldNotBeAuthenticated_WhenIncorrectCredentialsWereReceived() {
        var requestBody = new LoginRequest("admin", "123456");
        var httpResponse = apiClient.postForEntity("/login", requestBody, ErrorResponse.class);

        assertEquals(UNAUTHORIZED, httpResponse.getStatusCode());
    }

    @Test
    void login_UserShouldNotBeAuthenticated_WhenUserIsInactive() {
        userRepository.deleteAll();
        securityHelper.createUser(UserFactory.getInactiveAdmin());

        var requestBody = CredentialsFactory.getAdmin();
        var httpResponse = apiClient.postForEntity("/login", requestBody, ErrorResponse.class);

        assertEquals(UNAUTHORIZED, httpResponse.getStatusCode());
    }

}
