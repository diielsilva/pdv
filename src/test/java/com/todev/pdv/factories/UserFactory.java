package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.UserRequestBody;
import com.todev.pdv.common.dtos.UserResponseBody;
import com.todev.pdv.core.builders.UserBuilder;
import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.models.User;

import java.time.LocalDateTime;

public final class UserFactory {

    private UserFactory() {
    }

    public static User getAdmin() {
        return new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getManager() {
        return new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getSeller() {
        return new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(Role.SELLER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveAdmin() {
        return new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveManager() {
        return new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveSeller() {
        return new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(Role.SELLER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getSavedAdmin() {
        return new UserBuilder()
                .builder()
                .id(1)
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getSavedManager() {
        return new UserBuilder()
                .builder()
                .id(1)
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getSavedSeller() {
        return new UserBuilder()
                .builder()
                .id(1)
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(Role.SELLER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveSavedAdmin() {
        return new UserBuilder()
                .builder()
                .id(1)
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveSavedManager() {
        return new UserBuilder()
                .builder()
                .id(1)
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static UserRequestBody getManagerWithoutIssues() {
        return new UserRequestBody("Manager", "manager", "12345", "MANAGER");
    }

    public static UserRequestBody getRequestDTO() {
        return new UserRequestBody("Seller", "seller", "12345", "SELLER");
    }

    public static UserResponseBody getResponseDTO() {
        return new UserResponseBody(
                1,
                "Seller",
                Role.SELLER,
                LocalDateTime.now(),
                null
        );
    }
}
