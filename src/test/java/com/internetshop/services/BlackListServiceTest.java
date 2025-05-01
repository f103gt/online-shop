package com.internetshop.services;

import com.internetshop.model.BlackList;
import com.internetshop.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlackListServiceTest {
    private BlackListService service;
    private MockBlackListRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockBlackListRepository();
        service = new BlackListService(mockRepository);
    }

    @Test
    void addToBlackList_ShouldAddNewEntry() throws SQLException {
        // Given
        int initialCount = mockRepository.getBlackListCount();
        int userId = 103;
        int orderId = 1003;

        // When
        service.addToBlackList(userId, orderId);

        // Then
        assertEquals(initialCount + 1, mockRepository.getBlackListCount());
        BlackList lastEntry = mockRepository.getAll().get(initialCount);
        assertEquals(userId, lastEntry.getUserId());
        assertEquals(orderId, lastEntry.getOrderId());
    }

    @Test
    void removeFromBlackList_ShouldRemoveExistingEntry() throws SQLException {
        // Given
        int initialCount = mockRepository.getBlackListCount();
        int idToRemove = 1;

        // When
        service.removeFromBlackList(idToRemove);

        // Then
        assertEquals(initialCount - 1, mockRepository.getBlackListCount());
        assertThrows(SQLException.class, () -> mockRepository.getById(idToRemove));
    }

    @Test
    void removeFromBlackList_ShouldThrowWhenNotFound() {
        // Given
        int nonExistentId = 999;

        // Then
        assertThrows(SQLException.class, () -> service.removeFromBlackList(nonExistentId));
    }

    @Test
    void getBlacklistedUsers_ShouldReturnAllBlacklistedUsers() throws SQLException {
        // When
        List<User> users = service.getBlacklistedUsers();

        // Then
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("user2", users.get(1).getUsername());
    }

    @Test
    void getBlacklistedUsers_ShouldReturnEmptyListWhenNoUsers() throws SQLException {
        MockBlackListRepository emptyRepository = new MockBlackListRepository() {
            @Override
            public List<User> getBlacklistedUsers() {
                return List.of();
            }
        };
        BlackListService emptyService = new BlackListService(emptyRepository);

        // When
        List<User> users = emptyService.getBlacklistedUsers();

        // Then
        assertTrue(users.isEmpty());
    }
}