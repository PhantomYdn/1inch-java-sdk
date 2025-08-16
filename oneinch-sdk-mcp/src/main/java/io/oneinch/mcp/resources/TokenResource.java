package io.oneinch.mcp.resources;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.token.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Resource for accessing token information across different blockchains.
 * Provides comprehensive token data including metadata, lists, and search functionality.
 */
@ApplicationScoped
public class TokenResource {

    private static final Logger log = LoggerFactory.getLogger(TokenResource.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Get token list for a specific blockchain.
     * Format: /tokens/{chainId}
     */
    public CompletableFuture<String> getTokenList(String chainId) {
        log.info("Retrieving token list for chain: {}", chainId);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            // Create request for token list
            TokenListRequest request = TokenListRequest.builder()
                    .chainId(chain)
                    .provider("1inch") // Use 1inch as primary provider
                    .build();
            
            return integrationService.getTokenList(request)
                    .thenApply(response -> {
                        Map<String, Object> result = responseMapper.mapTokenList(response);
                        return formatTokenListResponse(result, chain);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving token list for chain {}: {}", chainId, throwable.getMessage());
                        return formatErrorResponse("token_list_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        }
    }

    /**
     * Search tokens across chains or on specific chain.
     * Format: /tokens/{chainId}/search?q={query}
     */
    public CompletableFuture<String> searchTokens(String chainId, String query) {
        log.info("Searching tokens on chain {} with query: {}", chainId, query);
        
        if (query == null || query.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                formatErrorResponse("missing_query", "Search query is required", chainId)
            );
        }
        
        try {
            int chain = Integer.parseInt(chainId);
            
            // Create search request
            TokenSearchRequest request = TokenSearchRequest.builder()
                    .chainId(chain)
                    .query(query.trim())
                    .build();
            
            return integrationService.searchTokens(request)
                    .thenApply(tokens -> {
                        List<Map<String, Object>> tokenMaps = tokens.stream()
                                .map(responseMapper::mapToken)
                                .collect(Collectors.toList());
                        return formatSearchResponse(tokenMaps, query, chain);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error searching tokens on chain {} with query {}: {}", 
                                chainId, query, throwable.getMessage());
                        return formatErrorResponse("token_search_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        }
    }

    /**
     * Get detailed information for specific tokens.
     * Format: /tokens/{chainId}/details/{address}
     */
    public CompletableFuture<String> getTokenDetails(String chainId, String address) {
        log.info("Retrieving token details for {}:{}", chainId, address);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            // Create custom token request
            CustomTokenRequest request = CustomTokenRequest.builder()
                    .chainId(chain)
                    .addresses(List.of(address))
                    .build();
            
            return integrationService.getCustomTokens(request)
                    .thenApply(tokens -> {
                        if (tokens.isEmpty()) {
                            return formatErrorResponse("token_not_found", 
                                "Token not found at address " + address, chainId);
                        }
                        
                        Map<String, Object> tokenData = responseMapper.mapToken(tokens.get(0));
                        return formatTokenDetailsResponse(tokenData, address, chain);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving token details for {}:{}: {}", 
                                chainId, address, throwable.getMessage());
                        return formatErrorResponse("token_details_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        }
    }

    /**
     * Get multi-chain token information.
     * Format: /tokens/multi-chain
     */
    public CompletableFuture<String> getMultiChainTokens() {
        log.info("Retrieving multi-chain token information");
        
        return integrationService.getMultiChainTokens()
                .thenApply(tokensMap -> {
                    Map<String, Object> result = responseMapper.mapMultiChainTokens(tokensMap);
                    return formatMultiChainResponse(result);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving multi-chain tokens: {}", throwable.getMessage());
                    return formatErrorResponse("multi_chain_error", throwable.getMessage(), "all");
                });
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatTokenListResponse(Map<String, Object> result, int chainId) {
        return String.format(
            "{" +
            "\"resource\": \"tokens/%d\"," +
            "\"type\": \"token_list\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"total_tokens\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}", 
            chainId, chainId, 
            getChainName(chainId),
            result.containsKey("tokens") ? ((List<?>) result.get("tokens")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatSearchResponse(List<Map<String, Object>> tokens, String query, int chainId) {
        return String.format(
            "{" +
            "\"resource\": \"tokens/%d/search\"," +
            "\"type\": \"token_search\"," +
            "\"query\": \"%s\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"results_count\": %d," +
            "\"results\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, query, chainId,
            getChainName(chainId),
            tokens.size(),
            toJsonString(tokens),
            System.currentTimeMillis()
        );
    }

    private String formatTokenDetailsResponse(Map<String, Object> tokenData, String address, int chainId) {
        return String.format(
            "{" +
            "\"resource\": \"tokens/%d/details/%s\"," +
            "\"type\": \"token_details\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"address\": \"%s\"," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, address, chainId,
            getChainName(chainId),
            address,
            toJsonString(tokenData),
            System.currentTimeMillis()
        );
    }

    private String formatMultiChainResponse(Map<String, Object> result) {
        return String.format(
            "{" +
            "\"resource\": \"tokens/multi-chain\"," +
            "\"type\": \"multi_chain_tokens\"," +
            "\"supported_chains\": %s," +
            "\"total_tokens\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}",
            toJsonString(result.get("supported_chains")),
            result.containsKey("total_tokens") ? (Integer) result.get("total_tokens") : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"tokens/%s\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
            "\"type\": \"%s\"," +
            "\"message\": \"%s\"," +
            "\"chain_id\": \"%s\"," +
            "\"timestamp\": %d" +
            "}" +
            "}", 
            chainId, errorType, message, chainId, System.currentTimeMillis()
        );
    }

    private String getChainName(int chainId) {
        switch (chainId) {
            case 1:
                return "Ethereum";
            case 56:
                return "BNB Smart Chain";
            case 137:
                return "Polygon";
            case 42161:
                return "Arbitrum One";
            case 10:
                return "Optimism";
            case 43114:
                return "Avalanche C-Chain";
            case 100:
                return "Gnosis Chain";
            case 250:
                return "Fantom";
            case 1313161554:
                return "Aurora";
            case 1284:
                return "Moonbeam";
            case 25:
                return "Cronos";
            case 42220:
                return "Celo";
            case 8217:
                return "Klaytn";
            default:
                return "Chain " + chainId;
        }
    }

    private String toJsonString(Object obj) {
        // Simple JSON serialization - in production, use proper JSON library
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + obj + "\"";
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