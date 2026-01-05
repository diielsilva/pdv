package com.todev.pdv.core.models.user;

import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class UserBuilder {
    private static final String ZONE_ID = "America/Sao_Paulo";
    private User instance;

    public UserBuilder builder() {
        instance = new User();
        return this;
    }

    public UserBuilder withId(Integer id) {
        instance.setId(id);
        return this;
    }

    public UserBuilder withName(String name) {
        instance.setName(name);
        return this;
    }

    public UserBuilder withLogin(String login) {
        instance.setLogin(login);
        return this;
    }

    public UserBuilder withPassword(String password) {
        instance.setPassword(password);
        return this;
    }

    public UserBuilder withEncryptedPassword(PasswordEncoder bcrypt, String password) {
        instance.setPassword(bcrypt.encode(password));
        return this;
    }

    public UserBuilder withRole(Role role) {
        instance.setRole(role);
        return this;
    }

    public UserBuilder createdAt(LocalDateTime date) {
        instance.setCreatedAt(date);
        return this;
    }

    public UserBuilder deletedAt(LocalDateTime date) {
        instance.setDeletedAt(date);
        return this;
    }

    public User build() {
        return instance;
    }
}
