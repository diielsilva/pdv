package com.todev.pdv.core.repositories;

import com.todev.pdv.factories.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByDeletedAtIsNull_UsersShouldBeReturned_WhenHaveActiveUsers() {
        userRepository.saveAll(List.of(
                UserFactory.getAdmin(),
                UserFactory.getManager(),
                UserFactory.getSeller())
        );
        var users = userRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));
        assertEquals(3, users.getContent().size());
    }

    @Test
    void findByDeletedAtIsNull_UsersShouldNotBeReturned_WhenDoNotHaveActiveUsers() {
        userRepository.saveAll(List.of(
                UserFactory.getInactiveAdmin(),
                UserFactory.getInactiveManager(),
                UserFactory.getInactiveSeller())
        );
        var users = userRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));
        assertTrue(users.isEmpty());
    }

    @Test
    void findByDeletedAtIsNotNull_UsersShouldBeReturned_WhenHaveInactiveUsers() {
        userRepository.saveAll(List.of(
                UserFactory.getInactiveAdmin(),
                UserFactory.getInactiveManager(),
                UserFactory.getInactiveSeller())
        );
        var users = userRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));
        assertEquals(3, users.getContent().size());
    }

    @Test
    void findByDeletedAtIsNotNull_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsers() {
        userRepository.saveAll(List.of(
                UserFactory.getAdmin(),
                UserFactory.getManager(),
                UserFactory.getSeller())
        );
        var users = userRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));
        assertTrue(users.isEmpty());
    }

    @Test
    void findByNameContainingAndDeletedAtIsNull_UsersShouldBeReturned_WhenHaveActiveUsersWithNameContaining() {
        userRepository.save(UserFactory.getAdmin());
        var users = userRepository.findByNameContainingAndDeletedAtIsNull("dm", PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findByNameContainingAndDeletedAtIsNull_UsersShouldNotBeReturned_WhenDoNotHaveActiveUsersWithNameContaining() {
        userRepository.save(UserFactory.getInactiveAdmin());
        var users = userRepository.findByNameContainingAndDeletedAtIsNull("dm", PageRequest.of(0, 5));
        assertTrue(users.isEmpty());
    }

    @Test
    void findByNameContainingAndDeletedAtIsNotNull_UsersShouldBeReturned_WhenHaveInactiveUsersWithNameContaining() {
        userRepository.save(UserFactory.getInactiveManager());
        var users = userRepository.findByNameContainingAndDeletedAtIsNotNull("ger", PageRequest.of(0, 5));
        assertEquals(1, users.getContent().size());
    }

    @Test
    void findByNameContainingAndDeletedAtIsNotNull_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsersWithNameContaining() {
        userRepository.save(UserFactory.getManager());
        var users = userRepository.findByNameContainingAndDeletedAtIsNotNull("dm", PageRequest.of(0, 5));
        assertTrue(users.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNull_UserShouldBeReturned_WhenIdWasFound() {
        var activeUser = userRepository.save(UserFactory.getSeller());
        var activeUserById = userRepository.findByIdAndDeletedAtIsNull(activeUser.getId());
        assertTrue(activeUserById.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNull_UserShouldNotBeReturned_WhenIdWasNotFound() {
        var inactiveUser = userRepository.save(UserFactory.getInactiveSeller());
        var activeUserById = userRepository.findByIdAndDeletedAtIsNull(inactiveUser.getId());
        assertTrue(activeUserById.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_UserShouldBeReturned_WhenIdWasFound() {
        var inactiveUser = userRepository.save(UserFactory.getInactiveAdmin());
        var inactiveUserById = userRepository.findByIdAndDeletedAtIsNotNull(inactiveUser.getId());
        assertTrue(inactiveUserById.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_UserShouldNotBeReturned_WhenIdWasNotFound() {
        var activeUser = userRepository.save(UserFactory.getAdmin());
        var inactiveUserById = userRepository.findByIdAndDeletedAtIsNotNull(activeUser.getId());
        assertTrue(inactiveUserById.isEmpty());
    }

    @Test
    void findByLogin_UserShouldBeReturned_WhenLoginWasFound() {
        userRepository.save(UserFactory.getAdmin());
        var user = userRepository.findByLogin("admin");
        assertTrue(user.isPresent());
    }

    @Test
    void findByLogin_UserShouldNotBeReturned_WhenLoginWasNotFound() {
        userRepository.save(UserFactory.getAdmin());
        var user = userRepository.findByLogin("admins");
        assertTrue(user.isEmpty());
    }

    @Test
    void findByLoginAndDeletedAtIsNull_UserShouldBeReturned_WhenLoginWasFound() {
        userRepository.save(UserFactory.getManager());
        var user = userRepository.findByLoginAndDeletedAtIsNull("manager");
        assertTrue(user.isPresent());
    }

    @Test
    void findByLoginAndDeletedAtIsNull_UserShouldNotBeReturned_WhenLoginWasNotFound() {
        userRepository.save(UserFactory.getInactiveSeller());
        var user = userRepository.findByLoginAndDeletedAtIsNull("seller");
        assertTrue(user.isEmpty());
    }

}