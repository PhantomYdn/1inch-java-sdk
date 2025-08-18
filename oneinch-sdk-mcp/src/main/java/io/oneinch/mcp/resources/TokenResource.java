package io.oneinch.mcp.resources;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.token.*;
import io.quarkiverse.mcp.server.Resource;
import io.quarkiverse.mcp.server.ResourceTemplate;
import io.quarkiverse.mcp.server.BlobResourceContents;
import io.quarkiverse.mcp.server.RequestUri;
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
     * Get token list for any blockchain network.
     */
    @ResourceTemplate(uriTemplate = "tokens/{chainId}")
    public BlobResourceContents getTokenList(String chainId, RequestUri uri) {
        log.info("Retrieving token list for chain: {}", chainId);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            // Create request for token list
            TokenListRequest request = TokenListRequest.builder()
                    .chainId(chain)
                    .provider("1inch") // Use 1inch as primary provider
                    .build();
            
            // Execute synchronously for MCP Resource
            CompletableFuture<TokenListResponse> future = integrationService.getTokenList(request);
            TokenListResponse response = future.join(); // Block until completion
            
            Map<String, Object> result = responseMapper.mapTokenList(response);
            String content = formatTokenListResponse(result, chain);
            
            return BlobResourceContents.create("tokens/" + chainId, content.getBytes());
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            String error = formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId);
            return BlobResourceContents.create("tokens/" + chainId, error.getBytes());
        } catch (Exception e) {
            log.error("Unexpected error in getTokenList for chain {}", chainId, e);
            String error = formatErrorResponse("unexpected_error", e.getMessage(), chainId);
            return BlobResourceContents.create("tokens/" + chainId, error.getBytes());
        }
    }

    /**
     * Get multi-chain token information.
     */
    @Resource(uri = "tokens/multi-chain")
    public BlobResourceContents getMultiChainTokens() {
        log.info("Retrieving multi-chain token information");
        
        try {
            CompletableFuture<Map<Integer, List<Token>>> future = integrationService.getMultiChainTokens();
            Map<Integer, List<Token>> tokensMap = future.join(); // Block until completion
            
            Map<String, Object> result = responseMapper.mapMultiChainTokens(tokensMap);
            String content = formatMultiChainResponse(result);
            
            return BlobResourceContents.create("tokens/multi-chain", content.getBytes());
            
        } catch (Exception e) {
            log.error("Error retrieving multi-chain tokens: {}", e.getMessage());
            String error = formatErrorResponse("multi_chain_error", e.getMessage(), "all");
            return BlobResourceContents.create("tokens/multi-chain", error.getBytes());
        }
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