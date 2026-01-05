package com.todev.pdv.core.builders;

import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.models.User;

public class UserBuilder extends BaseBuilder<User> {
    @Override
    public UserBuilder builder() {
        instance = new User();
        return this;
    }

    public UserBuilder id(int id) {
        instance.setId(id);
        return this;
    }

    public UserBuilder name(String name) {
        instance.setName(name);
        return this;
    }

    public UserBuilder login(String login) {
        instance.setLogin(login);
        return this;
    }

    public UserBuilder password(String password) {
        instance.setPassword(password);
        return this;
    }

    public UserBuilder role(Role role) {
        instance.setRole(role);
        return this;
    }

    @Override
    public User build() {
        User user = instance;
        instance = new User();
        return user;
    }
}
