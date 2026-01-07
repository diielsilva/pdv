package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.UserRequestBody;
import com.todev.pdv.common.dtos.UserResponseBody;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.exceptions.ConstraintConflictException;
import com.todev.pdv.core.exceptions.DependencyInUseException;
import com.todev.pdv.core.exceptions.PermissionDeniedException;
import com.todev.pdv.core.models.User;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.core.services.contracts.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.todev.pdv.core.enums.Role.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserProvider userProvider;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserProvider userProvider, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userProvider = userProvider;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public UserResponseBody save(UserRequestBody requestBody) {
        User user = modelMapper.toModel(requestBody);
        boolean isAnAdminUser = user.getRole().equals(ADMIN);
        boolean isLoginInUse = isLoginInUse(user.getLogin());
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        if (isAnAdminUser) {
            throw new PermissionDeniedException("Não é possível cadastrar usuários do tipo ADMIN!");
        }

        if (isLoginInUse) {
            throw new ConstraintConflictException(String.format("O login: %s já está em uso!", user.getLogin()));
        }

        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now());

        userProvider.save(user);

        return modelMapper.toResponseBody(user);
    }

    @Override
    public Page<UserResponseBody> findActive(Pageable page) {
        Page<User> users = userProvider.findActive(page);

        return users.map(modelMapper::toResponseBody);
    }

    @Override
    public Page<UserResponseBody> findInactive(Pageable page) {
        Page<User> users = userProvider.findInactive(page);

        return users.map(modelMapper::toResponseBody);
    }

    @Override
    public Page<UserResponseBody> findActiveByNameContaining(String name, Pageable page) {
        Page<User> users = userProvider.findActiveByNameContaining(name, page);

        return users.map(modelMapper::toResponseBody);
    }

    @Override
    public Page<UserResponseBody> findInactiveByNameContaining(String name, Pageable page) {
        Page<User> users = userProvider.findInactiveByNameContaining(name, page);

        return users.map(modelMapper::toResponseBody);
    }

    @Override
    public UserResponseBody findActiveById(Integer id) {
        User user = userProvider.findActiveById(id);

        return modelMapper.toResponseBody(user);
    }

    @Override
    public UserResponseBody findInactiveById(Integer id) {
        User user = userProvider.findInactiveById(id);

        return modelMapper.toResponseBody(user);
    }

    @Transactional
    @Override
    public UserResponseBody update(String onlineUser, UserRequestBody requestBody) {
        User storedUser = userProvider.findActiveByLogin(onlineUser);
        boolean isLoginInUse = isLoginInUse(requestBody.login());
        boolean doesTheNewLoginBelongToAnotherUser = !onlineUser.equals(requestBody.login());
        String encodedPassword = passwordEncoder.encode(requestBody.password());

        if (isLoginInUse && doesTheNewLoginBelongToAnotherUser) {
            throw new ConstraintConflictException(String.format("O login: %s já está em uso!", requestBody.login()));
        }

        storedUser.setName(requestBody.name());
        storedUser.setLogin(requestBody.login());
        storedUser.setPassword(encodedPassword);
        storedUser.setRole(Role.valueOf(requestBody.role()));

        userProvider.save(storedUser);

        return modelMapper.toResponseBody(storedUser);
    }

    @Transactional
    @Override
    public void delete(String onlineUser, Integer id) {
        User user = userProvider.findActiveByLogin(onlineUser);
        User userToDelete = userProvider.findActiveById(id);
        boolean isUserTryingToDeleteItself = user.getId().equals(userToDelete.getId());
        boolean isTheOnlineUserTryingToDeleteAnotherUserWithoutHavingPermissionToIt = user.getRole().equals(MANAGER) && !userToDelete.getRole().equals(SELLER);

        if (isUserTryingToDeleteItself) {
            throw new DependencyInUseException(String.format("O usuário: %s está em uso!", onlineUser));
        }

        if (isTheOnlineUserTryingToDeleteAnotherUserWithoutHavingPermissionToIt) {
            throw new PermissionDeniedException(String.format("O usuário: %s não possui permissão para remover o usuário selecionado!", onlineUser));
        }

        userToDelete.setDeletedAt(LocalDateTime.now());

        userProvider.save(userToDelete);
    }

    @Transactional
    @Override
    public void reactivate(Integer id) {
        var user = userProvider.findInactiveById(id);
        user.setDeletedAt(null);
        userProvider.save(user);
    }

    public boolean isLoginInUse(String login) {
        return userProvider.findByLogin(login).isPresent();
    }
}
