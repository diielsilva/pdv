package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.dtos.UserResponse;
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

    public static UserRequest getManagerWithoutIssues() {
        return new UserRequest("Manager", "manager", "12345", "MANAGER");
    }

    public static UserRequest getRequestDTO() {
        return new UserRequest("Seller", "seller", "12345", "SELLER");
    }

    public static UserResponse getResponseDTO() {
        return new UserResponse(
                1,
                "Seller",
                Role.SELLER,
                LocalDateTime.now(),
                null
        );
    }
}
