package com.todev.pdv.helpers;

import com.todev.pdv.core.models.User;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.security.dtos.LoginRequest;
import com.todev.pdv.security.dtos.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class SecurityHelper {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TestRestTemplate apiClient;

    public HttpHeaders authenticate(LoginRequest requestBody) {
        var httpHeaders = new HttpHeaders();
        var httpResponse = apiClient.postForEntity("/login", requestBody, LoginResponse.class);
        assertThat(httpResponse.getBody()).isNotNull();
        httpHeaders.add("Authorization", "Bearer " + httpResponse.getBody().token());
        return httpHeaders;
    }

    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
