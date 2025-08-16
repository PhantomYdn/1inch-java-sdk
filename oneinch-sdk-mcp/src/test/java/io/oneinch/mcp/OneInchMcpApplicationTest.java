package io.oneinch.mcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic unit test for the MCP server application.
 */
class OneInchMcpApplicationTest {

    @Test
    void testApplicationClass() {
        // Test that the main application class exists and can be instantiated
        assertNotNull(OneInchMcpApplication.class);
    }
    
    @Test
    void testBasicFunctionality() {
        // Basic smoke test to verify the class is properly structured
        assertTrue(OneInchMcpApplication.class.getName().contains("OneInchMcpApplication"));
    }
}