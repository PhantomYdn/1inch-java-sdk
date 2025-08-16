package io.oneinch.mcp.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests for configuration validation logic.
 */
class ConfigValidatorTest {

    @Test
    void testMaskApiKey() {
        ConfigValidator configValidator = new ConfigValidator();
        
        // Test normal API key
        String apiKey = "abcd1234efgh5678";
        String masked = configValidator.maskApiKey(apiKey);
        assertEquals("abcd***5678", masked);
        
        // Test short API key
        String shortKey = "abc";
        String maskedShort = configValidator.maskApiKey(shortKey);
        assertEquals("***", maskedShort);
        
        // Test null API key
        String maskedNull = configValidator.maskApiKey(null);
        assertEquals("***", maskedNull);
    }

    @Test
    void testConfigValidatorClass() {
        // Simple test to verify the class can be instantiated
        ConfigValidator configValidator = new ConfigValidator();
        assertNotNull(configValidator);
    }
}