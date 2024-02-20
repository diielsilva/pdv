package com.todev.pdv.factories;

import com.todev.pdv.security.dtos.LoginRequest;

public final class CredentialsFactory {
    private CredentialsFactory() {
    }

    public static LoginRequest getAdminCredentials() {
        return new LoginRequest("admin", "12345");
    }

    public static LoginRequest getManagerCredentials() {
        return new LoginRequest("manager", "12345");
    }

    public static LoginRequest getSellerCredentials() {
        return new LoginRequest("seller", "12345");
    }

    public static LoginRequest getAdminCredentialsWithInvalidPassword() {
        return new LoginRequest("admin", "123456");
    }
}
