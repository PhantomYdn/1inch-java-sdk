package io.oneinch.mcp.service;

import io.oneinch.mcp.config.ConfigValidator;
import io.oneinch.mcp.config.OneInchConfig;
import io.oneinch.sdk.client.OneInchClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing the 1inch SDK client instance.
 * Provides centralized access to the SDK for all MCP components.
 */
@ApplicationScoped
public class OneInchClientService {

    private static final Logger log = LoggerFactory.getLogger(OneInchClientService.class);

    @Inject
    OneInchConfig config;

    @Inject
    ConfigValidator configValidator;

    private OneInchClient client;

    @PostConstruct
    void init() {
        log.info("Initializing 1inch SDK client...");
        
        try {
            // Use validated configuration
            String apiKey = configValidator.getEffectiveApiKey();
            String maskedKey = configValidator.maskApiKey(apiKey);
            
            log.debug("Using API key: {}", maskedKey);
            
            this.client = OneInchClient.builder()
                    .apiKey(apiKey)
                    .build();
            
            log.info("1inch SDK client initialized successfully");
            log.debug("Using API base URL: {}", config.baseUrl());
            
        } catch (Exception e) {
            log.error("Failed to initialize 1inch SDK client", e);
            throw new IllegalStateException("Failed to initialize 1inch SDK client", e);
        }
    }

    @PreDestroy
    void cleanup() {
        if (client != null) {
            try {
                client.close();
                log.info("1inch SDK client closed successfully");
            } catch (Exception e) {
                log.warn("Error closing 1inch SDK client", e);
            }
        }
    }

    /**
     * Get the initialized 1inch SDK client.
     *
     * @return OneInchClient instance
     * @throws IllegalStateException if client is not initialized
     */
    public OneInchClient getClient() {
        if (client == null) {
            throw new IllegalStateException("1inch SDK client not initialized");
        }
        return client;
    }

    /**
     * Check if the client is properly initialized and ready to use.
     *
     * @return true if client is ready, false otherwise
     */
    public boolean isReady() {
        return client != null;
    }
}