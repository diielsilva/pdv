package com.todev.pdv.security.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.todev.pdv.security.services.contracts.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {
    @Value("${security.token.secret}")
    private String secret;
    @Value("${security.token.expiration}")
    private Integer expiration;

    @Override
    public String createToken(String userLogin) {
        return JWT
                .create()
                .withSubject(userLogin)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public String validateToken(String token) {
        return JWT
                .require(Algorithm.HMAC512(secret))
                .build()
                .verify(token.replace("Bearer ", ""))
                .getSubject();
    }
}
