package com.example.service;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CacheServiceTest {

    @Mock
    private StatefulRedisConnection<String, String> redisConnection;

    @Mock
    private RedisCommands<String, String> redisCommands;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisConnection.sync()).thenReturn(redisCommands);
    }

    @Test
    void testGetValueSuccess() {
        // Given
        String key = "testKey";
        String value = "testValue";
        when(redisCommands.get(key)).thenReturn(value);

        // When
        Optional<String> result = cacheService.getValue(key);

        // Then
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
        verify(redisCommands).get(key);
    }

    @Test
    void testGetValueNotFound() {
        // Given
        String key = "nonExistentKey";
        when(redisCommands.get(key)).thenReturn(null);

        // When
        Optional<String> result = cacheService.getValue(key);

        // Then
        assertFalse(result.isPresent());
        verify(redisCommands).get(key);
    }

    @Test
    void testSetValue() {
        // Given
        String key = "testKey";
        String value = "testValue";
        when(redisCommands.set(key, value)).thenReturn("OK");

        // When
        assertDoesNotThrow(() -> cacheService.setValue(key, value));

        // Then
        verify(redisCommands).set(key, value);
    }

    @Test
    void testDeleteValueSuccess() {
        // Given
        String key = "testKey";
        when(redisCommands.del(key)).thenReturn(1L);

        // When
        boolean result = cacheService.deleteValue(key);

        // Then
        assertTrue(result);
        verify(redisCommands).del(key);
    }

    @Test
    void testDeleteValueNotFound() {
        // Given
        String key = "nonExistentKey";
        when(redisCommands.del(key)).thenReturn(0L);

        // When
        boolean result = cacheService.deleteValue(key);

        // Then
        assertFalse(result);
        verify(redisCommands).del(key);
    }

    @Test
    void testKeyExists() {
        // Given
        String key = "existingKey";
        when(redisCommands.exists(key)).thenReturn(1L);

        // When
        boolean result = cacheService.keyExists(key);

        // Then
        assertTrue(result);
        verify(redisCommands).exists(key);
    }

    @Test
    void testKeyNotExists() {
        // Given
        String key = "nonExistentKey";
        when(redisCommands.exists(key)).thenReturn(0L);

        // When
        boolean result = cacheService.keyExists(key);

        // Then
        assertFalse(result);
        verify(redisCommands).exists(key);
    }

    @Test
    void testGetValueException() {
        // Given
        String key = "errorKey";
        when(redisCommands.get(key)).thenThrow(new RuntimeException("Redis error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cacheService.getValue(key);
        });
        
        assertEquals("Failed to get value from cache", exception.getMessage());
        verify(redisCommands).get(key);
    }
}