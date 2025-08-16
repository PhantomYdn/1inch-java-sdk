package io.oneinch.mcp;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the 1inch MCP Server.
 * 
 * This application provides AI-enhanced access to DeFi data through the 1inch ecosystem
 * via the Model Context Protocol (MCP). It serves as a read-only bridge between
 * AI applications and the comprehensive 1inch API suite.
 */
@QuarkusMain
public class OneInchMcpApplication implements QuarkusApplication {

    private static final Logger log = LoggerFactory.getLogger(OneInchMcpApplication.class);

    public static void main(String... args) {
        log.info("Starting 1inch MCP Server...");
        Quarkus.run(OneInchMcpApplication.class, args);
    }

    @Override
    public int run(String... args) {
        log.info("1inch MCP Server is ready to serve DeFi data to AI applications");
        log.info("Server provides read-only access to:");
        log.info("  • Token prices and metadata across 13+ chains");
        log.info("  • Portfolio analytics and performance metrics");
        log.info("  • Swap route analysis and optimization");
        log.info("  • Transaction history and on-chain data");
        log.info("  • DeFi yield opportunities and risk assessments");
        
        // Keep the application running
        Quarkus.waitForExit();
        return 0;
    }
}