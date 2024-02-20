package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.dtos.UserResponse;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.enums.Role;
import com.todev.pdv.core.exceptions.ConstraintConflictException;
import com.todev.pdv.core.exceptions.DependencyInUseException;
import com.todev.pdv.core.exceptions.PermissionDeniedException;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.core.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserProvider userProvider;
    private final PasswordEncoder BCryptEncoder;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public UserResponse save(UserRequest requestDTO) {
        var user = modelMapper.toModel(requestDTO);

        if (user.getRole().equals(Role.ADMIN)) {
            throw new PermissionDeniedException("Não é possível cadastrar usuários do tipo ADMIN!");
        }

        if (isLoginInUse(user.getLogin())) {
            throw new ConstraintConflictException(String.format("O login: %s já está em uso!", user.getLogin()));
        }

        user.setPassword(BCryptEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userProvider.save(user);
        return modelMapper.toDTO(user);
    }

    @Override
    public Page<UserResponse> findActive(Pageable pageable) {
        var users = userProvider.findActive(pageable);
        return users.map(modelMapper::toDTO);
    }

    @Override
    public Page<UserResponse> findInactive(Pageable pageable) {
        var users = userProvider.findInactive(pageable);
        return users.map(modelMapper::toDTO);
    }

    @Override
    public Page<UserResponse> findActiveByNameContaining(String name, Pageable pageable) {
        var users = userProvider.findActiveByNameContaining(name, pageable);
        return users.map(modelMapper::toDTO);
    }

    @Override
    public Page<UserResponse> findInactiveByNameContaining(String name, Pageable pageable) {
        var users = userProvider.findInactiveByNameContaining(name, pageable);
        return users.map(modelMapper::toDTO);
    }

    @Override
    public UserResponse findActiveById(Integer id) {
        var user = userProvider.findActiveById(id);
        return modelMapper.toDTO(user);
    }

    @Override
    public UserResponse findInactiveById(Integer id) {
        var user = userProvider.findInactiveById(id);
        return modelMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserResponse update(String onlineLogin, UserRequest requestDTO) {
        var savedUser = userProvider.findActiveByLogin(onlineLogin);
        var newUser = modelMapper.toModel(requestDTO);

        if (isLoginInUse(newUser.getLogin()) && !onlineLogin.equals(newUser.getLogin())) {
            throw new ConstraintConflictException(String.format("O login: %s já está em uso!", newUser.getLogin()));
        }

        savedUser.setName(newUser.getName());
        savedUser.setLogin(newUser.getLogin());
        savedUser.setPassword(BCryptEncoder.encode(newUser.getPassword()));
        savedUser.setRole(newUser.getRole());
        userProvider.save(savedUser);
        return modelMapper.toDTO(savedUser);
    }

    @Transactional
    @Override
    public void delete(String onlineLogin, Integer id) {
        var onlineUser = userProvider.findActiveByLogin(onlineLogin);
        var userToDelete = userProvider.findActiveById(id);

        if (onlineUser.getId().equals(userToDelete.getId())) {
            throw new DependencyInUseException(String.format("O usuário: %s está em uso!", onlineLogin));
        }

        if (onlineUser.getRole().equals(Role.MANAGER) && !userToDelete.getRole().equals(Role.SELLER)) {
            throw new PermissionDeniedException(String.format("O usuário: %s não possui permissão para remover o usuário selecionado!", onlineLogin));
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

    private boolean isLoginInUse(String login) {
        return userProvider
                .findByLogin(login)
                .isPresent();
    }
}
