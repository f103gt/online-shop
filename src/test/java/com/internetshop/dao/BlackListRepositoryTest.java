package com.internetshop.dao;

import com.internetshop.configurations.DatabaseConfig;
import com.internetshop.model.BlackList;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlackListRepositoryTest {
    private static BlackListRepository blackListRepository;
    private static UserRepository userRepository;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        // Create all tables before running tests
        DatabaseConfig.createTables();
        blackListRepository = new BlackListRepository();
        userRepository = new UserRepository();
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        // Drop all tables after tests are done
        DatabaseConfig.dropTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Clear tables before each test
        DatabaseConfig.dropTables();
        DatabaseConfig.createTables();
    }

    private User createTestUser(String username, String email) throws SQLException {
        User user = new User(1, username, "password", Role.CUSTOMER, email, "Test Address");
        userRepository.insert(user);
        return user;
    }

    @Test
    void testInsertAndGetById() throws SQLException {
        // Create a test user first
        User user = createTestUser("testuser", "test@example.com");

        // Create and insert blacklist entry
        BlackList blackList = new BlackList(1, user.getId(), 1);
        blackListRepository.insert(blackList);

        // Retrieve and verify
        BlackList retrieved = blackListRepository.getById(blackList.getId());
        assertNotNull(retrieved);
        assertEquals(blackList.getId(), retrieved.getId());
        assertEquals(user.getId(), retrieved.getUserId());
        assertEquals(1, retrieved.getOrderId());
    }

    @Test
    void testUpdate() throws SQLException {
        // Create a test user first
        User user = createTestUser("testuser", "test@example.com");

        // Create and insert blacklist entry
        BlackList blackList = new BlackList(1, user.getId(), 1);
        blackListRepository.insert(blackList);

        // Update the blacklist entry
        blackList.setOrderId(2);
        blackListRepository.update(blackList);

        // Retrieve and verify update
        BlackList updated = blackListRepository.getById(blackList.getId());
        assertNotNull(updated);
        assertEquals(2, updated.getOrderId());
        assertEquals(user.getId(), updated.getUserId());
    }

    @Test
    void testDelete() throws SQLException {
        // Create a test user first
        User user = createTestUser("testuser", "test@example.com");

        // Create and insert blacklist entry
        BlackList blackList = new BlackList(1, user.getId(), 1);
        blackListRepository.insert(blackList);

        // Verify exists before deletion
        assertNotNull(blackListRepository.getById(blackList.getId()));

        // Delete the entry
        blackListRepository.delete(blackList.getId());

        // Verify deletion
        assertNull(blackListRepository.getById(blackList.getId()));
    }

    @Test
    void testGetAll() throws SQLException {
        User user1 = new User(1, "user1", "password", Role.CUSTOMER,
                "user1@example.com", "Test Address");
        userRepository.insert(user1);
        User user2 = new User(2,"user2", "password", Role.CUSTOMER,
                "user2@example.com", "Test Address");
        userRepository.insert(user2);

        // Create and insert blacklist entries
        BlackList blackList1 = new BlackList(1, user1.getId(), 1);
        BlackList blackList2 = new BlackList(2, user2.getId(), 2);
        blackListRepository.insert(blackList1);
        blackListRepository.insert(blackList2);

        // Get all and verify
        List<BlackList> allBlacklists = blackListRepository.getAll();
        assertEquals(2, allBlacklists.size());
        assertTrue(allBlacklists.stream().anyMatch(b -> b.getUserId() == user1.getId()));
        assertTrue(allBlacklists.stream().anyMatch(b -> b.getUserId() == user2.getId()));
    }

    @Test
    void testGetBlacklistedUsers() throws SQLException {
        // Create test users first
        User user1 = new User(1, "user1", "password", Role.CUSTOMER,
                "user1@example.com", "Test Address");
        userRepository.insert(user1);
        User user2 = new User(2,"user2", "password", Role.CUSTOMER,
                "user2@example.com", "Test Address");
        userRepository.insert(user2);

        // Add only user1 to blacklist
        BlackList blackList = new BlackList(1, user1.getId(), 1);
        blackListRepository.insert(blackList);

        // Get blacklisted users and verify
        List<User> blacklistedUsers = blackListRepository.getBlacklistedUsers();
        assertEquals(1, blacklistedUsers.size());
        assertEquals(user1.getId(), blacklistedUsers.get(0).getId());
        assertEquals(user1.getUsername(), blacklistedUsers.get(0).getUsername());
    }

    @Test
    void testGetByIdNonExistent() throws SQLException {
        assertNull(blackListRepository.getById(999));
    }

    @Test
    void testGetAllEmpty() throws SQLException {
        List<BlackList> allBlacklists = blackListRepository.getAll();
        assertTrue(allBlacklists.isEmpty());
    }

    @Test
    void testGetBlacklistedUsersEmpty() throws SQLException {
        List<User> blacklistedUsers = blackListRepository.getBlacklistedUsers();
        assertTrue(blacklistedUsers.isEmpty());
    }

    @Test
    void testBlackListConstructor() {
        int userId = 1;
        int orderId = 2;
        BlackList blackList = new BlackList(0, userId, orderId);

        assertEquals(0, blackList.getId());
        assertEquals(userId, blackList.getUserId());
        assertEquals(orderId, blackList.getOrderId());
    }

    @Test
    void testBlackListSetters() {
        BlackList blackList = new BlackList(0, 1, 2);

        blackList.setId(10);
        blackList.setUserId(20);
        blackList.setOrderId(30);

        assertEquals(10, blackList.getId());
        assertEquals(20, blackList.getUserId());
        assertEquals(30, blackList.getOrderId());
    }
}