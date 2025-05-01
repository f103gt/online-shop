package com.internetshop.dao;

import com.internetshop.configurations.DatabaseConfig;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private static UserRepository userRepository;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        DatabaseConfig.createTables();
        userRepository = new UserRepository();
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        DatabaseConfig.dropTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConfig.dropTables();
        DatabaseConfig.createTables();
    }

    @Test
    void testInsertAndGetById() throws SQLException {
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);

        // Get the inserted user by ID
        User retrieved = userRepository.getById(user.getId());
        assertNotNull(retrieved);
        assertEquals(user.getUsername(), retrieved.getUsername());
        assertEquals(user.getPassword(), retrieved.getPassword());
        assertEquals(user.getRole(), retrieved.getRole());
        assertEquals(user.getEmail(), retrieved.getEmail());
        assertEquals(user.getAddress(), retrieved.getAddress());
    }

    @Test
    void testUpdate() throws SQLException {
        // Insert initial user
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);

        // Update the user
        user.setUsername("updateduser");
        user.setPassword("newpassword");
        user.setRole(Role.ADMIN);
        user.setEmail("updated@example.com");
        user.setAddress("456 Updated St");
        userRepository.update(user);

        // Retrieve and verify updates
        User updated = userRepository.getById(user.getId());
        assertEquals("updateduser", updated.getUsername());
        assertEquals("newpassword", updated.getPassword());
        assertEquals(Role.ADMIN, updated.getRole());
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("456 Updated St", updated.getAddress());
    }

    @Test
    void testDelete() throws SQLException {
        // Insert a user
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);

        // Verify exists before deletion
        assertNotNull(userRepository.getById(user.getId()));

        // Delete the user
        userRepository.delete(user.getId());

        // Verify deletion
        assertNull(userRepository.getById(user.getId()));
    }

    @Test
    void testGetAll() throws SQLException {
        User user1 = new User(1, "user1", "pass1", Role.CUSTOMER, "user1@example.com", "Address 1");
        User user2 = new User(2, "user2", "pass2", Role.ADMIN, "user2@example.com", "Address 2");
        userRepository.insert(user1);
        userRepository.insert(user2);

        // Get all users and verify
        List<User> allUsers = userRepository.getAll();
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals("user2")));
    }

    @Test
    void testFindByUsername() throws SQLException {
        // Insert a user
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);

        // Find by username
        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
        assertEquals(user.getUsername(), found.get().getUsername());
        assertEquals(user.getRole(), found.get().getRole());
    }

    @Test
    void testFindByUsernameNotFound() throws SQLException {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testGetByIdNonExistent() throws SQLException {
        assertNull(userRepository.getById(999));
    }

    @Test
    void testGetAllEmpty() throws SQLException {
        List<User> allUsers = userRepository.getAll();
        assertTrue(allUsers.isEmpty());
    }

    @Test
    void testInsertWithInvalidRole() {
        assertDoesNotThrow(() -> {
            User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
            userRepository.insert(user);
        });
    }

    @Test
    void testRoleConversionFromDatabase() throws SQLException {
        User user = new User(1, "adminuser", "adminpass", Role.ADMIN, "admin@example.com", "Admin Address");
        userRepository.insert(user);

        User retrieved = userRepository.getById(user.getId());
        assertEquals(Role.ADMIN, retrieved.getRole());
    }
}