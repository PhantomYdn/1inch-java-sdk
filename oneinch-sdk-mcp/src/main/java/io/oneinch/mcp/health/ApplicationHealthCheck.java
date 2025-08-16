package io.oneinch.mcp.health;

import io.oneinch.mcp.monitoring.MonitoringService;
import io.oneinch.mcp.config.ConfigValidator;
import io.oneinch.mcp.service.OneInchClientService;
import io.oneinch.mcp.ratelimit.RateLimitService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Overall application health check that provides a comprehensive status overview.
 * This is the main health check that aggregates status from all components.
 */
@Readiness
@ApplicationScoped
public class ApplicationHealthCheck implements HealthCheck {

    private static final Logger log = LoggerFactory.getLogger(ApplicationHealthCheck.class);

    @Inject
    MonitoringService monitoringService;

    @Inject
    ConfigValidator configValidator;

    @Inject
    OneInchClientService clientService;

    @Inject
    RateLimitService rateLimitService;

    @Override
    public HealthCheckResponse call() {
        try {
            // Get current monitoring status
            MonitoringService.MonitoringStatus status = monitoringService.getStatus();
            
            // Check all critical components
            boolean configValid = isConfigurationValid();
            boolean clientReady = status.isSdkClientReady();
            boolean rateLimitOperational = isRateLimitOperational();
            boolean acceptableFailureRate = status.getFailureRate() < 25.0; // Less than 25% failure rate
            
            // Determine overall health
            boolean isHealthy = configValid && clientReady && rateLimitOperational && acceptableFailureRate;
            
            // Add comprehensive status data
            if (isHealthy) {
                return HealthCheckResponse.named("mcp-application")
                        .up()
                        .withData("status", "healthy")
                        .withData("uptime_seconds", status.getUptime().toSeconds())
                        .withData("uptime_formatted", formatDuration(status.getUptime()))
                        .withData("total_requests", status.getTotalRequests())
                        .withData("successful_requests", status.getSuccessfulRequests())
                        .withData("failure_rate_percent", String.valueOf(Math.round(status.getFailureRate() * 100.0) / 100.0))
                        .withData("active_connections", status.getActiveConnections())
                        .withData("sdk_client_ready", clientReady)
                        .withData("configuration_valid", configValid)
                        .withData("rate_limiting_operational", rateLimitOperational)
                        .withData("version", "1.0.0")
                        .withData("build_timestamp", System.getProperty("app.build.timestamp", "unknown"))
                        .build();
            } else {
                return HealthCheckResponse.named("mcp-application")
                        .down()
                        .withData("status", "unhealthy")
                        .withData("uptime_seconds", status.getUptime().toSeconds())
                        .withData("uptime_formatted", formatDuration(status.getUptime()))
                        .withData("total_requests", status.getTotalRequests())
                        .withData("successful_requests", status.getSuccessfulRequests())
                        .withData("failure_rate_percent", String.valueOf(Math.round(status.getFailureRate() * 100.0) / 100.0))
                        .withData("active_connections", status.getActiveConnections())
                        .withData("sdk_client_ready", clientReady)
                        .withData("configuration_valid", configValid)
                        .withData("rate_limiting_operational", rateLimitOperational)
                        .withData("version", "1.0.0")
                        .withData("build_timestamp", System.getProperty("app.build.timestamp", "unknown"))
                        .build();
            }
                    
        } catch (Exception e) {
            log.error("Error during application health check", e);
            return HealthCheckResponse.named("mcp-application")
                    .down()
                    .withData("status", "error")
                    .withData("error", e.getMessage())
                    .withData("error_class", e.getClass().getSimpleName())
                    .build();
        }
    }

    private boolean isConfigurationValid() {
        try {
            configValidator.validateConfiguration();
            return true;
        } catch (Exception e) {
            log.debug("Configuration validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isRateLimitOperational() {
        try {
            // Basic check that rate limiting service is working
            // This could be enhanced to check rate limiter state
            return rateLimitService != null;
        } catch (Exception e) {
            log.debug("Rate limiting check failed: {}", e.getMessage());
            return false;
        }
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.toSeconds();
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            long hours = seconds / 3600;
            long remainingSeconds = seconds % 3600;
            long minutes = remainingSeconds / 60;
            long secs = remainingSeconds % 60;
            return hours + "h " + minutes + "m " + secs + "s";
        }
    }
}