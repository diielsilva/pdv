package com.todev.pdv.core.providers.impl;

import com.todev.pdv.core.exceptions.ModelNotFoundException;
import com.todev.pdv.core.models.User;
import com.todev.pdv.core.repositories.UserRepository;
import com.todev.pdv.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;

@ExtendWith(SpringExtension.class)
class UserProviderImplTest {
    @InjectMocks
    private UserProviderImpl userProvider;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUpRepository() {
        when(userRepository.save(any(User.class)))
                .thenReturn(UserFactory.getSavedAdmin());

        when(userRepository.findByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getSavedAdmin())));

        when(userRepository.findByDeletedAtIsNotNull(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getInactiveSavedAdmin())));

        when(userRepository.findByNameContainingAndDeletedAtIsNull(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getSavedAdmin())));

        when(userRepository.findByNameContainingAndDeletedAtIsNotNull(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UserFactory.getInactiveAdmin())));

        when(userRepository.findByIdAndDeletedAtIsNull(anyInt()))
                .thenReturn(Optional.of(UserFactory.getSavedAdmin()));

        when(userRepository.findByIdAndDeletedAtIsNotNull(anyInt()))
                .thenReturn(Optional.of(UserFactory.getInactiveSavedAdmin()));

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(UserFactory.getSavedAdmin()));

        when(userRepository.findByLogin(anyString()))
                .thenReturn(Optional.of(UserFactory.getSavedAdmin()));

        when(userRepository.findByLoginAndDeletedAtIsNull(anyString()))
                .thenReturn(Optional.of(UserFactory.getSavedAdmin()));

    }

    @Test
    void save_UserShouldBeSaved_WhenValidUserWasReceived() {
        assertDoesNotThrow(() -> userProvider.save(UserFactory.getAdmin()));
    }

    @Test
    void findActive_UsersShouldBeReturned_WhenHaveActiveUsers() {
        var users = userProvider.findActive(PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findInactive_UsersShouldBeReturned_WhenHaveInactiveUsers() {
        var users = userProvider.findInactive(PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findActiveByNameContaining_UsersShouldBeReturned_WhenHaveActiveUsersWithNameContaining() {
        var users = userProvider.findActiveByNameContaining("adm", PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findInactiveByNameContaining_UsersShouldBeReturned_WhenHaveInactiveUsersWithNameContaining() {
        var users = userProvider.findInactiveByNameContaining("adm", PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findActiveById_UserShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> userProvider.findActiveById(1));
    }

    @Test
    void findActiveById_UserShouldNotBeReturned_WhenIdWasNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> userProvider.findActiveById(1));
    }

    @Test
    void findInactiveById_UserShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> userProvider.findInactiveById(1));
    }

    @Test
    void findInactiveById_UserShouldNotBeReturned_WhenIdWasNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNotNull(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> userProvider.findInactiveById(1));
    }

    @Test
    void findById_UserShouldBeReturned_WhenIdWasFound() {
        assertDoesNotThrow(() -> userProvider.findById(1));
    }

    @Test
    void findById_UserShouldNotBeReturned_WhenIdWasNotFound() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> userProvider.findById(1));
    }

    @Test
    void findByLogin_UserShouldBeReturned_WhenLoginWasFound() {
        var user = userProvider.findByLogin("admin");
        assertTrue(user.isPresent());
    }

    @Test
    void findActiveByLogin_UserShouldBeReturned_WhenLoginWasFound() {
        assertDoesNotThrow(() -> userProvider.findActiveByLogin("admin"));
    }

    @Test
    void findActiveByLogin_UserShouldNotBeReturned_WhenLoginWasNotFound() {
        when(userRepository.findByLoginAndDeletedAtIsNull(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> userProvider.findActiveByLogin("admins"));
    }
}