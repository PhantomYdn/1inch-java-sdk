package io.oneinch.mcp.config;

import io.smallrye.config.ConfigMapping;
import java.util.List;
import java.util.Optional;

/**
 * Configuration for MCP transport layer enhancements.
 * Supports both stdio and HTTP/SSE transport modes with security and CORS settings.
 */
@ConfigMapping(prefix = "mcp.transport")
public interface McpTransportConfig {

    /**
     * Transport mode configuration
     */
    TransportMode mode();

    /**
     * HTTP/SSE transport specific configuration
     */
    HttpConfig http();

    /**
     * Security configuration for HTTP transports
     */
    SecurityConfig security();

    /**
     * CORS configuration for web clients
     */
    CorsConfig cors();

    enum TransportMode {
        STDIO,
        HTTP_SSE,
        BOTH
    }

    interface HttpConfig {
        
        /**
         * Root path for MCP HTTP endpoints
         * Default: /mcp
         */
        String rootPath();

        /**
         * Server host to bind to
         * Default: 0.0.0.0 (all interfaces)
         */
        String host();

        /**
         * Server port for HTTP transport
         * Default: 8080
         */
        Integer port();

        /**
         * Enable SSE endpoint (deprecated 2024-11-05 protocol)
         * Default: true for backward compatibility
         */
        Boolean enableSse();

        /**
         * Enable Streamable HTTP endpoint (2025-03-26 protocol)
         * Default: true
         */
        Boolean enableStreamable();

        /**
         * Include query parameters from initial request
         * Default: false
         */
        Boolean includeQueryParams();

        /**
         * Request timeout in milliseconds
         * Default: 30000 (30 seconds)
         */
        Integer requestTimeoutMs();

        /**
         * Maximum message size in bytes
         * Default: 1048576 (1MB)
         */
        Integer maxMessageSize();
    }

    interface SecurityConfig {
        
        /**
         * Enable security for HTTP transports
         * Default: true for production
         */
        Boolean enabled();

        /**
         * Authentication method
         */
        AuthMethod authMethod();

        /**
         * API key header name for API key authentication
         * Default: X-API-Key
         */
        String apiKeyHeader();

        /**
         * List of allowed API keys (for development only)
         * Production should use environment variables
         */
        Optional<List<String>> allowedApiKeys();

        /**
         * JWT configuration for JWT authentication
         */
        JwtConfig jwt();

        enum AuthMethod {
            NONE,
            API_KEY,
            JWT,
            BASIC
        }

        interface JwtConfig {
            /**
             * JWT issuer validation
             */
            Optional<String> issuer();

            /**
             * JWT audience validation
             */
            Optional<String> audience();

            /**
             * Public key for JWT verification
             */
            Optional<String> publicKey();

            /**
             * JWK Set URL for JWT verification
             */
            Optional<String> jwksUrl();
        }
    }

    interface CorsConfig {
        
        /**
         * Enable CORS for web clients
         * Default: true for HTTP transport
         */
        Boolean enabled();

        /**
         * Allowed origins for CORS
         * Default: * for development
         */
        List<String> allowedOrigins();

        /**
         * Allowed HTTP methods
         * Default: GET, POST, OPTIONS
         */
        List<String> allowedMethods();

        /**
         * Allowed headers
         * Default: Content-Type, Authorization, X-API-Key
         */
        List<String> allowedHeaders();

        /**
         * Allow credentials in CORS requests
         * Default: true
         */
        Boolean allowCredentials();

        /**
         * Max age for CORS preflight requests in seconds
         * Default: 3600 (1 hour)
         */
        Integer maxAge();
    }
}