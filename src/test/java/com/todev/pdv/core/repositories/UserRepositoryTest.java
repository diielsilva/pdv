package com.todev.pdv.core.repositories;

import com.todev.pdv.core.builders.UserBuilder;
import com.todev.pdv.core.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.todev.pdv.core.enums.Role.*;
import static org.junit.jupiter.api.Assertions.*;
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
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        User seller = new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.saveAll(List.of(admin, manager, seller));

        Page<User> users = userRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));

        assertEquals(3, users.getContent().size());
    }

    @Test
    void findByDeletedAtIsNull_UsersShouldNotBeReturned_WhenDoNotHaveActiveUsers() {
        User seller = new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.save(seller);

        Page<User> users = userRepository.findByDeletedAtIsNull(PageRequest.of(0, 5));

        assertTrue(users.isEmpty());
    }

    @Test
    void findByDeletedAtIsNotNull_UsersShouldBeReturned_WhenHaveInactiveUsers() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        User seller = new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.saveAll(List.of(admin, manager, seller));

        Page<User> users = userRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));

        assertEquals(3, users.getContent().size());
    }

    @Test
    void findByDeletedAtIsNotNull_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsers() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        User seller = new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.saveAll(List.of(admin, manager, seller));

        Page<User> users = userRepository.findByDeletedAtIsNotNull(PageRequest.of(0, 5));

        assertTrue(users.isEmpty());
    }

    @Test
    void findByNameContainingAndDeletedAtIsNull_UsersShouldBeReturned_WhenHaveActiveUsersWithNameContaining() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        Page<User> users = userRepository.findByNameContainingAndDeletedAtIsNull("dm", PageRequest.of(0, 5));

        assertAll(() -> {
            assertEquals(1, users.getContent().size());
            assertTrue(users.getContent().getFirst().getName().contains("dm"));
        });
    }

    @Test
    void findByNameContainingAndDeletedAtIsNull_UsersShouldNotBeReturned_WhenDoNotHaveActiveUsersWithNameContaining() {
        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.save(manager);

        Page<User> users = userRepository.findByNameContainingAndDeletedAtIsNull("ger", PageRequest.of(0, 5));

        assertTrue(users.isEmpty());
    }

    @Test
    void findByNameContainingAndDeletedAtIsNotNull_UsersShouldBeReturned_WhenHaveInactiveUsersWithNameContaining() {
        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.save(manager);

        Page<User> users = userRepository.findByNameContainingAndDeletedAtIsNotNull("ger", PageRequest.of(0, 5));

        assertAll(() -> {
            assertEquals(1, users.getContent().size());
            assertTrue(users.getContent().getFirst().getName().contains("ger"));
        });
    }

    @Test
    void findByNameContainingAndDeletedAtIsNotNull_UsersShouldNotBeReturned_WhenDoNotHaveInactiveUsersWithNameContaining() {
        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(manager);

        Page<User> users = userRepository.findByNameContainingAndDeletedAtIsNotNull("man", PageRequest.of(0, 5));

        assertTrue(users.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNull_UserShouldBeReturned_WhenIdWasFound() {
        User seller = new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(seller);

        Optional<User> searchedUser = userRepository.findByIdAndDeletedAtIsNull(seller.getId());

        assertTrue(searchedUser.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNull_UserShouldNotBeReturned_WhenIdWasNotFound() {
        User seller = new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.save(seller);

        Optional<User> searchedUser = userRepository.findByIdAndDeletedAtIsNull(seller.getId());

        assertTrue(searchedUser.isEmpty());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_UserShouldBeReturned_WhenIdWasFound() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        Optional<User> searchedUser = userRepository.findByIdAndDeletedAtIsNotNull(admin.getId());

        assertTrue(searchedUser.isPresent());
    }

    @Test
    void findByIdAndDeletedAtIsNotNull_UserShouldNotBeReturned_WhenIdWasNotFound() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        Optional<User> searchedUser = userRepository.findByIdAndDeletedAtIsNotNull(admin.getId());

        assertTrue(searchedUser.isEmpty());
    }

    @Test
    void findByLogin_UserShouldBeReturned_WhenLoginWasFound() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        Optional<User> searchedUser = userRepository.findByLogin("admin");

        assertTrue(searchedUser.isPresent());
    }

    @Test
    void findByLogin_UserShouldNotBeReturned_WhenLoginWasNotFound() {
        User admin = new UserBuilder()
                .builder()
                .name("Admin")
                .login("admin")
                .password("12345")
                .role(ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        Optional<User> searchedUser = userRepository.findByLogin("admins");

        assertTrue(searchedUser.isEmpty());
    }

    @Test
    void findByLoginAndDeletedAtIsNull_UserShouldBeReturned_WhenLoginWasFound() {
        User manager = new UserBuilder()
                .builder()
                .name("Manager")
                .login("manager")
                .password("12345")
                .role(MANAGER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(manager);

        Optional<User> searchedUser = userRepository.findByLoginAndDeletedAtIsNull("manager");

        assertTrue(searchedUser.isPresent());
    }

    @Test
    void findByLoginAndDeletedAtIsNull_UserShouldNotBeReturned_WhenLoginWasNotFound() {
        User seller = new UserBuilder()
                .builder()
                .name("Seller")
                .login("seller")
                .password("12345")
                .role(SELLER)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.save(seller);

        Optional<User> searchedUser = userRepository.findByLoginAndDeletedAtIsNull("seller");

        assertTrue(searchedUser.isEmpty());
    }

}