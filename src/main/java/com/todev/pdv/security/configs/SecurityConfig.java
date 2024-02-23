package com.todev.pdv.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.security.filters.LoginFilter;
import com.todev.pdv.security.filters.TokenFilter;
import com.todev.pdv.security.utils.contracts.ResponseUtil;
import com.todev.pdv.security.services.contracts.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    ObjectMapper getObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager getAuthManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain getFilterChain(HttpSecurity httpSecurity,
                                       TokenService tokenService,
                                       UserProvider userProvider,
                                       ResponseUtil responseUtil) throws Exception {
        var authManager = getAuthManager(httpSecurity.getSharedObject(AuthenticationConfiguration.class));
        var loginFilter = new LoginFilter(authManager, tokenService, responseUtil, getObjectMapper());
        var tokenFilter = new TokenFilter(authManager, tokenService, userProvider, responseUtil);

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var configuration = new CorsConfiguration();
                    configuration.setAllowedMethods(List.of("GET", "PUT", "DELETE", "POST", "HEAD", "OPTIONS", "PATCH"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(List.of("*"));
                    configuration.setAllowedOriginPatterns(List.of("*"));
                    return configuration;
                }))
                .authenticationManager(authManager)
                .authorizeHttpRequests(authorizer -> authorizer
                        .requestMatchers("/login", "v3/api-docs/**", "/swagger-ui/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenFilter, BasicAuthenticationFilter.class)
                .build();

    }
}
