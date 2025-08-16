package io.oneinch.mcp.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.oneinch.mcp.service.OneInchClientService;
import io.oneinch.mcp.ratelimit.RateLimitService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central monitoring service for MCP server metrics and performance tracking.
 * Provides comprehensive metrics for observability and operational insights.
 */
@ApplicationScoped
public class MonitoringService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);

    @Inject
    MeterRegistry meterRegistry;

    @Inject
    OneInchClientService clientService;

    @Inject
    RateLimitService rateLimitService;

    // Metrics counters and gauges
    private Counter apiRequestsTotal;
    private Counter apiRequestsSuccessful;
    private Counter apiRequestsFailed;
    private Counter rateLimitHits;
    private Counter cacheHits;
    private Counter cacheMisses;
    private Timer apiResponseTime;
    private Timer cacheAccessTime;
    
    // Runtime metrics
    private final AtomicLong startupTime = new AtomicLong();
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);

    @PostConstruct
    void initializeMetrics() {
        log.info("Initializing monitoring metrics...");
        
        startupTime.set(System.currentTimeMillis());
        
        // API metrics
        apiRequestsTotal = Counter.builder("mcp.api.requests.total")
                .description("Total number of API requests made")
                .register(meterRegistry);
                
        apiRequestsSuccessful = Counter.builder("mcp.api.requests.successful")
                .description("Number of successful API requests")
                .register(meterRegistry);
                
        apiRequestsFailed = Counter.builder("mcp.api.requests.failed")
                .description("Number of failed API requests")
                .register(meterRegistry);
                
        rateLimitHits = Counter.builder("mcp.ratelimit.hits")
                .description("Number of rate limit hits")
                .register(meterRegistry);
                
        // Cache metrics
        cacheHits = Counter.builder("mcp.cache.hits")
                .description("Number of cache hits")
                .register(meterRegistry);
                
        cacheMisses = Counter.builder("mcp.cache.misses")
                .description("Number of cache misses")
                .register(meterRegistry);
        
        // Timing metrics
        apiResponseTime = Timer.builder("mcp.api.response.time")
                .description("API response time distribution")
                .register(meterRegistry);
                
        cacheAccessTime = Timer.builder("mcp.cache.access.time")
                .description("Cache access time distribution")
                .register(meterRegistry);
        
        // Gauge metrics for current state
        Gauge.builder("mcp.uptime.seconds", this, self -> 
                (System.currentTimeMillis() - self.startupTime.get()) / 1000.0)
                .description("MCP server uptime in seconds")
                .register(meterRegistry);
                    
        Gauge.builder("mcp.connections.active", this, self -> 
                (double) self.activeConnections.get())
                .description("Number of active MCP connections")
                .register(meterRegistry);
                
        Gauge.builder("mcp.sdk.client.ready", this, self -> 
                self.clientService.isReady() ? 1.0 : 0.0)
                .description("1inch SDK client readiness (1=ready, 0=not ready)")
                .register(meterRegistry);
        
        log.info("Monitoring metrics initialized successfully");
    }

    // === API METRICS ===

    public void recordApiRequest() {
        apiRequestsTotal.increment();
        totalRequests.incrementAndGet();
    }

    public void recordApiSuccess() {
        apiRequestsSuccessful.increment();
        successfulRequests.incrementAndGet();
    }

    public void recordApiFailure() {
        apiRequestsFailed.increment();
    }

    public void recordApiResponseTime(Duration duration) {
        apiResponseTime.record(duration);
    }

    public Timer.Sample startApiTimer() {
        return Timer.start(meterRegistry);
    }
    
    public Timer getApiResponseTimer() {
        return apiResponseTime;
    }

    // === RATE LIMITING METRICS ===

    public void recordRateLimitHit() {
        rateLimitHits.increment();
    }

    // === CACHE METRICS ===

    public void recordCacheHit() {
        cacheHits.increment();
    }

    public void recordCacheMiss() {
        cacheMisses.increment();
    }

    public void recordCacheAccessTime(Duration duration) {
        cacheAccessTime.record(duration);
    }

    public Timer.Sample startCacheTimer() {
        return Timer.start(meterRegistry);
    }

    // === CONNECTION METRICS ===

    public void recordConnectionOpened() {
        activeConnections.incrementAndGet();
    }

    public void recordConnectionClosed() {
        activeConnections.decrementAndGet();
    }

    // === STATUS INFORMATION ===

    public MonitoringStatus getStatus() {
        return MonitoringStatus.builder()
                .uptime(Duration.ofMillis(System.currentTimeMillis() - startupTime.get()))
                .totalRequests(totalRequests.get())
                .successfulRequests(successfulRequests.get())
                .failureRate(calculateFailureRate())
                .activeConnections(activeConnections.get())
                .sdkClientReady(clientService.isReady())
                .build();
    }

    private double calculateFailureRate() {
        long total = totalRequests.get();
        if (total == 0) return 0.0;
        
        long failed = total - successfulRequests.get();
        return (double) failed / total * 100.0;
    }

    /**
     * Status information for monitoring dashboard.
     */
    public static class MonitoringStatus {
        private final Duration uptime;
        private final long totalRequests;
        private final long successfulRequests;
        private final double failureRate;
        private final int activeConnections;
        private final boolean sdkClientReady;

        private MonitoringStatus(Duration uptime, long totalRequests, long successfulRequests, 
                               double failureRate, int activeConnections, boolean sdkClientReady) {
            this.uptime = uptime;
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failureRate = failureRate;
            this.activeConnections = activeConnections;
            this.sdkClientReady = sdkClientReady;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public Duration getUptime() { return uptime; }
        public long getTotalRequests() { return totalRequests; }
        public long getSuccessfulRequests() { return successfulRequests; }
        public double getFailureRate() { return failureRate; }
        public int getActiveConnections() { return activeConnections; }
        public boolean isSdkClientReady() { return sdkClientReady; }

        public static class Builder {
            private Duration uptime;
            private long totalRequests;
            private long successfulRequests;
            private double failureRate;
            private int activeConnections;
            private boolean sdkClientReady;

            public Builder uptime(Duration uptime) {
                this.uptime = uptime;
                return this;
            }

            public Builder totalRequests(long totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }

            public Builder successfulRequests(long successfulRequests) {
                this.successfulRequests = successfulRequests;
                return this;
            }

            public Builder failureRate(double failureRate) {
                this.failureRate = failureRate;
                return this;
            }

            public Builder activeConnections(int activeConnections) {
                this.activeConnections = activeConnections;
                return this;
            }

            public Builder sdkClientReady(boolean sdkClientReady) {
                this.sdkClientReady = sdkClientReady;
                return this;
            }

            public MonitoringStatus build() {
                return new MonitoringStatus(uptime, totalRequests, successfulRequests,
                        failureRate, activeConnections, sdkClientReady);
            }
        }
    }
}