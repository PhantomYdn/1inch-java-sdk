package io.oneinch.mcp.resources;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.portfolio.*;
import io.oneinch.sdk.model.tokendetails.CurrentValueResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Resource for accessing DeFi portfolio data across blockchains.
 * Provides comprehensive portfolio analytics including value tracking, protocol positions, and performance metrics.
 */
@ApplicationScoped
public class PortfolioResource {

    private static final Logger log = LoggerFactory.getLogger(PortfolioResource.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Get current portfolio value breakdown for wallet addresses.
     * Format: /portfolio/{address}
     */
    public CompletableFuture<String> getCurrentValue(String address, String chainId) {
        log.info("Retrieving portfolio value for address {} on chain {}", address, chainId);
        
        try {
            List<String> addresses = List.of(address);
            Integer chain = chainId != null ? Integer.parseInt(chainId) : null;
            
            PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
                    .addresses(addresses)
                    .chainId(chain)
                    .useCache(true)
                    .build();
            
            return integrationService.getPortfolioValue(request)
                    .thenApply(response -> {
                        Map<String, Object> result = responseMapper.mapCurrentValueResponse(response);
                        return formatCurrentValueResponse(result, address, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving portfolio value for {}: {}", address, throwable.getMessage());
                        return formatErrorResponse("portfolio_value_error", throwable.getMessage(), address);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", address)
            );
        }
    }

    /**
     * Get portfolio value for multiple addresses.
     * Format: /portfolio/multiple
     */
    public CompletableFuture<String> getMultiplePortfolios(List<String> addresses, String chainId) {
        log.info("Retrieving portfolio values for {} addresses on chain {}", addresses.size(), chainId);
        
        if (addresses == null || addresses.isEmpty()) {
            return CompletableFuture.completedFuture(
                formatErrorResponse("missing_addresses", "Wallet addresses are required", "multiple")
            );
        }
        
        try {
            Integer chain = chainId != null ? Integer.parseInt(chainId) : null;
            
            PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
                    .addresses(addresses)
                    .chainId(chain)
                    .useCache(true)
                    .build();
            
            return integrationService.getPortfolioValue(request)
                    .thenApply(response -> {
                        Map<String, Object> result = responseMapper.mapCurrentValueResponse(response);
                        return formatMultiplePortfoliosResponse(result, addresses, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving multiple portfolio values: {}", throwable.getMessage());
                        return formatErrorResponse("multiple_portfolios_error", throwable.getMessage(), "multiple");
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", "multiple")
            );
        }
    }

    /**
     * Get protocol snapshot for portfolio positions.
     * Format: /portfolio/{address}/protocols
     */
    public CompletableFuture<String> getProtocolsSnapshot(String address, String chainId, Long timestamp) {
        log.info("Retrieving protocols snapshot for address {} on chain {} at timestamp {}", address, chainId, timestamp);
        
        try {
            List<String> addresses = List.of(address);
            Integer chain = chainId != null ? Integer.parseInt(chainId) : null;
            
            PortfolioV5SnapshotRequest request = PortfolioV5SnapshotRequest.builder()
                    .addresses(addresses)
                    .chainId(chain)
                    .timestamp(timestamp)
                    .build();
            
            return integrationService.getProtocolsSnapshot(request)
                    .thenApply(adapters -> {
                        List<Map<String, Object>> result = adapters.stream()
                                .map(responseMapper::mapAdapterResult)
                                .collect(Collectors.toList());
                        return formatProtocolsSnapshotResponse(result, address, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving protocols snapshot for {}: {}", address, throwable.getMessage());
                        return formatErrorResponse("protocols_snapshot_error", throwable.getMessage(), address);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", address)
            );
        }
    }

    /**
     * Get tokens snapshot for portfolio positions.
     * Format: /portfolio/{address}/tokens
     */
    public CompletableFuture<String> getTokensSnapshot(String address, String chainId, Long timestamp) {
        log.info("Retrieving tokens snapshot for address {} on chain {} at timestamp {}", address, chainId, timestamp);
        
        try {
            List<String> addresses = List.of(address);
            Integer chain = chainId != null ? Integer.parseInt(chainId) : null;
            
            PortfolioV5SnapshotRequest request = PortfolioV5SnapshotRequest.builder()
                    .addresses(addresses)
                    .chainId(chain)
                    .timestamp(timestamp)
                    .build();
            
            return integrationService.getTokensSnapshot(request)
                    .thenApply(adapters -> {
                        List<Map<String, Object>> result = adapters.stream()
                                .map(responseMapper::mapAdapterResult)
                                .collect(Collectors.toList());
                        return formatTokensSnapshotResponse(result, address, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving tokens snapshot for {}: {}", address, throwable.getMessage());
                        return formatErrorResponse("tokens_snapshot_error", throwable.getMessage(), address);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", address)
            );
        }
    }

    /**
     * Get supported chains for portfolio API.
     * Format: /portfolio/chains
     */
    public CompletableFuture<String> getSupportedChains() {
        log.info("Retrieving supported chains for portfolio API");
        
        return integrationService.getSupportedChains()
                .thenApply(chains -> {
                    List<Map<String, Object>> result = chains.stream()
                            .map(responseMapper::mapSupportedChainResponse)
                            .collect(Collectors.toList());
                    return formatSupportedChainsResponse(result);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving supported chains: {}", throwable.getMessage());
                    return formatErrorResponse("supported_chains_error", throwable.getMessage(), "chains");
                });
    }

    /**
     * Get supported protocols for portfolio API.
     * Format: /portfolio/protocols
     */
    public CompletableFuture<String> getSupportedProtocols() {
        log.info("Retrieving supported protocols for portfolio API");
        
        return integrationService.getSupportedProtocols()
                .thenApply(protocols -> {
                    List<Map<String, Object>> result = protocols.stream()
                            .map(responseMapper::mapSupportedProtocolGroupResponse)
                            .collect(Collectors.toList());
                    return formatSupportedProtocolsResponse(result);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving supported protocols: {}", throwable.getMessage());
                    return formatErrorResponse("supported_protocols_error", throwable.getMessage(), "protocols");
                });
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatCurrentValueResponse(Map<String, Object> result, String address, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"portfolio/%s\"," +
            "\"type\": \"current_value\"," +
            "\"address\": \"%s\"," +
            "\"chain_filter\": %s," +
            "\"total_value_usd\": %s," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, address,
            chainId != null ? "\"" + chainId + "\"" : "null",
            result.get("total") != null ? result.get("total").toString() : "0",
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatMultiplePortfoliosResponse(Map<String, Object> result, List<String> addresses, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"portfolio/multiple\"," +
            "\"type\": \"multiple_portfolios\"," +
            "\"addresses\": %s," +
            "\"chain_filter\": %s," +
            "\"total_addresses\": %d," +
            "\"total_value_usd\": %s," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            toJsonString(addresses),
            chainId != null ? "\"" + chainId + "\"" : "null",
            addresses.size(),
            result.get("total") != null ? result.get("total").toString() : "0",
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatProtocolsSnapshotResponse(List<Map<String, Object>> result, String address, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"portfolio/%s/protocols\"," +
            "\"type\": \"protocols_snapshot\"," +
            "\"address\": \"%s\"," +
            "\"chain_filter\": %s," +
            "\"total_protocols\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, address,
            chainId != null ? "\"" + chainId + "\"" : "null",
            result.size(),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatTokensSnapshotResponse(List<Map<String, Object>> result, String address, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"portfolio/%s/tokens\"," +
            "\"type\": \"tokens_snapshot\"," +
            "\"address\": \"%s\"," +
            "\"chain_filter\": %s," +
            "\"total_positions\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, address,
            chainId != null ? "\"" + chainId + "\"" : "null",
            result.size(),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatSupportedChainsResponse(List<Map<String, Object>> result) {
        return String.format(
            "{" +
            "\"resource\": \"portfolio/chains\"," +
            "\"type\": \"supported_chains\"," +
            "\"total_chains\": %d," +
            "\"chains\": %s," +
            "\"timestamp\": %d" +
            "}", 
            result.size(),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatSupportedProtocolsResponse(List<Map<String, Object>> result) {
        return String.format(
            "{" +
            "\"resource\": \"portfolio/protocols\"," +
            "\"type\": \"supported_protocols\"," +
            "\"total_protocols\": %d," +
            "\"protocols\": %s," +
            "\"timestamp\": %d" +
            "}", 
            result.size(),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, String resource) {
        return String.format(
            "{" +
            "\"resource\": \"portfolio/%s\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
                "\"type\": \"%s\"," +
                "\"message\": \"%s\"," +
                "\"timestamp\": %d" +
            "}" +
            "}", 
            resource, errorType, message.replace("\"", "\\\""), System.currentTimeMillis()
        );
    }

    private String toJsonString(Object obj) {
        // Simple JSON serialization - in production, use proper JSON library
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + ((String) obj).replace("\"", "\\\"") + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof List) return "[" + ((List<?>) obj).stream()
                .map(this::toJsonString)
                .collect(Collectors.joining(",")) + "]";
        if (obj instanceof Map) {
            return "{" + ((Map<?, ?>) obj).entrySet().stream()
                    .map(entry -> "\"" + entry.getKey() + "\":" + toJsonString(entry.getValue()))
                    .collect(Collectors.joining(",")) + "}";
        }
        return "\"" + obj.toString() + "\"";
    }
}