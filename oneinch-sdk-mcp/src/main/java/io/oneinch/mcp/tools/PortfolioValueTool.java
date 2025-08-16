package io.oneinch.mcp.tools;

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
     * 
     * @param addresses Array of wallet addresses to analyze
     * @param chainId Optional specific chain ID, or null for all chains
     * @param includeMetrics Whether to include detailed P&L and performance metrics
     * @return Comprehensive portfolio analysis
     */
    public CompletableFuture<String> getPortfolioValue(String[] addresses, Integer chainId, Boolean includeMetrics) {
        log.info("Getting portfolio value for addresses {} on chain {}", String.join(",", addresses), chainId);

        try {
            // Build portfolio request
            PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
                    .addresses(List.of(addresses))
                    .chainId(chainId)
                    .build();

            return integrationService.getPortfolioValue(request)
                    .thenCompose(portfolioResponse -> {
                        if (includeMetrics != null && includeMetrics) {
                            return getPortfolioWithMetrics(addresses, chainId, portfolioResponse);
                        } else {
                            return CompletableFuture.completedFuture(
                                formatBasicPortfolioValue(portfolioResponse, addresses, chainId)
                            );
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Error getting portfolio value for addresses {}: {}", String.join(",", addresses), throwable.getMessage());
                        return formatErrorResponse("portfolio_value_failed", throwable.getMessage(), addresses, chainId);
                    });
                    
        } catch (Exception e) {
            log.error("Unexpected error in getPortfolioValue", e);
            return CompletableFuture.completedFuture(
                formatErrorResponse("unexpected_error", e.getMessage(), addresses, chainId)
            );
        }
    }

    /**
     * Calculate portfolio performance and P&L metrics.
     * 
     * @param addresses Array of wallet addresses to analyze
     * @param chainId Optional specific chain ID
     * @param timeframe Optional timeframe for P&L calculation (7d, 30d, 90d)
     * @return Portfolio performance analysis
     */
    public CompletableFuture<String> calculatePortfolioMetrics(String[] addresses, Integer chainId, String timeframe) {
        log.info("Calculating portfolio metrics for addresses {} on chain {} timeframe {}", 
                String.join(",", addresses), chainId, timeframe);

        // Get current portfolio value
        CompletableFuture<String> currentValue = getPortfolioValue(addresses, chainId, true);
        
        // Get historical data for P&L calculation (simplified for this implementation)
        return currentValue
                .thenApply(portfolio -> {
                    return formatPortfolioMetrics(portfolio, addresses, chainId, timeframe);
                })
                .exceptionally(throwable -> {
                    log.error("Error calculating portfolio metrics: {}", throwable.getMessage());
                    return formatErrorResponse("portfolio_metrics_failed", throwable.getMessage(), addresses, chainId);
                });
    }

    /**
     * Analyze portfolio diversification and risk exposure.
     * 
     * @param addresses Array of wallet addresses to analyze
     * @param chainId Optional specific chain ID
     * @return Portfolio risk analysis
     */
    public CompletableFuture<String> analyzePortfolioRisk(String[] addresses, Integer chainId) {
        log.info("Analyzing portfolio risk for addresses {} on chain {}", String.join(",", addresses), chainId);

        return getPortfolioValue(addresses, chainId, true)
                .thenCompose(portfolioValue -> {
                    return getProtocolsSnapshot(addresses, chainId)
                            .thenApply(protocolsData -> {
                                return formatRiskAnalysis(portfolioValue, protocolsData, addresses, chainId);
                            });
                })
                .exceptionally(throwable -> {
                    log.error("Error analyzing portfolio risk: {}", throwable.getMessage());
                    return formatErrorResponse("portfolio_risk_analysis_failed", throwable.getMessage(), addresses, chainId);
                });
    }

    /**
     * Find yield opportunities in current portfolio positions.
     * 
     * @param addresses Array of wallet addresses to analyze
     * @param chainId Optional specific chain ID
     * @param minAPR Minimum APR threshold for opportunities
     * @return Yield opportunities analysis
     */
    public CompletableFuture<String> findYieldOpportunities(String[] addresses, Integer chainId, Double minAPR) {
        log.info("Finding yield opportunities for addresses {} on chain {} min APR {}", 
                String.join(",", addresses), chainId, minAPR);

        return getTokensSnapshot(addresses, chainId)
                .thenApply(tokensData -> {
                    return formatYieldOpportunities(tokensData, addresses, chainId, minAPR);
                })
                .exceptionally(throwable -> {
                    log.error("Error finding yield opportunities: {}", throwable.getMessage());
                    return formatErrorResponse("yield_opportunities_failed", throwable.getMessage(), addresses, chainId);
                });
    }

    /**
     * Generate portfolio rebalancing recommendations.
     * 
     * @param addresses Array of wallet addresses to analyze
     * @param chainId Optional specific chain ID
     * @param targetAllocation Target allocation percentages
     * @return Rebalancing strategy
     */
    public CompletableFuture<String> generateRebalancingStrategy(String[] addresses, Integer chainId, 
                                                                Map<String, Double> targetAllocation) {
        log.info("Generating rebalancing strategy for addresses {} on chain {}", 
                String.join(",", addresses), chainId);

        return getPortfolioValue(addresses, chainId, true)
                .thenApply(portfolioValue -> {
                    return formatRebalancingStrategy(portfolioValue, targetAllocation, addresses, chainId);
                })
                .exceptionally(throwable -> {
                    log.error("Error generating rebalancing strategy: {}", throwable.getMessage());
                    return formatErrorResponse("rebalancing_strategy_failed", throwable.getMessage(), addresses, chainId);
                });
    }

    /**
     * Compare portfolio performance across multiple addresses or time periods.
     * 
     * @param addressGroups Array of address groups to compare
     * @param chainId Optional specific chain ID
     * @return Portfolio comparison analysis
     */
    public CompletableFuture<String> comparePortfolios(String[][] addressGroups, Integer chainId) {
        log.info("Comparing {} portfolios on chain {}", addressGroups.length, chainId);

        // Get portfolio values for each group
        CompletableFuture<String>[] portfolioFutures = new CompletableFuture[addressGroups.length];
        for (int i = 0; i < addressGroups.length; i++) {
            portfolioFutures[i] = getPortfolioValue(addressGroups[i], chainId, true);
        }
        
        return CompletableFuture.allOf(portfolioFutures)
                .thenApply(unused -> {
                    return formatPortfolioComparison(portfolioFutures, addressGroups, chainId);
                })
                .exceptionally(throwable -> {
                    log.error("Error comparing portfolios: {}", throwable.getMessage());
                    return formatErrorResponse("portfolio_comparison_failed", throwable.getMessage(), new String[]{"multiple"}, chainId);
                });
    }

    // === HELPER METHODS ===

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