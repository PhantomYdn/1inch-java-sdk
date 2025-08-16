package io.oneinch.mcp.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Configuration mapping for MCP server settings.
 * Defines rate limiting and server behavior configuration.
 */
@ConfigMapping(prefix = "mcp")
public interface McpConfig {

    /**
     * Server identification configuration.
     */
    ServerConfig server();

    /**
     * Rate limiting configuration.
     */
    RateLimitConfig rateLimit();

    interface ServerConfig {
        @WithName("name")
        @WithDefault("1inch-mcp-server")
        String name();

        @WithName("version")
        @WithDefault("1.0.0")
        String version();
    }

    interface RateLimitConfig {
        @WithName("requests-per-minute")
        @WithDefault("60")
        int requestsPerMinute();

        @WithName("burst-capacity")
        @WithDefault("10")
        int burstCapacity();
    }
}