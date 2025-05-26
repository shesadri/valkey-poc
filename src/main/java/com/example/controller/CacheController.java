package com.example.controller;

import com.example.service.CacheService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpStatus;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Controller("/api/v1/cache")
@Tag(name = "Cache", description = "Valkey cache operations")
public class CacheController {
    
    private static final Logger LOG = LoggerFactory.getLogger(CacheController.class);
    
    @Inject
    private CacheService cacheService;
    
    @Get("/{keyId}")
    @Operation(
        summary = "Get value by key",
        description = "Retrieves a value from Valkey cache using the provided key ID"
    )
    @ApiResponse(responseCode = "200", description = "Value found")
    @ApiResponse(responseCode = "404", description = "Key not found")
    @Timed(value = "cache.get.time", description = "Time taken to get value from cache")
    @Counted(value = "cache.get.count", description = "Number of cache get operations")
    public HttpResponse<String> getValue(@Parameter(description = "The key ID to retrieve") @PathVariable String keyId) {
        LOG.info("Getting value for key: {}", keyId);
        
        try {
            Optional<String> value = cacheService.getValue(keyId);
            
            if (value.isPresent()) {
                LOG.info("Value found for key: {}", keyId);
                return HttpResponse.ok(value.get());
            } else {
                LOG.warn("No value found for key: {}", keyId);
                return HttpResponse.notFound();
            }
        } catch (Exception e) {
            LOG.error("Error retrieving value for key: {}", keyId, e);
            return HttpResponse.serverError("Error retrieving value from cache");
        }
    }
    
    @Put("/{keyId}")
    @Operation(
        summary = "Set value by key",
        description = "Sets a value in Valkey cache with the provided key ID"
    )
    @ApiResponse(responseCode = "201", description = "Value set successfully")
    @Timed(value = "cache.set.time", description = "Time taken to set value in cache")
    @Counted(value = "cache.set.count", description = "Number of cache set operations")
    public HttpResponse<String> setValue(
            @Parameter(description = "The key ID to set") @PathVariable String keyId,
            @Body String value) {
        LOG.info("Setting value for key: {}", keyId);
        
        try {
            cacheService.setValue(keyId, value);
            LOG.info("Value set successfully for key: {}", keyId);
            return HttpResponse.created("Value set successfully");
        } catch (Exception e) {
            LOG.error("Error setting value for key: {}", keyId, e);
            return HttpResponse.serverError("Error setting value in cache");
        }
    }
    
    @Delete("/{keyId}")
    @Operation(
        summary = "Delete value by key",
        description = "Deletes a value from Valkey cache using the provided key ID"
    )
    @ApiResponse(responseCode = "200", description = "Value deleted successfully")
    @ApiResponse(responseCode = "404", description = "Key not found")
    @Timed(value = "cache.delete.time", description = "Time taken to delete value from cache")
    @Counted(value = "cache.delete.count", description = "Number of cache delete operations")
    public HttpResponse<String> deleteValue(@Parameter(description = "The key ID to delete") @PathVariable String keyId) {
        LOG.info("Deleting value for key: {}", keyId);
        
        try {
            boolean deleted = cacheService.deleteValue(keyId);
            
            if (deleted) {
                LOG.info("Value deleted successfully for key: {}", keyId);
                return HttpResponse.ok("Value deleted successfully");
            } else {
                LOG.warn("No value found to delete for key: {}", keyId);
                return HttpResponse.notFound("Key not found");
            }
        } catch (Exception e) {
            LOG.error("Error deleting value for key: {}", keyId, e);
            return HttpResponse.serverError("Error deleting value from cache");
        }
    }
}