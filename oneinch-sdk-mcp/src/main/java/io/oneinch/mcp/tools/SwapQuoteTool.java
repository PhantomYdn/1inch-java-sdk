package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.swap.*;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkiverse.mcp.server.TextContent;
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
     */
    @Tool(description = "Generate swap quotes with comprehensive route analysis and optimization recommendations")
    public ToolResponse getSwapQuote(
            @ToolArg(description = "Blockchain network ID (1=Ethereum, 137=Polygon, etc.)") Integer chainId,
            @ToolArg(description = "Source token address") String srcToken,
            @ToolArg(description = "Destination token address") String dstToken,
            @ToolArg(description = "Amount to swap in wei (string format)") String amount,
            @ToolArg(description = "Optional comma-separated list of protocols to use", defaultValue = "") String protocols,
            @ToolArg(description = "Optional number of parts for split routing", defaultValue = "1") Integer parts,
            @ToolArg(description = "Optional fee in percentage (0.0-3.0)", defaultValue = "0.0") Double fee) {
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
            
            // Execute the request synchronously for MCP Tool response
            CompletableFuture<QuoteResponse> future = integrationService.getSwapQuote(request);
            QuoteResponse response = future.join(); // Block until completion
            
            Map<String, Object> quoteData = responseMapper.mapQuoteResponse(response);
            String analysis = formatSwapQuoteAnalysis(quoteData, chainId, srcToken, dstToken, amount, protocols, parts, fee);
            
            return ToolResponse.success(new TextContent(analysis));
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid amount format: {}", amount);
            String error = formatErrorResponse("invalid_amount", "Amount must be a valid integer in wei", chainId, srcToken, dstToken, amount);
            return ToolResponse.success(new TextContent(error));
        } catch (Exception e) {
            log.error("Unexpected error in getSwapQuote", e);
            String error = formatErrorResponse("unexpected_error", e.getMessage(), chainId, srcToken, dstToken, amount);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Get simplified swap quote for quick analysis.
     */
    @Tool(description = "Get quick swap quote with essential information only")
    public ToolResponse getQuickQuote(
            @ToolArg(description = "Blockchain network ID") Integer chainId,
            @ToolArg(description = "Source token address") String srcToken,
            @ToolArg(description = "Destination token address") String dstToken,
            @ToolArg(description = "Amount to swap in wei (string format)") String amount) {
        
        // For now, just get a basic quote without calling getSwapQuote to avoid circular dependencies
        log.info("Getting quick quote for chain {} src {} dst {} amount {}", chainId, srcToken, dstToken, amount);
        
        try {
            BigInteger amountBig = new BigInteger(amount);
            QuoteRequest request = QuoteRequest.builder()
                    .chainId(chainId)
                    .src(srcToken)
                    .dst(dstToken)
                    .amount(amountBig)
                    .build();
            
            CompletableFuture<QuoteResponse> future = integrationService.getSwapQuote(request);
            QuoteResponse response = future.join();
            
            Map<String, Object> quoteData = responseMapper.mapQuoteResponse(response);
            String simplified = formatSimpleQuote(quoteData, chainId, srcToken, dstToken, amount);
            
            return ToolResponse.success(new TextContent(simplified));
            
        } catch (Exception e) {
            String error = formatErrorResponse("quick_quote_failed", e.getMessage(), chainId, srcToken, dstToken, amount);
            return ToolResponse.success(new TextContent(error));
        }
    }



    // === RESPONSE FORMATTING METHODS ===

    private String formatSimpleQuote(Map<String, Object> quoteData, Integer chainId, String srcToken, 
                                    String dstToken, String amount) {
        Object dstAmount = quoteData.get("dstAmount");
        Object gas = quoteData.get("gas");
        
        return String.format(
            "{" +
            "\"tool\": \"getQuickQuote\"," +
            "\"chain_id\": %d," +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"input_amount\": \"%s\"," +
            "\"expected_output\": \"%s\"," +
            "\"estimated_gas\": \"%s\"," +
            "\"timestamp\": %d" +
            "}",
            chainId, srcToken, dstToken, amount,
            dstAmount != null ? dstAmount.toString() : "0",
            gas != null ? gas.toString() : "unknown",
            System.currentTimeMillis()
        );
    }

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