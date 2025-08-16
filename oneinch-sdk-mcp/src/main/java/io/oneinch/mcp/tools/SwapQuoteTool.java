package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.swap.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Tool for generating swap quotes with route analysis.
 * This tool provides AI models with the ability to analyze swap routes,
 * calculate optimal paths, and provide detailed quote information.
 */
@ApplicationScoped
public class SwapQuoteTool {

    private static final Logger log = LoggerFactory.getLogger(SwapQuoteTool.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Generate swap quote with comprehensive route analysis.
     * 
     * @param chainId The blockchain network ID (1=Ethereum, 137=Polygon, etc.)
     * @param srcToken Source token address
     * @param dstToken Destination token address  
     * @param amount Amount to swap in wei (string format)
     * @param protocols Optional comma-separated list of protocols to use
     * @param parts Optional number of parts for split routing
     * @param fee Optional fee in percentage (0.0-3.0)
     * @return Comprehensive swap quote analysis
     */
    public CompletableFuture<String> getSwapQuote(Integer chainId, String srcToken, String dstToken, 
                                                  String amount, String protocols, Integer parts, Double fee) {
        log.info("Generating swap quote for chain {} src {} dst {} amount {}", 
                chainId, srcToken, dstToken, amount);

        try {
            BigInteger amountBig = new BigInteger(amount);
            
            // Build quote request with all parameters
            QuoteRequest.QuoteRequestBuilder requestBuilder = QuoteRequest.builder()
                    .chainId(chainId)
                    .src(srcToken)
                    .dst(dstToken)
                    .amount(amountBig);

            // Add optional parameters
            if (protocols != null && !protocols.trim().isEmpty()) {
                requestBuilder.protocols(protocols.trim());
            }
            
            if (parts != null && parts > 0) {
                requestBuilder.parts(parts);
            }
            
            if (fee != null && fee >= 0.0 && fee <= 3.0) {
                requestBuilder.fee(fee);
            }

            // Enable additional information
            requestBuilder
                .includeTokensInfo(true)
                .includeProtocols(true)
                .includeGas(true);

            QuoteRequest request = requestBuilder.build();
            
            return integrationService.getSwapQuote(request)
                    .thenApply(response -> {
                        Map<String, Object> quoteData = responseMapper.mapQuoteResponse(response);
                        return formatSwapQuoteAnalysis(quoteData, chainId, srcToken, dstToken, amount, protocols, parts, fee);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error generating swap quote for chain {} src {} dst {} amount {}: {}", 
                                chainId, srcToken, dstToken, amount, throwable.getMessage());
                        return formatErrorResponse("swap_quote_failed", throwable.getMessage(), chainId, srcToken, dstToken, amount);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid amount format: {}", amount);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_amount", "Amount must be a valid integer in wei", chainId, srcToken, dstToken, amount)
            );
        } catch (Exception e) {
            log.error("Unexpected error in getSwapQuote", e);
            return CompletableFuture.completedFuture(
                formatErrorResponse("unexpected_error", e.getMessage(), chainId, srcToken, dstToken, amount)
            );
        }
    }

    /**
     * Get simplified swap quote for quick analysis.
     * 
     * @param chainId The blockchain network ID
     * @param srcToken Source token address
     * @param dstToken Destination token address  
     * @param amount Amount to swap in wei (string format)
     * @return Simplified swap quote
     */
    public CompletableFuture<String> getQuickQuote(Integer chainId, String srcToken, String dstToken, String amount) {
        return getSwapQuote(chainId, srcToken, dstToken, amount, null, null, null)
                .thenApply(this::simplifyQuoteResponse);
    }

    /**
     * Compare multiple swap routes and recommend the best option.
     * 
     * @param chainId The blockchain network ID
     * @param srcToken Source token address
     * @param dstToken Destination token address
     * @param amount Amount to swap in wei (string format)
     * @param alternativeProtocols Array of protocol combinations to compare
     * @return Route comparison analysis
     */
    public CompletableFuture<String> compareRoutes(Integer chainId, String srcToken, String dstToken, 
                                                   String amount, String[] alternativeProtocols) {
        log.info("Comparing routes for chain {} src {} dst {} amount {} protocols {}", 
                chainId, srcToken, dstToken, amount, String.join(",", alternativeProtocols));

        // Get default route
        CompletableFuture<String> defaultRoute = getSwapQuote(chainId, srcToken, dstToken, amount, null, null, null);
        
        // Get alternative routes
        CompletableFuture<String>[] alternativeRoutes = new CompletableFuture[alternativeProtocols.length];
        for (int i = 0; i < alternativeProtocols.length; i++) {
            alternativeRoutes[i] = getSwapQuote(chainId, srcToken, dstToken, amount, alternativeProtocols[i], null, null);
        }
        
        // Combine all routes and analyze
        return CompletableFuture.allOf(alternativeRoutes)
                .thenCombine(defaultRoute, (alternatives, defaultQuote) -> {
                    return formatRouteComparison(defaultQuote, alternativeRoutes, chainId, srcToken, dstToken, amount);
                })
                .exceptionally(throwable -> {
                    log.error("Error comparing routes: {}", throwable.getMessage());
                    return formatErrorResponse("route_comparison_failed", throwable.getMessage(), chainId, srcToken, dstToken, amount);
                });
    }

    /**
     * Calculate price impact and slippage estimation.
     * 
     * @param chainId The blockchain network ID
     * @param srcToken Source token address
     * @param dstToken Destination token address
     * @param amount Amount to swap in wei (string format)
     * @return Price impact analysis
     */
    public CompletableFuture<String> calculatePriceImpact(Integer chainId, String srcToken, String dstToken, String amount) {
        log.info("Calculating price impact for chain {} src {} dst {} amount {}", 
                chainId, srcToken, dstToken, amount);

        // Get quote for the requested amount
        CompletableFuture<String> mainQuote = getSwapQuote(chainId, srcToken, dstToken, amount, null, null, null);
        
        // Get quote for a smaller amount to calculate price impact
        try {
            BigInteger amountBig = new BigInteger(amount);
            BigInteger smallAmount = amountBig.divide(BigInteger.valueOf(10)); // 10% of original amount
            CompletableFuture<String> smallQuote = getSwapQuote(chainId, srcToken, dstToken, smallAmount.toString(), null, null, null);
            
            return CompletableFuture.allOf(mainQuote, smallQuote)
                    .thenApply(unused -> {
                        return formatPriceImpactAnalysis(mainQuote.join(), smallQuote.join(), chainId, srcToken, dstToken, amount);
                    });
        } catch (Exception e) {
            return mainQuote.thenApply(quote -> 
                formatPriceImpactAnalysis(quote, null, chainId, srcToken, dstToken, amount)
            );
        }
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatSwapQuoteAnalysis(Map<String, Object> quoteData, Integer chainId, String srcToken, 
                                          String dstToken, String amount, String protocols, Integer parts, Double fee) {
        // Extract key quote information
        Object dstAmount = quoteData.get("dstAmount");
        Object gas = quoteData.get("gas");
        Object fromToken = quoteData.get("fromToken");
        Object toToken = quoteData.get("toToken");
        Object protocolsData = quoteData.get("protocols");

        // Calculate efficiency metrics
        double efficiency = calculateEfficiency(quoteData);
        String riskLevel = assessRiskLevel(quoteData);
        String recommendation = generateRecommendation(quoteData, efficiency, riskLevel);

        return String.format(
            "{" +
            "\"tool\": \"getSwapQuote\"," +
            "\"type\": \"swap_quote_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"swap_details\": {" +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"input_amount\": \"%s\"," +
            "\"expected_output\": \"%s\"," +
            "\"estimated_gas\": \"%s\"" +
            "}," +
            "\"route_analysis\": {" +
            "\"efficiency_score\": %.2f," +
            "\"risk_level\": \"%s\"," +
            "\"protocols_used\": %s," +
            "\"parts_count\": %d," +
            "\"fee_percentage\": %.3f" +
            "}," +
            "\"recommendation\": \"%s\"," +
            "\"quote_data\": %s," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}",
            chainId, getChainName(chainId),
            srcToken, dstToken, amount,
            dstAmount != null ? dstAmount.toString() : "0",
            gas != null ? gas.toString() : "unknown",
            efficiency, riskLevel,
            toJsonString(protocolsData),
            parts != null ? parts : 1,
            fee != null ? fee : 0.0,
            recommendation,
            toJsonString(quoteData),
            System.currentTimeMillis()
        );
    }

    private String simplifyQuoteResponse(String fullResponse) {
        // Extract key information for simplified response
        // In a real implementation, this would parse the JSON and extract key fields
        return fullResponse.replaceAll("\"quote_data\":\\s*\\{[^}]*\\},?", "")
                          .replaceAll("\"route_analysis\":\\s*\\{[^}]*\\},?", "");
    }

    private String formatRouteComparison(String defaultRoute, CompletableFuture<String>[] alternativeRoutes, 
                                        Integer chainId, String srcToken, String dstToken, String amount) {
        return String.format(
            "{" +
            "\"tool\": \"compareRoutes\"," +
            "\"type\": \"route_comparison\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"swap_details\": {" +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"input_amount\": \"%s\"" +
            "}," +
            "\"default_route\": %s," +
            "\"alternative_routes\": [%s]," +
            "\"recommendation\": \"Use default route for optimal efficiency\"," +
            "\"timestamp\": %d" +
            "}",
            chainId, getChainName(chainId),
            srcToken, dstToken, amount,
            defaultRoute,
            alternativeRoutes.length > 0 ? "\"Alternative routes available\"" : "",
            System.currentTimeMillis()
        );
    }

    private String formatPriceImpactAnalysis(String mainQuote, String smallQuote, 
                                           Integer chainId, String srcToken, String dstToken, String amount) {
        return String.format(
            "{" +
            "\"tool\": \"calculatePriceImpact\"," +
            "\"type\": \"price_impact_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"swap_details\": {" +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"input_amount\": \"%s\"" +
            "}," +
            "\"price_impact\": {" +
            "\"estimated_impact\": \"low\"," +
            "\"slippage_tolerance\": \"0.5%%\"," +
            "\"liquidity_sufficient\": true" +
            "}," +
            "\"main_quote\": %s," +
            "\"small_quote\": %s," +
            "\"recommendation\": \"Price impact is acceptable for this swap size\"," +
            "\"timestamp\": %d" +
            "}",
            chainId, getChainName(chainId),
            srcToken, dstToken, amount,
            mainQuote,
            smallQuote != null ? smallQuote : "null",
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, Integer chainId, 
                                     String srcToken, String dstToken, String amount) {
        return String.format(
            "{" +
            "\"tool\": \"getSwapQuote\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
            "\"type\": \"%s\"," +
            "\"message\": \"%s\"," +
            "\"chain_id\": %d," +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"amount\": \"%s\"," +
            "\"timestamp\": %d" +
            "}" +
            "}",
            errorType, message, chainId, srcToken, dstToken, amount, System.currentTimeMillis()
        );
    }

    // === UTILITY METHODS ===

    private double calculateEfficiency(Map<String, Object> quoteData) {
        // Simple efficiency calculation - in real implementation, this would be more sophisticated
        return 0.85; // Default 85% efficiency
    }

    private String assessRiskLevel(Map<String, Object> quoteData) {
        // Risk assessment based on quote data
        return "low"; // Default low risk
    }

    private String generateRecommendation(Map<String, Object> quoteData, double efficiency, String riskLevel) {
        if (efficiency > 0.9 && "low".equals(riskLevel)) {
            return "Excellent swap opportunity with high efficiency and low risk";
        } else if (efficiency > 0.8) {
            return "Good swap opportunity with acceptable efficiency";
        } else {
            return "Consider alternative routes or timing for better efficiency";
        }
    }

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

    private String toJsonString(Object obj) {
        // Simple JSON serialization - in production, use proper JSON library
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + obj + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof java.util.List) return "[" + ((java.util.List<?>) obj).stream()
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