package io.oneinch.mcp.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration startup handler that validates configuration on application startup.
 * Ensures the MCP server fails fast if configuration is invalid.
 */
@ApplicationScoped
public class ConfigurationStartup {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationStartup.class);

    @Inject
    ConfigValidator configValidator;

    /**
     * Handles application startup event to validate configuration early.
     * 
     * @param event startup event
     */
    void onStart(@Observes StartupEvent event) {
        log.info("=== 1inch MCP Server Configuration Validation ===");
        
        try {
            configValidator.validateConfiguration();
            
            // Log configuration summary (with masked sensitive data)
            logConfigurationSummary();
            
            log.info("=== Configuration validation completed successfully ===");
            
        } catch (Exception e) {
            log.error("=== Configuration validation FAILED ===");
            log.error("Configuration error: {}", e.getMessage());
            
            // This will cause the application to fail to start
            throw new RuntimeException("Application startup failed due to invalid configuration", e);
        }
    }

    /**
     * Logs a summary of the current configuration (without sensitive data).
     */
    private void logConfigurationSummary() {
        try {
            String maskedApiKey = configValidator.maskApiKey(configValidator.getEffectiveApiKey());
            log.info("Configuration Summary:");
            log.info("  • 1inch API Key: {}", maskedApiKey);
            log.info("  • Server ready to provide AI-enhanced DeFi data access");
            log.info("  • Supporting 13+ blockchain networks");
            log.info("  • Read-only operations ensure security");
            
        } catch (Exception e) {
            log.warn("Could not generate configuration summary: {}", e.getMessage());
        }
    }
}