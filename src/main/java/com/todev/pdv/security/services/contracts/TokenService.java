package com.todev.pdv.security.services.contracts;

public interface TokenService {
    String createToken(String userLogin);

    String validateToken(String token);
}
