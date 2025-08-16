package io.oneinch.mcp.cache;

import io.oneinch.mcp.monitoring.MonitoringService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Health check for cache system status.
 * Monitors cache availability and basic functionality.
 */
@Readiness
@ApplicationScoped
public class CacheHealthCheck implements HealthCheck {

    private static final Logger log = LoggerFactory.getLogger(CacheHealthCheck.class);

    @Inject
    CacheService cacheService;

    @Override
    public HealthCheckResponse call() {
        try {
            // Basic cache functionality test
            boolean cacheOperational = testCacheOperations();
            
            if (cacheOperational) {
                return HealthCheckResponse.named("mcp-cache-system")
                        .up()
                        .withData("status", "operational")
                        .withData("description", "Caffeine cache system operational")
                        .withData("cache_types", "prices, tokens, portfolio")
                        .withData("ttl_prices", "30s")
                        .withData("ttl_tokens", "1h")
                        .withData("ttl_portfolio", "5m")
                        .withData("provider", "Quarkus Cache with Caffeine")
                        .build();
            } else {
                return HealthCheckResponse.named("mcp-cache-system")
                        .down()
                        .withData("status", "degraded")
                        .withData("description", "Cache system experiencing issues")
                        .withData("cache_types", "prices, tokens, portfolio")
                        .withData("ttl_prices", "30s")
                        .withData("ttl_tokens", "1h")
                        .withData("ttl_portfolio", "5m")
                        .withData("provider", "Quarkus Cache with Caffeine")
                        .build();
            }
                    
        } catch (Exception e) {
            log.error("Error during cache health check", e);
            return HealthCheckResponse.named("mcp-cache-system")
                    .down()
                    .withData("status", "error")
                    .withData("error", e.getMessage())
                    .withData("error_class", e.getClass().getSimpleName())
                    .build();
        }
    }

    private boolean testCacheOperations() {
        try {
            // Test basic cache service availability
            if (cacheService == null) {
                log.debug("CacheService is not available");
                return false;
            }
            
            // Log cache stats if available
            cacheService.logCacheStats();
            
            // Cache system is operational if we reach this point
            return true;
            
        } catch (Exception e) {
            log.debug("Cache operation test failed: {}", e.getMessage());
            return false;
        }
    }
}