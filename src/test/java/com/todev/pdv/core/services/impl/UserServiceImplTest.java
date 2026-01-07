package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.UserRequestBody;
import com.todev.pdv.common.dtos.UserResponseBody;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.builders.UserBuilder;
import com.todev.pdv.core.exceptions.ConstraintConflictException;
import com.todev.pdv.core.exceptions.DependencyInUseException;
import com.todev.pdv.core.exceptions.PermissionDeniedException;
import com.todev.pdv.core.models.User;
import com.todev.pdv.core.providers.contracts.UserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.todev.pdv.core.enums.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserProvider userProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    private static final Pageable PAGE = PageRequest.of(0, 5);

    @BeforeEach
    void setUp() {
        User seller = new UserBuilder()
                .builder()
                .id(1)
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .build();

        Page<User> users = new PageImpl<>(List.of(seller));

        UserResponseBody user = new UserResponseBody(1, "Seller", SELLER, LocalDateTime.now(), null);

        when(userProvider.save(any(User.class))).thenReturn(seller);

        when(userProvider.findActive(any(Pageable.class))).thenReturn(users);

        when(userProvider.findInactive(any(Pageable.class))).thenReturn(users);

        when(userProvider.findActiveByNameContaining(anyString(), any(Pageable.class))).thenReturn(users);

        when(userProvider.findInactiveByNameContaining(anyString(), any(Pageable.class))).thenReturn(users);

        when(userProvider.findActiveById(anyInt())).thenReturn(seller);

        when(userProvider.findInactiveById(anyInt())).thenReturn(seller);

        when(userProvider.findByLogin(anyString())).thenReturn(Optional.empty());

        when(userProvider.findActiveByLogin(anyString())).thenReturn(seller);

        when(passwordEncoder.encode(anyString())).thenReturn("12345");

        when(modelMapper.toModel(any(UserRequestBody.class))).thenReturn(seller);

        when(modelMapper.toDTO(any(User.class))).thenReturn(user);

    }

    @Test
    void save_UserShouldBeSaved() {
        UserRequestBody requestBody = new UserRequestBody("Seller", "seller", "12345", "SELLER");

        assertDoesNotThrow(() -> userService.save(requestBody));
    }

    @Test
    void save_UserShouldNotBeSaved_WhenUserIsAnAdmin() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        when(modelMapper.toModel(any(UserRequestBody.class))).thenReturn(admin);

        UserRequestBody requestBody = new UserRequestBody("Admin", "admin", "12345", "ADMIN");

        assertThrows(PermissionDeniedException.class, () -> userService.save(requestBody));
    }

    @Test
    void save_UserShouldNotBeSaved_WhenLoginIsInUse() {
        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userProvider.findByLogin(anyString())).thenReturn(Optional.of(manager));

        UserRequestBody requestBody = new UserRequestBody("Manager", "manager", "12345", "MANAGER");

        assertThrows(ConstraintConflictException.class, () -> userService.save(requestBody));
    }

    @Test
    void findActive_UsersShouldBeReturned() {
        Page<UserResponseBody> users = userService.findActive(PAGE);

        assertEquals(1, users.getContent().size());
    }

    @Test
    void findInactive_UsersShouldBeReturned() {
        Page<UserResponseBody> users = userService.findInactive(PAGE);

        assertEquals(1, users.getContent().size());
    }

    @Test
    void findActiveByNameContaining_UsersShouldBeReturned_WhenHaveActiveUsersWithNameContaining() {
        Page<UserResponseBody> users = userService.findActiveByNameContaining("Sel", PAGE);

        assertEquals(1, users.getContent().size());
    }

    @Test
    void findInactiveByNameContaining_UsersShouldBeReturned_WhenHaveInactiveUsersWithNameContaining() {
        Page<UserResponseBody> users = userService.findInactiveByNameContaining("Man", PAGE);

        assertEquals(1, users.getContent().size());
    }

    @Test
    void findActiveById_UserShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> userService.findActiveById(1));
    }

    @Test
    void findInactiveById_UserShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> userService.findInactiveById(1));
    }

    @Test
    void update_UserShouldBeUpdated_WhenValidUserWasReceivedAndLoginIsInUseBySameUser() {
        User seller = new UserBuilder()
                .builder()
                .id(1)
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userProvider.findByLogin(anyString())).thenReturn(Optional.of(seller));

        UserRequestBody requestBody = new UserRequestBody("Seller", "seller", "12345", "SELLER");

        assertDoesNotThrow(() -> userService.update("seller", requestBody));
    }

    @Test
    void update_UserShouldBeUpdated_WhenValidUserWasReceivedAndLoginIsNotInUse() {
        UserRequestBody requestBody = new UserRequestBody("Manager", "manager", "12345", "MANAGER");

        assertDoesNotThrow(() -> userService.update("seller", requestBody));
    }

    @Test
    void update_UserShouldNotBeUpdated_WhenLoginIsInUseByAnotherUser() {
        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userProvider.findByLogin(anyString())).thenReturn(Optional.of(manager));

        when(modelMapper.toModel(any(UserRequestBody.class))).thenReturn(manager);

        UserRequestBody requestBody = new UserRequestBody("Manager", "manager", "12345", "MANAGER");

        assertThrows(ConstraintConflictException.class, () -> userService.update("seller", requestBody));
    }

    @Test
    void delete_UserShouldBeDeleted_WhenIdWasFoundAndOnlineUserIsAManager() {
        User manager = new UserBuilder()
                .builder()
                .id(1)
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        User seller = new UserBuilder()
                .builder()
                .id(2)
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userProvider.findActiveByLogin(anyString())).thenReturn(manager);

        when(userProvider.findActiveById(anyInt())).thenReturn(seller);

        assertDoesNotThrow(() -> userService.delete("manager", 2));
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserTriesToDeleteHimself() {
        assertThrows(DependencyInUseException.class, () -> userService.delete("manager", 1));
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserDoesNotHavePermissionToDeleteAnotherManager() {
        User onlineUser = new UserBuilder()
                .builder()
                .id(1)
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        User user = new UserBuilder()
                .builder()
                .id(2)
                .name("Manager")
                .login("manager2")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userProvider.findActiveByLogin(anyString())).thenReturn(onlineUser);

        when(userProvider.findActiveById(anyInt())).thenReturn(user);

        assertThrows(PermissionDeniedException.class, () -> userService.delete("manager", 2));
    }

    @Test
    void reactivate_UserShouldBeReactivated_WhenIdWasFound() {
        assertDoesNotThrow(() -> userService.reactivate(1));
    }
}