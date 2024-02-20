package com.todev.pdv.core.services.impl;

import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.mappers.contracts.ModelMapper;
import com.todev.pdv.core.exceptions.ConstraintConflictException;
import com.todev.pdv.core.exceptions.DependencyInUseException;
import com.todev.pdv.core.exceptions.PermissionDeniedException;
import com.todev.pdv.core.models.User;
import com.todev.pdv.core.providers.contracts.UserProvider;
import com.todev.pdv.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

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
    private PasswordEncoder BCryptEncoder;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUpUserProvider() {
        when(userProvider.save(any(User.class)))
                .thenReturn(UserFactory.getSavedSeller());

        when(userProvider.findActive(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getSavedAdmin())));

        when(userProvider.findInactive(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getInactiveSavedManager())));

        when(userProvider.findActiveByNameContaining(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getSavedSeller())));

        when(userProvider.findInactiveByNameContaining(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getInactiveSavedManager())));

        when(userProvider.findActiveById(anyInt()))
                .thenReturn(UserFactory.getSavedSeller());

        when(userProvider.findInactiveById(anyInt()))
                .thenReturn(UserFactory.getInactiveSavedManager());

        when(userProvider.findByLogin(anyString()))
                .thenReturn(Optional.empty());

        when(userProvider.findActiveByLogin(anyString()))
                .thenReturn(UserFactory.getSavedSeller());
    }

    @BeforeEach
    void setUpPasswordEncoder() {
        when(BCryptEncoder.encode(anyString()))
                .thenReturn("12345");
    }

    @BeforeEach
    void setUpModelMapper() {
        when(modelMapper.toModel(any(UserRequest.class)))
                .thenReturn(UserFactory.getSeller());

        when(modelMapper.toDTO(any(User.class)))
                .thenReturn(UserFactory.getSavedSellerWithoutIssues());
    }

    @Test
    void save_UserShouldBeSaved_WhenValidUserWasReceived() {
        assertDoesNotThrow(() -> userService.save(UserFactory.getSellerWithoutIssues()));
    }

    @Test
    void save_UserShouldNotBeSaved_WhenUserIsAnAdmin() {
        when(modelMapper.toModel(any(UserRequest.class)))
                .thenReturn(UserFactory.getAdmin());
        var user = UserFactory.getAdminWithoutIssues();
        assertThrows(PermissionDeniedException.class, () -> userService.save(user));
    }

    @Test
    void save_UserShouldNotBeSaved_WhenLoginIsInUse() {
        when(userProvider.findByLogin(anyString()))
                .thenReturn(Optional.of(UserFactory.getSavedManager()));
        var user = UserFactory.getManagerWithoutIssues();
        assertThrows(ConstraintConflictException.class, () -> userService.save(user));
    }

    @Test
    void findActive_UsersShouldBeReturned_WhenHaveActiveUsers() {
        var users = userService.findActive(PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findInactive_UsersShouldBeReturned_WhenHaveInactiveUsers() {
        var users = userService.findInactive(PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findActiveByNameContaining_UsersShouldBeReturned_WhenHaveActiveUsersWithNameContaining() {
        var users = userService.findActiveByNameContaining("Sel", PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findInactiveByNameContaining_UsersShouldBeReturned_WhenHaveInactiveUsersWithNameContaining() {
        var users = userService.findInactiveByNameContaining("Man", PageRequest.of(0, 5));
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
        when(userProvider.findByLogin(anyString()))
                .thenReturn(Optional.of(UserFactory.getSavedSeller()));
        var user = UserFactory.getSellerWithoutIssues();
        assertDoesNotThrow(() -> userService.update("seller", user));
    }

    @Test
    void update_UserShouldBeUpdated_WhenValidUserWasReceivedAndLoginIsNotInUse() {
        var user = UserFactory.getManagerWithoutIssues();
        assertDoesNotThrow(() -> userService.update("seller", user));
    }

    @Test
    void update_UserShouldNotBeUpdated_WhenLoginIsInUseByAnotherUser() {
        when(userProvider.findByLogin(anyString()))
                .thenReturn(Optional.of(UserFactory.getSavedManager()));
        when(modelMapper.toModel(any(UserRequest.class)))
                .thenReturn(UserFactory.getManager());
        var user = UserFactory.getManagerWithoutIssues();
        assertThrows(ConstraintConflictException.class, () -> userService.update("seller", user));
    }

    @Test
    void delete_UserShouldBeDeleted_WhenIdWasFoundAndOnlineUserIsAManager() {
        var user = UserFactory.getSavedSeller();
        user.setId(2);
        when(userProvider.findActiveByLogin(anyString()))
                .thenReturn(UserFactory.getSavedManager());
        when(userProvider.findActiveById(anyInt()))
                .thenReturn(user);
        assertDoesNotThrow(() -> userService.delete("manager", 2));
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserTriesToDeleteHimself() {
        when(userProvider.findActiveByLogin(anyString()))
                .thenReturn(UserFactory.getSavedManager());
        when(userProvider.findActiveById(anyInt()))
                .thenReturn(UserFactory.getSavedManager());
        assertThrows(DependencyInUseException.class, () -> userService.delete("manager", 1));
    }

    @Test
    void delete_UserShouldNotBeDeleted_WhenOnlineUserDoesNotHavePermissionToDeleteAnotherManager() {
        var user = UserFactory.getSavedManager();
        user.setId(2);
        when(userProvider.findActiveByLogin(anyString()))
                .thenReturn(UserFactory.getSavedManager());
        when(userProvider.findActiveById(anyInt()))
                .thenReturn(user);
        assertThrows(PermissionDeniedException.class, () -> userService.delete("manager", 2));
    }

    @Test
    void reactivate_UserShouldBeReactivated_WhenIdWasFound() {
        assertDoesNotThrow(() -> userService.reactivate(1));
    }
}