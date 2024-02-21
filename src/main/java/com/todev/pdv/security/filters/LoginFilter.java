package com.todev.pdv.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.core.models.User;
import com.todev.pdv.security.dtos.LoginRequest;
import com.todev.pdv.security.dtos.LoginResponse;
import com.todev.pdv.security.services.contracts.TokenService;
import com.todev.pdv.security.utils.contracts.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final ResponseUtil responseUtil;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            var loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            var internalLogin = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password());
            return authManager.authenticate(internalLogin);
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain chain,
                                         Authentication authResult) {
        var user = (User) authResult.getPrincipal();
        var token = tokenService.createToken(user.getLogin());
        var loginResponse = new LoginResponse(token, user.getRole());
        responseUtil.sendResponse(response, 200, loginResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        var errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                401,
                "Login ou senha inválido(a)!",
                request.getServletPath(),
                Set.of()
        );
        responseUtil.sendResponse(response, 401, errorResponse);
    }
}
