package com.todev.pdv.security.filters;

import com.todev.pdv.common.dtos.ErrorResponse;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.security.services.contracts.TokenService;
import com.todev.pdv.security.utils.contracts.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.time.LocalDateTime;
import java.util.Set;

public class TokenFilter extends BasicAuthenticationFilter {
    private final TokenService tokenService;
    private final UserProvider userProvider;
    private final ResponseUtil responseUtil;

    public TokenFilter(AuthenticationManager authManager,
                       TokenService tokenService, UserProvider userProvider, ResponseUtil responseUtil) {
        super(authManager);
        this.tokenService = tokenService;
        this.userProvider = userProvider;
        this.responseUtil = responseUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) {
        try {
            var internalLogin = validateTokenFromRequest(request);

            if (internalLogin != null) {
                SecurityContextHolder.getContext().setAuthentication(internalLogin);
            }

            chain.doFilter(request, response);
        } catch (Exception exception) {
            var errorResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    403,
                    "O token de acesso está inválido, expirado ou você não pode acessar este recurso!",
                    request.getServletPath(),
                    Set.of()
            );
            responseUtil.sendResponse(response, 403, errorResponse);
        }
    }

    private UsernamePasswordAuthenticationToken validateTokenFromRequest(HttpServletRequest request) {
        var token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        var userLogin = tokenService.validateToken(token);

        if (userLogin == null) {
            return null;
        }

        var onlineUser = userProvider.findActiveByLogin(userLogin);

        return new UsernamePasswordAuthenticationToken(
                onlineUser.getLogin(),
                null,
                onlineUser.getAuthorities()
        );
    }
}
