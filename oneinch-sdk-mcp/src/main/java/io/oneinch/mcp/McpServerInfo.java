package io.oneinch.mcp;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic MCP server component for the 1inch MCP Server.
 * Server metadata is configured via application.properties.
 */
@ApplicationScoped
public class McpServerInfo {

    private static final Logger log = LoggerFactory.getLogger(McpServerInfo.class);

    public McpServerInfo() {
        log.info("1inch MCP Server component initialized");
        log.info("Server provides AI-enhanced read-only access to DeFi data");
        log.info("Supporting 13+ blockchain networks through 1inch ecosystem");
    }
}