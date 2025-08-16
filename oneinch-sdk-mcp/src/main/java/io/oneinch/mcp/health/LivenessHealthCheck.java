package io.oneinch.mcp.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Liveness health check to determine if the MCP server is still alive and should not be restarted.
 * This check focuses on basic application health rather than external dependencies.
 */
@Liveness
@ApplicationScoped
public class LivenessHealthCheck implements HealthCheck {

    private static final Logger log = LoggerFactory.getLogger(LivenessHealthCheck.class);
    
    // Memory usage threshold (90% of max heap)
    private static final double MEMORY_THRESHOLD = 0.90;
    
    // Minimum free memory required (50MB)
    private static final long MIN_FREE_MEMORY = 50 * 1024 * 1024;

    @Override
    public HealthCheckResponse call() {
        try {
            // Check memory usage
            boolean memoryHealthy = isMemoryHealthy();
            
            // Check thread count (detect potential deadlocks or thread leaks)
            boolean threadCountHealthy = isThreadCountHealthy();
            
            // Overall liveness status
            boolean isAlive = memoryHealthy && threadCountHealthy;
            
            // Add diagnostic information
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
            
            long usedHeap = heapUsage.getUsed();
            long maxHeap = heapUsage.getMax();
            double heapUsagePercent = (double) usedHeap / maxHeap * 100.0;
            
            if (isAlive) {
                return HealthCheckResponse.named("mcp-liveness")
                        .up()
                        .withData("status", "alive")
                        .withData("memory_healthy", memoryHealthy)
                        .withData("thread_count_healthy", threadCountHealthy)
                        .withData("heap_used_mb", usedHeap / (1024 * 1024))
                        .withData("heap_max_mb", maxHeap / (1024 * 1024))
                        .withData("heap_usage_percent", String.valueOf(Math.round(heapUsagePercent * 100.0) / 100.0))
                        .withData("non_heap_used_mb", nonHeapUsage.getUsed() / (1024 * 1024))
                        .withData("thread_count", ManagementFactory.getThreadMXBean().getThreadCount())
                        .withData("daemon_thread_count", ManagementFactory.getThreadMXBean().getDaemonThreadCount())
                        .build();
            } else {
                return HealthCheckResponse.named("mcp-liveness")
                        .down()
                        .withData("status", "unhealthy")
                        .withData("memory_healthy", memoryHealthy)
                        .withData("thread_count_healthy", threadCountHealthy)
                        .withData("heap_used_mb", usedHeap / (1024 * 1024))
                        .withData("heap_max_mb", maxHeap / (1024 * 1024))
                        .withData("heap_usage_percent", String.valueOf(Math.round(heapUsagePercent * 100.0) / 100.0))
                        .withData("non_heap_used_mb", nonHeapUsage.getUsed() / (1024 * 1024))
                        .withData("thread_count", ManagementFactory.getThreadMXBean().getThreadCount())
                        .withData("daemon_thread_count", ManagementFactory.getThreadMXBean().getDaemonThreadCount())
                        .build();
            }
                    
        } catch (Exception e) {
            log.error("Error during liveness health check", e);
            return HealthCheckResponse.named("mcp-liveness")
                    .down()
                    .withData("status", "error")
                    .withData("error", e.getMessage())
                    .build();
        }
    }

    private boolean isMemoryHealthy() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            
            long used = heapUsage.getUsed();
            long max = heapUsage.getMax();
            
            // Check if we're using too much memory
            double usageRatio = (double) used / max;
            if (usageRatio > MEMORY_THRESHOLD) {
                log.warn("High memory usage detected: {}% of heap used", Math.round(usageRatio * 100));
                return false;
            }
            
            // Check if we have minimum free memory available
            long free = max - used;
            if (free < MIN_FREE_MEMORY) {
                log.warn("Low free memory: {} MB available", free / (1024 * 1024));
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.warn("Error checking memory health: {}", e.getMessage());
            return false;
        }
    }

    private boolean isThreadCountHealthy() {
        try {
            int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
            
            // Check for unusually high thread count (potential thread leak)
            // Adjust this threshold based on your application's normal thread usage
            int maxExpectedThreads = 100;
            
            if (threadCount > maxExpectedThreads) {
                log.warn("High thread count detected: {} threads", threadCount);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.warn("Error checking thread count: {}", e.getMessage());
            return false;
        }
    }
}