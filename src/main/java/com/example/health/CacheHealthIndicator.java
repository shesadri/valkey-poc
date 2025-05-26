package com.example.health;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class CacheHealthIndicator implements HealthIndicator {
    
    @Inject
    private StatefulRedisConnection<String, String> redisConnection;
    
    @Override
    public Publisher<HealthResult> getResult() {
        return Mono.fromCallable(() -> {
            try {
                RedisCommands<String, String> commands = redisConnection.sync();
                String pong = commands.ping();
                
                Map<String, Object> details = new HashMap<>();
                details.put("valkey.response", pong);
                details.put("valkey.connected", true);
                
                return HealthResult.builder("valkey", HealthStatus.UP)
                    .details(details)
                    .build();
                    
            } catch (Exception e) {
                Map<String, Object> details = new HashMap<>();
                details.put("valkey.connected", false);
                details.put("error", e.getMessage());
                
                return HealthResult.builder("valkey", HealthStatus.DOWN)
                    .details(details)
                    .build();
            }
        });
    }
}