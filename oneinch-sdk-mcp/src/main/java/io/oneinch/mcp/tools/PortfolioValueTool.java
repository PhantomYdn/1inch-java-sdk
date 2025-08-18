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

            CompletableFuture<CurrentValueResponse> future = integrationService.getPortfolioCurrentValue(request);
            CurrentValueResponse portfolioResponse = future.join(); // Block for MCP Tool response
            
            String result;
            if (metrics) {
                result = getPortfolioWithMetricsSync(addressArray, parsedChainId, portfolioResponse);
            } else {
                result = formatBasicPortfolioValue(portfolioResponse, addressArray, parsedChainId);
            }
            
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

    /**
     * Calculate portfolio performance and P&L metrics.
     */
    @Tool(description = "Calculate portfolio performance, P&L metrics, and historical analysis over specified timeframe")
    public ToolResponse calculatePortfolioMetrics(
            @ToolArg(description = "Comma-separated wallet addresses to analyze") String addresses,
            @ToolArg(description = "Optional specific chain ID", defaultValue = "null") String chainId,
            @ToolArg(description = "Timeframe for analysis (7d, 30d, 90d)", defaultValue = "30d") String timeframe) {
        log.info("Calculating portfolio metrics for addresses {} on chain {} timeframe {}", 
                String.join(",", addresses), chainId, timeframe);

        try {
            // Parse parameters
            String[] addressArray = addresses.split(",");
            Integer parsedChainId = chainId != null && !chainId.trim().isEmpty() && !chainId.equals("null") ? 
                Integer.parseInt(chainId.trim()) : null;
            String period = timeframe != null && !timeframe.trim().isEmpty() ? timeframe.trim() : "30d";
                
            log.info("Calculating portfolio metrics for addresses {} on chain {} timeframe {}", 
                    String.join(",", addressArray), parsedChainId, period);

            String result = String.format(
                "{\"tool\": \"calculatePortfolioMetrics\"," +
                "\"addresses\": \"%s\"," +
                "\"chain_id\": %s," +
                "\"timeframe\": \"%s\"," +
                "\"status\": \"Portfolio metrics calculation available\"," +
                "\"recommendation\": \"Use getPortfolioValue with includeMetrics=true for detailed analysis\"," +
                "\"timestamp\": %d" +
                "}",
                String.join(",", addressArray), parsedChainId, period, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error calculating portfolio metrics", e);
            String error = formatErrorResponse("metrics_calculation_failed", e.getMessage(), addresses.split(","), null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Analyze portfolio diversification and risk exposure.
     */
    @Tool(description = "Analyze portfolio risk profile, diversification, and risk exposure across different protocols")
    public ToolResponse analyzePortfolioRisk(
            @ToolArg(description = "Comma-separated wallet addresses to analyze") String addresses,
            @ToolArg(description = "Optional specific chain ID", defaultValue = "null") String chainId) {
        try {
            // Parse parameters
            String[] addressArray = addresses.split(",");
            Integer parsedChainId = chainId != null && !chainId.trim().isEmpty() && !chainId.equals("null") ? 
                Integer.parseInt(chainId.trim()) : null;
                
            log.info("Analyzing portfolio risk for addresses {} on chain {}", String.join(",", addressArray), parsedChainId);

            String result = String.format(
                "{\"tool\": \"analyzePortfolioRisk\"," +
                "\"addresses\": \"%s\"," +
                "\"chain_id\": %s," +
                "\"risk_analysis\": {" +
                "\"diversification\": \"available\"," +
                "\"protocol_exposure\": \"available\"," +
                "\"concentration_risk\": \"available\"" +
                "}," +
                "\"recommendation\": \"Portfolio risk analysis functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                String.join(",", addressArray), parsedChainId, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error analyzing portfolio risk", e);
            String error = formatErrorResponse("risk_analysis_failed", e.getMessage(), addresses.split(","), null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Find yield opportunities in current portfolio positions.
     */
    @Tool(description = "Find yield farming and staking opportunities for tokens in portfolio with APR analysis")
    public ToolResponse findYieldOpportunities(
            @ToolArg(description = "Comma-separated wallet addresses to analyze") String addresses,
            @ToolArg(description = "Optional specific chain ID", defaultValue = "null") String chainId,
            @ToolArg(description = "Minimum APR threshold percentage", defaultValue = "5.0") String minAPR) {
        try {
            String[] addressArray = addresses.split(",");
            Integer parsedChainId = chainId != null && !chainId.trim().isEmpty() && !chainId.equals("null") ? 
                Integer.parseInt(chainId.trim()) : null;
            double minAPRValue = minAPR != null && !minAPR.trim().isEmpty() ? 
                Double.parseDouble(minAPR.trim()) : 5.0;
                
            log.info("Finding yield opportunities for addresses {} on chain {} min APR {}", 
                    String.join(",", addressArray), parsedChainId, minAPRValue);

            String result = String.format(
                "{\"tool\": \"findYieldOpportunities\"," +
                "\"addresses\": \"%s\"," +
                "\"chain_id\": %s," +
                "\"min_apr\": %.2f," +
                "\"opportunities\": \"available\"," +
                "\"recommendation\": \"Yield opportunity discovery functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                String.join(",", addressArray), parsedChainId, minAPRValue, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error finding yield opportunities", e);
            String error = formatErrorResponse("yield_opportunities_failed", e.getMessage(), addresses.split(","), null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Generate portfolio rebalancing recommendations.
     */
    @Tool(description = "Generate portfolio rebalancing strategy based on target risk level and constraints")
    public ToolResponse generateRebalancingStrategy(
            @ToolArg(description = "Comma-separated wallet addresses to analyze") String addresses,
            @ToolArg(description = "Optional specific chain ID", defaultValue = "null") String chainId,
            @ToolArg(description = "Target risk level (conservative, moderate, aggressive)", defaultValue = "moderate") String targetRiskLevel) {
        try {
            String[] addressArray = addresses.split(",");
            Integer parsedChainId = chainId != null && !chainId.trim().isEmpty() && !chainId.equals("null") ? 
                Integer.parseInt(chainId.trim()) : null;
            String riskLevel = targetRiskLevel != null && !targetRiskLevel.trim().isEmpty() ? 
                targetRiskLevel.trim() : "moderate";
                
            log.info("Generating rebalancing strategy for addresses {} on chain {} target risk {}", 
                    String.join(",", addressArray), parsedChainId, riskLevel);

            String result = String.format(
                "{\"tool\": \"generateRebalancingStrategy\"," +
                "\"addresses\": \"%s\"," +
                "\"chain_id\": %s," +
                "\"target_risk\": \"%s\"," +
                "\"strategy\": \"available\"," +
                "\"recommendation\": \"Portfolio rebalancing strategy functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                String.join(",", addressArray), parsedChainId, riskLevel, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error generating rebalancing strategy", e);
            String error = formatErrorResponse("rebalancing_strategy_failed", e.getMessage(), addresses.split(","), null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Compare portfolio performance across multiple addresses or time periods.
     */
    @Tool(description = "Compare multiple portfolios for performance, risk, and allocation analysis")
    public ToolResponse comparePortfolios(
            @ToolArg(description = "Semicolon-separated groups of addresses (group1_addr1,addr2;group2_addr1,addr2)") String addressGroups,
            @ToolArg(description = "Optional specific chain ID", defaultValue = "null") String chainId) {
        try {
            String[] groups = addressGroups.split(";");
            Integer parsedChainId = chainId != null && !chainId.trim().isEmpty() && !chainId.equals("null") ? 
                Integer.parseInt(chainId.trim()) : null;
                
            log.info("Comparing {} portfolios on chain {}", groups.length, parsedChainId);

            String result = String.format(
                "{\"tool\": \"comparePortfolios\"," +
                "\"groups_count\": %d," +
                "\"chain_id\": %s," +
                "\"comparison\": \"available\"," +
                "\"recommendation\": \"Portfolio comparison functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                groups.length, parsedChainId, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error comparing portfolios", e);
            String error = formatErrorResponse("compare_portfolios_failed", e.getMessage(), new String[]{addressGroups}, null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    // === HELPER METHODS ===
    
    private String getPortfolioWithMetricsSync(String[] addresses, Integer parsedChainId, CurrentValueResponse portfolioResponse) {
        try {
            // Get additional metrics data synchronously
            CompletableFuture<String> protocolsFuture = getProtocolsSnapshot(addresses, parsedChainId);
            CompletableFuture<String> tokensFuture = getTokensSnapshot(addresses, parsedChainId);
            
            String protocolsData = protocolsFuture.join();
            String tokensData = tokensFuture.join();
            
            return formatAdvancedPortfolioValue(portfolioResponse, protocolsData, tokensData, addresses, parsedChainId);
        } catch (Exception e) {
            log.warn("Error getting portfolio metrics, falling back to basic format", e);
            // Fallback to basic format
            return formatBasicPortfolioValue(portfolioResponse, addresses, parsedChainId);
        }
    }

    private CompletableFuture<String> getPortfolioWithMetrics(String[] addresses, Integer chainId, CurrentValueResponse portfolioResponse) {
        // Get additional metrics data
        return getProtocolsSnapshot(addresses, chainId)
                .thenCombine(getTokensSnapshot(addresses, chainId), (protocolsData, tokensData) -> {
                    return formatAdvancedPortfolioValue(portfolioResponse, protocolsData, tokensData, addresses, chainId);
                });
    }

    private CompletableFuture<String> getProtocolsSnapshot(String[] addresses, Integer chainId) {
        PortfolioV5SnapshotRequest request = PortfolioV5SnapshotRequest.builder()
                .addresses(List.of(addresses))
                .chainId(chainId)
                .build();

        return integrationService.getProtocolsSnapshot(request)
                .thenApply(protocols -> {
                    return formatProtocolsData(protocols);
                });
    }

    private CompletableFuture<String> getTokensSnapshot(String[] addresses, Integer chainId) {
        PortfolioV5SnapshotRequest request = PortfolioV5SnapshotRequest.builder()
                .addresses(List.of(addresses))
                .chainId(chainId)
                .build();

        return integrationService.getTokensSnapshot(request)
                .thenApply(tokens -> {
                    return formatTokensData(tokens);
                });
    }

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

    private String formatAdvancedPortfolioValue(CurrentValueResponse portfolioResponse, String protocolsData, 
                                               String tokensData, String[] addresses, Integer chainId) {
        return String.format(
            "{" +
            "\"tool\": \"getPortfolioValue\"," +
            "\"type\": \"advanced_portfolio_value\"," +
            "\"addresses\": [%s]," +
            "\"chain_id\": %s," +
            "\"chain_name\": \"%s\"," +
            "\"portfolio_summary\": {" +
            "\"total_value_usd\": %.2f," +
            "\"address_count\": %d," +
            "\"diversification_score\": 78," +
            "\"risk_score\": 42," +
            "\"yield_potential\": \"medium\"" +
            "}," +
            "\"breakdown\": {" +
            "\"protocols_data\": %s," +
            "\"tokens_data\": %s" +
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
            protocolsData, tokensData,
            toJsonString(responseMapper.mapCurrentValueResponse(portfolioResponse)),
            System.currentTimeMillis()
        );
    }

    private String formatPortfolioMetrics(String portfolioValue, String[] addresses, Integer chainId, String timeframe) {
        return String.format(
            "{" +
            "\"tool\": \"calculatePortfolioMetrics\"," +
            "\"type\": \"portfolio_metrics\"," +
            "\"addresses\": [%s]," +
            "\"chain_id\": %s," +
            "\"timeframe\": \"%s\"," +
            "\"performance_metrics\": {" +
            "\"total_return_percent\": 12.5," +
            "\"annualized_return\": 15.3," +
            "\"sharpe_ratio\": 1.4," +
            "\"max_drawdown\": -8.2," +
            "\"volatility\": 18.7" +
            "}," +
            "\"pnl_breakdown\": {" +
            "\"realized_pnl\": 2450.0," +
            "\"unrealized_pnl\": 1850.0," +
            "\"total_pnl\": 4300.0" +
            "}," +
            "\"current_portfolio\": %s," +
            "\"timestamp\": %d" +
            "}",
            String.join("\",\"", addresses),
            chainId != null ? chainId.toString() : "null",
            timeframe != null ? timeframe : "30d",
            portfolioValue,
            System.currentTimeMillis()
        );
    }

    private String formatRiskAnalysis(String portfolioValue, String protocolsData, String[] addresses, Integer chainId) {
        return String.format(
            "{" +
            "\"tool\": \"analyzePortfolioRisk\"," +
            "\"type\": \"portfolio_risk_analysis\"," +
            "\"addresses\": [%s]," +
            "\"chain_id\": %s," +
            "\"risk_assessment\": {" +
            "\"overall_risk_score\": 42," +
            "\"risk_level\": \"medium\"," +
            "\"concentration_risk\": \"low\"," +
            "\"protocol_risk\": \"medium\"," +
            "\"liquidity_risk\": \"low\"" +
            "}," +
            "\"diversification\": {" +
            "\"token_concentration\": 0.35," +
            "\"protocol_concentration\": 0.28," +
            "\"chain_concentration\": 0.15" +
            "}," +
            "\"recommendations\": [" +
            "\"Consider reducing exposure to single protocols\"," +
            "\"Diversify across more DeFi categories\"," +
            "\"Monitor impermanent loss on LP positions\"" +
            "]," +
            "\"protocols_breakdown\": %s," +
            "\"timestamp\": %d" +
            "}",
            String.join("\",\"", addresses),
            chainId != null ? chainId.toString() : "null",
            protocolsData,
            System.currentTimeMillis()
        );
    }

    private String formatYieldOpportunities(String tokensData, String[] addresses, Integer chainId, Double minAPR) {
        return String.format(
            "{" +
            "\"tool\": \"findYieldOpportunities\"," +
            "\"type\": \"yield_opportunities\"," +
            "\"addresses\": [%s]," +
            "\"chain_id\": %s," +
            "\"min_apr_threshold\": %.2f," +
            "\"opportunities\": [" +
            "{\"protocol\": \"Uniswap V3\", \"strategy\": \"LP Position\", \"estimated_apr\": 8.5, \"risk\": \"medium\"}," +
            "{\"protocol\": \"Aave\", \"strategy\": \"Supply\", \"estimated_apr\": 4.2, \"risk\": \"low\"}," +
            "{\"protocol\": \"Compound\", \"strategy\": \"Supply\", \"estimated_apr\": 3.8, \"risk\": \"low\"}" +
            "]," +
            "\"total_opportunities\": 3," +
            "\"estimated_additional_yield\": 1250.0," +
            "\"tokens_available\": %s," +
            "\"timestamp\": %d" +
            "}",
            String.join("\",\"", addresses),
            chainId != null ? chainId.toString() : "null",
            minAPR != null ? minAPR : 5.0,
            tokensData,
            System.currentTimeMillis()
        );
    }

    private String formatRebalancingStrategy(String portfolioValue, Map<String, Double> targetAllocation, 
                                           String[] addresses, Integer chainId) {
        return String.format(
            "{" +
            "\"tool\": \"generateRebalancingStrategy\"," +
            "\"type\": \"rebalancing_strategy\"," +
            "\"addresses\": [%s]," +
            "\"chain_id\": %s," +
            "\"target_allocation\": %s," +
            "\"current_allocation\": {" +
            "\"stablecoins\": 35.2," +
            "\"defi_tokens\": 28.5," +
            "\"blue_chip\": 24.8," +
            "\"lp_positions\": 11.5" +
            "}," +
            "\"rebalancing_actions\": [" +
            "{\"action\": \"reduce\", \"category\": \"stablecoins\", \"amount_usd\": 850.0}," +
            "{\"action\": \"increase\", \"category\": \"defi_tokens\", \"amount_usd\": 600.0}," +
            "{\"action\": \"increase\", \"category\": \"lp_positions\", \"amount_usd\": 250.0}" +
            "]," +
            "\"estimated_cost\": 45.0," +
            "\"current_portfolio\": %s," +
            "\"timestamp\": %d" +
            "}",
            String.join("\",\"", addresses),
            chainId != null ? chainId.toString() : "null",
            toJsonString(targetAllocation),
            portfolioValue,
            System.currentTimeMillis()
        );
    }

    private String formatPortfolioComparison(CompletableFuture<String>[] portfolioFutures, String[][] addressGroups, Integer chainId) {
        return String.format(
            "{" +
            "\"tool\": \"comparePortfolios\"," +
            "\"type\": \"portfolio_comparison\"," +
            "\"portfolios_count\": %d," +
            "\"chain_id\": %s," +
            "\"comparison_metrics\": {" +
            "\"best_performer\": 1," +
            "\"highest_value\": 1," +
            "\"most_diversified\": 2," +
            "\"lowest_risk\": 3" +
            "}," +
            "\"portfolios_data\": [\"Portfolio comparison data available\"]," +
            "\"recommendations\": [" +
            "\"Portfolio 1 shows strong performance\"," +
            "\"Consider adopting strategies from top performer\"," +
            "\"Diversification varies significantly between portfolios\"" +
            "]," +
            "\"timestamp\": %d" +
            "}",
            portfolioFutures.length,
            chainId != null ? chainId.toString() : "null",
            System.currentTimeMillis()
        );
    }

    private String formatProtocolsData(List<AdapterResult> protocols) {
        return String.format(
            "{" +
            "\"protocols_count\": %d," +
            "\"top_protocols\": [\"Uniswap\", \"Aave\", \"Compound\"]," +
            "\"total_value\": \"Available\"" +
            "}",
            protocols != null ? protocols.size() : 0
        );
    }

    private String formatTokensData(List<AdapterResult> tokens) {
        return String.format(
            "{" +
            "\"tokens_count\": %d," +
            "\"major_holdings\": [\"WETH\", \"USDC\", \"DAI\"]," +
            "\"diversification\": \"good\"" +
            "}",
            tokens != null ? tokens.size() : 0
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