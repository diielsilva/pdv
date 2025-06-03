package com.todev.pdv.factories;

import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.dtos.UserResponse;
import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.models.User;
import com.todev.pdv.core.models.user.UserBuilder;

import java.time.LocalDateTime;

public final class UserFactory {

    private UserFactory() {
    }

    public static User getAdmin() {
        return new UserBuilder()
                .builder()
                .withName("Admin")
                .withLogin("admin")
                .withPassword("12345")
                .withRole(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getManager() {
        return new UserBuilder()
                .builder()
                .withName("Manager")
                .withLogin("manager")
                .withPassword("12345")
                .withRole(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getSeller() {
        return new UserBuilder()
                .builder()
                .withName("Seller")
                .withLogin("seller")
                .withPassword("12345")
                .withRole(Role.SELLER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveAdmin() {
        return new UserBuilder()
                .builder()
                .withName("Admin")
                .withLogin("admin")
                .withPassword("12345")
                .withRole(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveManager() {
        return new UserBuilder()
                .builder()
                .withName("Manager")
                .withLogin("manager")
                .withPassword("12345")
                .withRole(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveSeller() {
        return new UserBuilder()
                .builder()
                .withName("Seller")
                .withLogin("seller")
                .withPassword("12345")
                .withRole(Role.SELLER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getSavedAdmin() {
        return new UserBuilder()
                .builder()
                .withId(1)
                .withName("Admin")
                .withLogin("admin")
                .withPassword("12345")
                .withRole(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getSavedManager() {
        return new UserBuilder()
                .builder()
                .withId(1)
                .withName("Manager")
                .withLogin("manager")
                .withPassword("12345")
                .withRole(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getSavedSeller() {
        return new UserBuilder()
                .builder()
                .withId(1)
                .withName("Seller")
                .withLogin("seller")
                .withPassword("12345")
                .withRole(Role.SELLER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveSavedAdmin() {
        return new UserBuilder()
                .builder()
                .withId(1)
                .withName("Admin")
                .withLogin("admin")
                .withPassword("12345")
                .withRole(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static User getInactiveSavedManager() {
        return new UserBuilder()
                .builder()
                .withId(1)
                .withName("Manager")
                .withLogin("manager")
                .withPassword("12345")
                .withRole(Role.MANAGER)
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
