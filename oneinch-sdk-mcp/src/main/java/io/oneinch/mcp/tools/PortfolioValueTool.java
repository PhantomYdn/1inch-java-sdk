package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.portfolio.*;
import io.oneinch.sdk.model.tokendetails.CurrentValueResponse;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkiverse.mcp.server.TextContent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Tool for portfolio valuation and metrics.
 * This tool provides AI models with the ability to analyze DeFi portfolios,
 * calculate valuations, track performance, and provide portfolio insights.
 */
@ApplicationScoped
public class PortfolioValueTool {

    private static final Logger log = LoggerFactory.getLogger(PortfolioValueTool.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Get comprehensive portfolio valuation for one or more addresses.
     */
    @Tool(description = "Get comprehensive portfolio valuation with breakdown by protocols, chains, and tokens")
    public ToolResponse getPortfolioValue(
            @ToolArg(description = "Comma-separated wallet addresses to analyze") String addresses,
            @ToolArg(description = "Optional specific chain ID (1=Ethereum, 137=Polygon, etc.)", defaultValue = "null") String chainId,
            @ToolArg(description = "Include detailed P&L and performance metrics", defaultValue = "true") String includeMetrics) {
        try {
            // Parse string parameters
            String[] addressArray = addresses.split(",");
            Integer parsedChainId = chainId != null && !chainId.trim().isEmpty() && !chainId.equals("null") ? 
                Integer.parseInt(chainId.trim()) : null;
            boolean metrics = includeMetrics != null && !includeMetrics.trim().isEmpty() ? 
                Boolean.parseBoolean(includeMetrics.trim()) : true;
                
            log.info("Getting portfolio value for addresses {} on chain {}", String.join(",", addressArray), parsedChainId);

            // Build portfolio request
            PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
                    .addresses(List.of(addressArray))
                    .chainId(parsedChainId)
                    .build();

            CompletableFuture<CurrentValueResponse> future = integrationService.getPortfolioValue(request);
            CurrentValueResponse portfolioResponse = future.join(); // Block for MCP Tool response
            
            String result = formatBasicPortfolioValue(portfolioResponse, addressArray, parsedChainId);
            
            return ToolResponse.success(new TextContent(result));
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            String error = formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", addresses.split(","), null);
            return ToolResponse.success(new TextContent(error));
        } catch (Exception e) {
            log.error("Unexpected error in getPortfolioValue", e);
            String error = formatErrorResponse("unexpected_error", e.getMessage(), addresses.split(","), null);
            return ToolResponse.success(new TextContent(error));
        }
    }






    // === HELPER METHODS ===


    // === RESPONSE FORMATTING METHODS ===

    private String formatBasicPortfolioValue(CurrentValueResponse portfolioResponse, String[] addresses, Integer chainId) {
        return String.format(
            "{" +
            "\"tool\": \"getPortfolioValue\"," +
            "\"type\": \"basic_portfolio_value\"," +
            "\"addresses\": [%s]," +
            "\"chain_id\": %s," +
            "\"chain_name\": \"%s\"," +
            "\"portfolio_summary\": {" +
            "\"total_value_usd\": %.2f," +
            "\"address_count\": %d," +
            "\"chains_analyzed\": %s" +
            "}," +
            "\"valuation_data\": %s," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}",
            String.join("\",\"", addresses),
            chainId != null ? chainId.toString() : "null",
            chainId != null ? getChainName(chainId) : "Multi-chain",
            portfolioResponse.getTotal() != null ? portfolioResponse.getTotal() : 0.0,
            addresses.length,
            chainId != null ? "1" : "multiple",
            toJsonString(responseMapper.mapCurrentValueResponse(portfolioResponse)),
            System.currentTimeMillis()
        );
    }



    private String formatErrorResponse(String errorType, String message, String[] addresses, Integer chainId) {
        return String.format(
            "{" +
            "\"tool\": \"getPortfolioValue\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
            "\"type\": \"%s\"," +
            "\"message\": \"%s\"," +
            "\"addresses\": [%s]," +
            "\"chain_id\": %s," +
            "\"timestamp\": %d" +
            "}" +
            "}",
            errorType, message,
            String.join("\",\"", addresses),
            chainId != null ? chainId.toString() : "null",
            System.currentTimeMillis()
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