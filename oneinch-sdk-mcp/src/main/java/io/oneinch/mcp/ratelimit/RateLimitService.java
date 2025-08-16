package io.oneinch.mcp.ratelimit;

import io.oneinch.mcp.config.McpConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rate limiting service for controlling API request frequency.
 * Implements a token bucket algorithm to ensure compliance with 1inch API limits.
 */
@ApplicationScoped
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    @Inject
    McpConfig mcpConfig;

    private final ConcurrentHashMap<String, RateLimiter> clientLimiters = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Checks if a request is allowed for the given client.
     * 
     * @param clientId the client identifier
     * @return true if request is allowed, false if rate limited
     */
    public boolean isRequestAllowed(String clientId) {
        RateLimiter limiter = getOrCreateLimiter(clientId);
        boolean allowed = limiter.tryConsume();
        
        if (!allowed) {
            log.debug("Rate limit exceeded for client: {}", clientId);
        }
        
        return allowed;
    }

    /**
     * Gets or creates a rate limiter for the specified client.
     * 
     * @param clientId the client identifier
     * @return rate limiter instance
     */
    private RateLimiter getOrCreateLimiter(String clientId) {
        return clientLimiters.computeIfAbsent(clientId, id -> {
            log.debug("Creating rate limiter for client: {}", id);
            return new RateLimiter(
                mcpConfig.rateLimit().requestsPerMinute(),
                mcpConfig.rateLimit().burstCapacity()
            );
        });
    }

    /**
     * Gets the remaining requests for a client.
     * 
     * @param clientId the client identifier
     * @return remaining requests
     */
    public int getRemainingRequests(String clientId) {
        RateLimiter limiter = clientLimiters.get(clientId);
        return limiter != null ? limiter.getAvailableTokens() : mcpConfig.rateLimit().burstCapacity();
    }

    /**
     * Gets the time until next token is available.
     * 
     * @param clientId the client identifier
     * @return seconds until next token, or 0 if tokens are available
     */
    public long getSecondsUntilReset(String clientId) {
        RateLimiter limiter = clientLimiters.get(clientId);
        return limiter != null ? limiter.getSecondsUntilNextToken() : 0;
    }

    /**
     * Clears expired rate limiters to prevent memory leaks.
     */
    public void cleanupExpiredLimiters() {
        lock.lock();
        try {
            long now = Instant.now().getEpochSecond();
            clientLimiters.entrySet().removeIf(entry -> {
                RateLimiter limiter = entry.getValue();
                boolean expired = (now - limiter.getLastAccessTime()) > 3600; // 1 hour
                if (expired) {
                    log.debug("Removing expired rate limiter for client: {}", entry.getKey());
                }
                return expired;
            });
        } finally {
            lock.unlock();
        }
    }

    /**
     * Token bucket rate limiter implementation.
     */
    private static class RateLimiter {
        private final int maxTokens;
        private final double refillRate; // tokens per second
        private final AtomicInteger availableTokens;
        private volatile long lastRefillTime;
        private volatile long lastAccessTime;

        public RateLimiter(int requestsPerMinute, int burstCapacity) {
            this.maxTokens = burstCapacity;
            this.refillRate = requestsPerMinute / 60.0; // convert to per second
            this.availableTokens = new AtomicInteger(burstCapacity);
            this.lastRefillTime = Instant.now().getEpochSecond();
            this.lastAccessTime = this.lastRefillTime;
        }

        public synchronized boolean tryConsume() {
            refillTokens();
            lastAccessTime = Instant.now().getEpochSecond();
            
            if (availableTokens.get() > 0) {
                availableTokens.decrementAndGet();
                return true;
            }
            return false;
        }

        public int getAvailableTokens() {
            refillTokens();
            return availableTokens.get();
        }

        public long getSecondsUntilNextToken() {
            if (availableTokens.get() > 0) {
                return 0;
            }
            
            // Calculate time until next token
            return (long) Math.ceil(1.0 / refillRate);
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        private void refillTokens() {
            long now = Instant.now().getEpochSecond();
            long timePassed = now - lastRefillTime;
            
            if (timePassed > 0) {
                int tokensToAdd = (int) (timePassed * refillRate);
                if (tokensToAdd > 0) {
                    int currentTokens = availableTokens.get();
                    int newTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
                    availableTokens.set(newTokens);
                    lastRefillTime = now;
                }
            }
        }
    }
}