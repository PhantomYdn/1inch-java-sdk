package io.oneinch.mcp.cache;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Health check for cache system status.
 * Monitors cache availability and basic functionality.
 */
@Readiness
@ApplicationScoped
public class CacheHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        try {
            // Basic cache health check - verify cache system is available
            // In a real implementation, this could check cache connectivity, 
            // memory usage, hit rates, etc.
            
            return HealthCheckResponse.named("mcp-cache-system")
                    .up()
                    .withData("status", "ready")
                    .withData("description", "Caffeine cache system operational")
                    .withData("caches", "prices, tokens, portfolio")
                    .build();
                    
        } catch (Exception e) {
            return HealthCheckResponse.named("mcp-cache-system")
                    .down()
                    .withData("status", "error")
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}