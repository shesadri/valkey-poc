package com.example.controller;

import com.example.service.CacheService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@MicronautTest
public class CacheControllerTest {

    @Inject
    @Client("/")
    private HttpClient client;

    @Inject
    private CacheService cacheService;

    @Test
    public void testGetValueFound() {
        // Given
        String key = "testKey";
        String expectedValue = "testValue";
        when(cacheService.getValue(key)).thenReturn(Optional.of(expectedValue));

        // When
        HttpResponse<String> response = client.toBlocking().exchange(
            HttpRequest.GET("/api/v1/cache/" + key), String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedValue, response.body());
    }

    @Test
    public void testGetValueNotFound() {
        // Given
        String key = "nonExistentKey";
        when(cacheService.getValue(key)).thenReturn(Optional.empty());

        // When
        HttpResponse<String> response = client.toBlocking().exchange(
            HttpRequest.GET("/api/v1/cache/" + key), String.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    public void testSetValue() {
        // Given
        String key = "testKey";
        String value = "testValue";

        // When
        HttpResponse<String> response = client.toBlocking().exchange(
            HttpRequest.PUT("/api/v1/cache/" + key, value), String.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Value set successfully", response.body());
    }

    @Test
    public void testDeleteValueSuccess() {
        // Given
        String key = "testKey";
        when(cacheService.deleteValue(key)).thenReturn(true);

        // When
        HttpResponse<String> response = client.toBlocking().exchange(
            HttpRequest.DELETE("/api/v1/cache/" + key), String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Value deleted successfully", response.body());
    }

    @Test
    public void testDeleteValueNotFound() {
        // Given
        String key = "nonExistentKey";
        when(cacheService.deleteValue(key)).thenReturn(false);

        // When
        HttpResponse<String> response = client.toBlocking().exchange(
            HttpRequest.DELETE("/api/v1/cache/" + key), String.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @MockBean(CacheService.class)
    CacheService cacheService() {
        return Mockito.mock(CacheService.class);
    }
}