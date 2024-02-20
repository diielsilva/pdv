package com.todev.pdv.core.providers.contracts;

import com.todev.pdv.core.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserProvider {
    User save(User user);

    Page<User> findActive(Pageable pageable);

    Page<User> findInactive(Pageable pageable);

    Page<User> findActiveByNameContaining(String name, Pageable pageable);

    Page<User> findInactiveByNameContaining(String name, Pageable pageable);

    User findActiveById(Integer id);

    User findInactiveById(Integer id);

    User findById(Integer id);

    Optional<User> findByLogin(String login);

    User findActiveByLogin(String login);
}
