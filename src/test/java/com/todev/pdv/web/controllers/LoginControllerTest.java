package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.CredentialsFactory;
import com.todev.pdv.factories.UserFactory;
import com.todev.pdv.helpers.LoginHelper;
import com.todev.pdv.security.dtos.LoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.todev.pdv.core.enums.Role.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class LoginControllerTest {
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
    void login_UserShouldBeAuthenticated_WhenValidCredentialsWereReceived() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpResponse = restTemplate.postForEntity("/login", CredentialsFactory.getAdminCredentials(), LoginResponse.class);
        assertEquals(OK, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getBody());
        assertNotNull(httpResponse.getBody().token());
        assertEquals(ADMIN, httpResponse.getBody().role());
    }

    @Test
    void login_UserShouldNotBeAuthenticated_WhenIncorrectCredentialsWereReceived() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getAdmin());
        var httpResponse = restTemplate
                .postForEntity("/login", CredentialsFactory.getAdminCredentialsWithInvalidPassword(), ErrorResponse.class);
        assertEquals(UNAUTHORIZED, httpResponse.getStatusCode());
    }

    @Test
    void login_UserShouldNotBeAuthenticated_WhenUserIsInactive() {
        LoginHelper.setAuthentication(userRepository, BCryptEncoder, UserFactory.getInactiveAdmin());
        var httpResponse = restTemplate.postForEntity("/login", CredentialsFactory.getAdminCredentials(), ErrorResponse.class);
        assertEquals(UNAUTHORIZED, httpResponse.getStatusCode());
    }

}
