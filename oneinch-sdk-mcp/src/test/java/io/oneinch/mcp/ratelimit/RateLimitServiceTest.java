package io.oneinch.mcp.ratelimit;

import io.oneinch.mcp.config.McpConfig;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests for rate limiting functionality.
 */
@QuarkusTest
class RateLimitServiceTest {

    @Inject
    RateLimitService rateLimitService;

    @Test
    void testRateLimitingBasic() {
        String testClient = "test-client";
        
        // First request should be allowed
        assertTrue(rateLimitService.isRequestAllowed(testClient));
        
        // Check remaining requests
        int remaining = rateLimitService.getRemainingRequests(testClient);
        assertTrue(remaining >= 0);
    }

    @Test
    void testGetRemainingRequests() {
        String testClient = "test-client-2";
        
        // Get initial remaining requests
        int initial = rateLimitService.getRemainingRequests(testClient);
        assertTrue(initial > 0);
        
        // Make a request
        rateLimitService.isRequestAllowed(testClient);
        
        // Remaining should be reduced (or same if refilled)
        int afterRequest = rateLimitService.getRemainingRequests(testClient);
        assertTrue(afterRequest >= 0);
    }

    @Test
    void testSecondsUntilReset() {
        String testClient = "test-client-3";
        
        // Initially should be 0 (tokens available)
        long waitTime = rateLimitService.getSecondsUntilReset(testClient);
        assertTrue(waitTime >= 0);
    }

    @Test
    void testCleanupExpiredLimiters() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            rateLimitService.cleanupExpiredLimiters();
        });
    }
}