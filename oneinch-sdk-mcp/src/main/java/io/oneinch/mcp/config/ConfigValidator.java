package io.oneinch.mcp.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Configuration validator for the 1inch MCP Server.
 * Ensures all required configuration is properly set and valid.
 */
@ApplicationScoped
public class ConfigValidator {

    private static final Logger log = LoggerFactory.getLogger(ConfigValidator.class);

    @Inject
    OneInchConfig oneInchConfig;

    @Inject
    McpConfig mcpConfig;

    @ConfigProperty(name = "ONEINCH_API_KEY")
    Optional<String> envApiKey;

    /**
     * Validates all configuration settings.
     * 
     * @throws IllegalStateException if configuration is invalid
     */
    public void validateConfiguration() {
        log.info("Validating configuration...");
        
        validateOneInchConfig();
        validateMcpConfig();
        
        log.info("Configuration validation successful");
    }

    /**
     * Validates 1inch API configuration.
     */
    private void validateOneInchConfig() {
        // Check API key from config
        String apiKey = oneInchConfig.apiKey();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // Check environment variable directly
            if (envApiKey.isEmpty() || envApiKey.get().trim().isEmpty()) {
                throw new IllegalStateException(
                    "1inch API key is required but not configured. " +
                    "Set ONEINCH_API_KEY environment variable or oneinch.api.key property."
                );
            }
            log.info("Using 1inch API key from environment variable");
        } else {
            log.info("Using 1inch API key from configuration");
            
            // Basic API key format validation
            if (apiKey.length() < 10) {
                log.warn("1inch API key appears to be too short. Please verify it's correct.");
            }
        }

        // Validate base URL
        String baseUrl = oneInchConfig.baseUrl();
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalStateException("1inch API base URL is required");
        }
        
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            throw new IllegalStateException("1inch API base URL must start with http:// or https://");
        }
        
        log.debug("1inch API configuration validated: base-url={}", baseUrl);
    }

    /**
     * Validates MCP server configuration.
     */
    private void validateMcpConfig() {
        // Validate server info
        String serverName = mcpConfig.server().name();
        if (serverName == null || serverName.trim().isEmpty()) {
            throw new IllegalStateException("MCP server name is required");
        }

        String serverVersion = mcpConfig.server().version();
        if (serverVersion == null || serverVersion.trim().isEmpty()) {
            throw new IllegalStateException("MCP server version is required");
        }

        // Validate rate limiting
        int requestsPerMinute = mcpConfig.rateLimit().requestsPerMinute();
        if (requestsPerMinute <= 0) {
            throw new IllegalStateException("Rate limit requests per minute must be positive");
        }
        
        if (requestsPerMinute > 1000) {
            log.warn("Rate limit is very high ({}), this may cause issues with 1inch API limits", 
                    requestsPerMinute);
        }

        int burstCapacity = mcpConfig.rateLimit().burstCapacity();
        if (burstCapacity <= 0) {
            throw new IllegalStateException("Rate limit burst capacity must be positive");
        }

        if (burstCapacity > requestsPerMinute) {
            log.warn("Burst capacity ({}) is higher than requests per minute ({})", 
                    burstCapacity, requestsPerMinute);
        }
        
        log.debug("MCP configuration validated: name={}, version={}, rate-limit={}/min", 
                serverName, serverVersion, requestsPerMinute);
    }

    /**
     * Gets the effective API key (from config or environment).
     * 
     * @return the API key to use
     */
    public String getEffectiveApiKey() {
        String configKey = oneInchConfig.apiKey();
        if (configKey != null && !configKey.trim().isEmpty()) {
            return configKey.trim();
        }
        
        return envApiKey.orElseThrow(() -> 
            new IllegalStateException("No valid API key found in configuration or environment")
        ).trim();
    }

    /**
     * Masks the API key for safe logging.
     * 
     * @param apiKey the API key to mask
     * @return masked API key
     */
    public String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        
        return apiKey.substring(0, 4) + "***" + apiKey.substring(apiKey.length() - 4);
    }
}