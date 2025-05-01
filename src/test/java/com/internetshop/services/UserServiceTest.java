package com.internetshop.services;

import com.internetshop.model.Role;
import com.internetshop.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;
    private MockUserRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockUserRepository();
        service = new UserService(mockRepository);
    }

    @Test
    void authenticate_ShouldReturnUser_WhenCredentialsMatch() throws SQLException {
        // When
        Optional<User> result = service.authenticate("admin", "admin123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
        assertEquals(Role.ADMIN, result.get().getRole());
    }

    @Test
    void authenticate_ShouldReturnEmpty_WhenPasswordWrong() throws SQLException {
        // When
        Optional<User> result = service.authenticate("admin", "wrongpassword");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_ShouldReturnEmpty_WhenUserNotFound() throws SQLException {
        // When
        Optional<User> result = service.authenticate("nonexistent", "password");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void register_ShouldCreateNewCustomer() throws SQLException {
        // Given
        int initialCount = mockRepository.getUserCount();
        User newUser = new User(0, "newuser", "password", null, "new@user.com", "New Address");

        // When
        User registeredUser = service.register(newUser);

        // Then
        assertEquals(initialCount + 1, mockRepository.getUserCount());
        assertEquals(Role.CUSTOMER, registeredUser.getRole());
        assertEquals("newuser", registeredUser.getUsername());
        assertTrue(registeredUser.getId() > 0);
    }

    @Test
    void register_ShouldPreserveAdminRole() throws SQLException {
        // Given
        User adminUser = new User(0, "newadmin", "password", Role.ADMIN, "admin@user.com", "Admin Address");

        // When
        User registeredUser = service.register(adminUser);

        // Then
        assertEquals(Role.ADMIN, registeredUser.getRole());
    }

    @Test
    void register_ShouldThrowWhenUsernameExists() {
        // Given
        User duplicateUser = new User(0, "admin", "password", null, "admin@user.com", "Address");

        // Then
        assertThrows(SQLException.class, () -> service.register(duplicateUser));
    }

    @Test
    void findByUsername_ShouldReturnUser() throws SQLException {
        // When
        Optional<User> result = service.findByUsername("customer");

        // Then
        assertTrue(result.isPresent());
        assertEquals("customer", result.get().getUsername());
    }
}