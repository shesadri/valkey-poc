package com.example.service;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Bean;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Bean
public class CacheService {
    
    private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);
    
    @Inject
    private StatefulRedisConnection<String, String> redisConnection;
    
    public Optional<String> getValue(String key) {
        try {
            RedisCommands<String, String> commands = redisConnection.sync();
            String value = commands.get(key);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            LOG.error("Error getting value for key: {}", key, e);
            throw new RuntimeException("Failed to get value from cache", e);
        }
    }
    
    public void setValue(String key, String value) {
        try {
            RedisCommands<String, String> commands = redisConnection.sync();
            commands.set(key, value);
        } catch (Exception e) {
            LOG.error("Error setting value for key: {}", key, e);
            throw new RuntimeException("Failed to set value in cache", e);
        }
    }
    
    public void setValue(String key, String value, long ttlSeconds) {
        try {
            RedisCommands<String, String> commands = redisConnection.sync();
            commands.setex(key, ttlSeconds, value);
        } catch (Exception e) {
            LOG.error("Error setting value with TTL for key: {}", key, e);
            throw new RuntimeException("Failed to set value with TTL in cache", e);
        }
    }
    
    public boolean deleteValue(String key) {
        try {
            RedisCommands<String, String> commands = redisConnection.sync();
            Long deletedCount = commands.del(key);
            return deletedCount > 0;
        } catch (Exception e) {
            LOG.error("Error deleting value for key: {}", key, e);
            throw new RuntimeException("Failed to delete value from cache", e);
        }
    }
    
    public boolean keyExists(String key) {
        try {
            RedisCommands<String, String> commands = redisConnection.sync();
            Long exists = commands.exists(key);
            return exists > 0;
        } catch (Exception e) {
            LOG.error("Error checking if key exists: {}", key, e);
            throw new RuntimeException("Failed to check key existence in cache", e);
        }
    }
}