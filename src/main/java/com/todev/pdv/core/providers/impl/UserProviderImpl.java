package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.exceptions.ModelNotFoundException;
import com.todev.pdv.core.models.User;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {
    private final UserRepository userRepository;
    private static final String ERROR_MESSAGE = "O usuário: %s não foi encontrado!";

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Page<User> findActive(Pageable pageable) {
        return userRepository.findByDeletedAtIsNull(pageable);
    }

    @Override
    public Page<User> findInactive(Pageable pageable) {
        return userRepository.findByDeletedAtIsNotNull(pageable);
    }

    @Override
    public Page<User> findActiveByNameContaining(String name, Pageable pageable) {
        return userRepository.findByNameContainingAndDeletedAtIsNull(name, pageable);
    }

    @Override
    public Page<User> findInactiveByNameContaining(String name, Pageable pageable) {
        return userRepository.findByNameContainingAndDeletedAtIsNotNull(name, pageable);
    }

    @Override
    public User findActiveById(Integer id) {
        return userRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public User findInactiveById(Integer id) {
        return userRepository
                .findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public User findById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, id)));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public User findActiveByLogin(String login) {
        return userRepository
                .findByLoginAndDeletedAtIsNull(login)
                .orElseThrow(() -> new ModelNotFoundException(String.format(ERROR_MESSAGE, login)));
    }

}
