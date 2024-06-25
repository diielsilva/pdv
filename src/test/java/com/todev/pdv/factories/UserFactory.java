package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.dtos.UserResponse;
import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.models.User;

import java.time.LocalDateTime;

public final class UserFactory {

    private UserFactory() {
    }

    public static User getAdmin() {
        return new User(
                null,
                "Admin",
                "admin",
                "12345",
                Role.ADMIN,
                LocalDateTime.now(),
                null
        );
    }

    public static User getManager() {
        return new User(
                null,
                "Manager",
                "manager",
                "12345",
                Role.MANAGER,
                LocalDateTime.now(),
                null
        );
    }

    public static User getSeller() {
        return new User(
                null,
                "Seller",
                "seller",
                "12345",
                Role.SELLER,
                LocalDateTime.now(),
                null
        );
    }

    public static User getInactiveAdmin() {
        return new User(
                null,
                "Admin",
                "admin",
                "12345",
                Role.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static User getInactiveManager() {
        return new User(
                null,
                "Manager",
                "manager",
                "12345",
                Role.MANAGER,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static User getInactiveSeller() {
        return new User(
                null,
                "Seller",
                "seller",
                "12345",
                Role.SELLER,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static User getSavedAdmin() {
        return new User(
                1,
                "Admin",
                "admin",
                "12345",
                Role.ADMIN,
                LocalDateTime.now(),
                null
        );
    }

    public static User getSavedManager() {
        return new User(
                1,
                "Manager",
                "manager",
                "12345",
                Role.MANAGER,
                LocalDateTime.now(),
                null
        );
    }

    public static User getSavedSeller() {
        return new User(
                1,
                "Seller",
                "seller",
                "12345",
                Role.SELLER,
                LocalDateTime.now(),
                null
        );
    }

    public static User getInactiveSavedAdmin() {
        return new User(
                1,
                "Admin",
                "admin",
                "12345",
                Role.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static User getInactiveSavedManager() {
        return new User(
                1,
                "Manager",
                "manager",
                "12345",
                Role.MANAGER,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
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
