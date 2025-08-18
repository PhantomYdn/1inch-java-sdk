package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.token.*;
import io.oneinch.sdk.model.price.*;
import io.oneinch.sdk.model.price.Currency;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkiverse.mcp.server.TextContent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Tool for comprehensive token analysis.
 * This tool provides AI models with the ability to analyze tokens,
 * get pricing data, market metrics, and provide detailed token information.
 */
@ApplicationScoped
public class TokenAnalysisTool {

    private static final Logger log = LoggerFactory.getLogger(TokenAnalysisTool.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;
    
    @Inject
    ObjectMapper objectMapper;

    /**
     * Analyze a specific token with comprehensive market data.
     */
    @Tool(description = "Analyze a specific token with comprehensive market data, pricing, and metrics")
    public ToolResponse analyzeToken(
            @ToolArg(description = "Blockchain network ID") String chainId,
            @ToolArg(description = "Token contract address") String tokenAddress,
            @ToolArg(description = "Currency for price data", defaultValue = "USD") String currency,
            @ToolArg(description = "Include advanced metrics and analysis", defaultValue = "false") String includeMetrics) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            String priceCurrency = currency != null && !currency.trim().isEmpty() ? 
                currency.trim().toUpperCase() : "USD";
            boolean withMetrics = includeMetrics != null && !includeMetrics.trim().isEmpty() ? 
                Boolean.parseBoolean(includeMetrics.trim()) : false;
                
            log.info("Analyzing token {} on chain {} with currency {}", tokenAddress, parsedChainId, priceCurrency);

            // Prepare response data structure
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("tool", "analyzeToken");
            responseData.put("chain_id", parsedChainId);
            responseData.put("chain_name", getChainName(parsedChainId));
            responseData.put("token_address", tokenAddress.toLowerCase());
            responseData.put("currency", priceCurrency);
            responseData.put("include_metrics", withMetrics);
            responseData.put("timestamp", System.currentTimeMillis());

            try {
                // Get token details using custom token API
                CustomTokenRequest tokenRequest = CustomTokenRequest.builder()
                    .chainId(parsedChainId)
                    .addresses(List.of(tokenAddress))
                    .build();

                CompletableFuture<List<Token>> tokensFuture = integrationService.getCustomTokens(tokenRequest);
                List<Token> tokensResponse = tokensFuture.join();

                if (tokensResponse != null && !tokensResponse.isEmpty()) {
                    Token tokenData = tokensResponse.get(0); // Get first token from the list
                    
                    if (tokenData != null) {
                        Map<String, Object> tokenInfo = new HashMap<>();
                        tokenInfo.put("symbol", tokenData.getSymbol());
                        tokenInfo.put("name", tokenData.getName());
                        tokenInfo.put("decimals", tokenData.getDecimals());
                        tokenInfo.put("logo_uri", tokenData.getLogoURI());
                        
                        responseData.put("token_info", tokenInfo);
                        responseData.put("status", "success");
                        
                        // Try to get price data using single token price method
                        try {
                            Currency currencyEnum = "USD".equals(priceCurrency) ? Currency.USD : Currency.EUR;
                            CompletableFuture<BigInteger> priceFuture = integrationService.getSingleTokenPrice(parsedChainId, tokenAddress, currencyEnum);
                            BigInteger priceResponse = priceFuture.join();
                            
                            if (priceResponse != null) {
                                // Convert wei price to readable format
                                double priceValue = priceResponse.doubleValue() / Math.pow(10, 18);
                                
                                Map<String, Object> priceData = new HashMap<>();
                                priceData.put("price", priceValue);
                                priceData.put("currency", priceCurrency);
                                priceData.put("raw_value", priceResponse.toString());
                                responseData.put("price_data", priceData);
                                
                                // Calculate basic metrics if requested
                                if (withMetrics && tokenData.getDecimals() != null) {
                                    Map<String, Object> metrics = new HashMap<>();
                                    metrics.put("decimals", tokenData.getDecimals());
                                    metrics.put("price_precision", String.format("1e-%d", tokenData.getDecimals()));
                                    metrics.put("current_price_usd", priceValue);
                                    metrics.put("token_standard", "ERC20");
                                    responseData.put("metrics", metrics);
                                }
                            } else {
                                responseData.put("price_data", Map.of("status", "unavailable"));
                            }
                            
                        } catch (Exception priceE) {
                            log.warn("Could not retrieve price data for token {}: {}", tokenAddress, priceE.getMessage());
                            responseData.put("price_data", Map.of("error", priceE.getMessage()));
                        }
                        
                    } else {
                        responseData.put("status", "token_not_found");
                        responseData.put("message", "Token details not found");
                    }
                } else {
                    responseData.put("status", "no_data");
                    responseData.put("message", "No token data available");
                }
                
            } catch (Exception tokenE) {
                log.warn("Could not retrieve token data for {}: {}", tokenAddress, tokenE.getMessage());
                responseData.put("status", "error");
                responseData.put("error", tokenE.getMessage());
            }
            
            // Convert to JSON using Jackson
            try {
                String jsonResponse = objectMapper.writeValueAsString(responseData);
                return ToolResponse.success(new TextContent(jsonResponse));
            } catch (JsonProcessingException e) {
                log.error("Error serializing token analysis response to JSON", e);
                String fallbackResponse = String.format(
                    "{\"tool\": \"analyzeToken\", \"error\": \"JSON serialization failed\", \"token_address\": \"%s\", \"timestamp\": %d}",
                    tokenAddress, System.currentTimeMillis()
                );
                return ToolResponse.success(new TextContent(fallbackResponse));
            }
            
        } catch (Exception e) {
            log.error("Error analyzing token {} on chain {}", tokenAddress, chainId, e);
            String error = formatErrorResponse("token_analysis_failed", e.getMessage(), null, tokenAddress);
            return ToolResponse.success(new TextContent(error));
        }
    }







    private String formatErrorResponse(String errorType, String message, Integer chainId, String tokenIdentifier) {
        return String.format(
            "{" +
            "\"tool\": \"analyzeToken\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
            "\"type\": \"%s\"," +
            "\"message\": \"%s\"," +
            "\"chain_id\": %s," +
            "\"token_identifier\": \"%s\"," +
            "\"timestamp\": %d" +
            "}" +
            "}",
            errorType, message, chainId != null ? chainId.toString() : "null", tokenIdentifier, System.currentTimeMillis()
        );
    }

    // === UTILITY METHODS ===

    private String getChainName(Integer chainId) {
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
}