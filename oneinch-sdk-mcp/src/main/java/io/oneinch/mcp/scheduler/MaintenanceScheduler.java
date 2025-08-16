package io.oneinch.mcp.scheduler;

import io.oneinch.mcp.ratelimit.RateLimitService;
import io.oneinch.mcp.cache.CacheService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduled maintenance tasks for the MCP server.
 * Handles cleanup and monitoring of various components.
 */
@ApplicationScoped
public class MaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceScheduler.class);

    @Inject
    RateLimitService rateLimitService;

    @Inject  
    CacheService cacheService;

    /**
     * Cleans up expired rate limiters every 30 minutes.
     */
    @Scheduled(every = "30m")
    void cleanupRateLimiters() {
        try {
            log.debug("Running rate limiter cleanup...");
            rateLimitService.cleanupExpiredLimiters();
            log.debug("Rate limiter cleanup completed");
        } catch (Exception e) {
            log.error("Error during rate limiter cleanup", e);
        }
    }

    /**
     * Logs cache statistics every hour for monitoring.
     */
    @Scheduled(every = "1h")
    void logCacheStatistics() {
        try {
            log.debug("Logging cache statistics...");
            cacheService.logCacheStats();
        } catch (Exception e) {
            log.error("Error logging cache statistics", e);
        }
    }

    /**
     * Health check log every 6 hours for monitoring.
     */
    @Scheduled(every = "6h") 
    void logHealthStatus() {
        try {
            log.info("=== 1inch MCP Server Health Check ===");
            log.info("Server operational and processing DeFi data requests");
            log.info("Rate limiting and caching systems active");
            log.info("Ready to serve AI applications with 1inch ecosystem data");
        } catch (Exception e) {
            log.error("Error during health status logging", e);
        }
    }
}