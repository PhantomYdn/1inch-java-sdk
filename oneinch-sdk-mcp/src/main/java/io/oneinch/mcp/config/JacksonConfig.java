package io.oneinch.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/**
 * Jackson configuration for safe JSON serialization in MCP tools.
 * Provides ObjectMapper instances for converting API responses to JSON strings.
 */
@ApplicationScoped
public class JacksonConfig {

    /**
     * Provides a configured ObjectMapper for MCP tool responses.
     * Uses snake_case naming for consistency with API responses.
     */
    @Produces
    @Singleton
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure naming strategy for API consistency
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // Configure serialization features
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false); // Compact JSON for MCP
        
        return mapper;
    }
}