package io.oneinch.mcp.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Configuration mapping for 1inch API settings.
 * Provides strongly-typed access to configuration properties.
 */
@ConfigMapping(prefix = "oneinch.api")
public interface OneInchConfig {

    /**
     * 1inch API key for authentication.
     */
    @WithName("key")
    String apiKey();

    /**
     * Base URL for 1inch API endpoints.
     */
    @WithName("base-url")
    @WithDefault("https://api.1inch.dev")
    String baseUrl();
}