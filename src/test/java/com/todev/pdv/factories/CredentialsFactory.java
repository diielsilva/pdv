package com.todev.pdv.factories;

import com.todev.pdv.security.dtos.LoginRequest;

public class CredentialsFactory {

    public static LoginRequest getAdmin() {
        return new LoginRequest("admin", "12345");
    }

    public static LoginRequest getManager() {
        return new LoginRequest("manager", "12345");
    }

    public static LoginRequest getSeller() {
        return new LoginRequest("seller", "12345");
    }

}
