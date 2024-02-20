package com.todev.pdv.helpers;

import com.todev.pdv.core.models.User;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.security.dtos.LoginRequest;
import com.todev.pdv.security.dtos.LoginResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;


public final class LoginHelper {

    public static HttpHeaders getAuthentication(TestRestTemplate restTemplate, LoginRequest requestDTO) {
        var httpHeaders = new HttpHeaders();
        var httpResponse = restTemplate.postForEntity("/login", requestDTO, LoginResponse.class);
        assertThat(httpResponse.getBody()).isNotNull();
        httpHeaders.add("Authorization", "Bearer " + httpResponse.getBody().token());
        return httpHeaders;
    }

    public static void setAuthentication(UserRepository repository, PasswordEncoder encoder, User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repository.save(user);
    }
}
