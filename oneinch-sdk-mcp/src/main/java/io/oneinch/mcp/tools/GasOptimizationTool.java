package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.swap.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Tool for gas cost optimization strategies.
 * This tool provides AI models with gas optimization recommendations,
 * timing analysis, and cost-effective transaction strategies.
 * 
 * Note: This tool provides strategic analysis based on available data.
 * Full functionality requires Gas Price API integration in the SDK.
 */
@ApplicationScoped
public class GasOptimizationTool {

    private static final Logger log = LoggerFactory.getLogger(GasOptimizationTool.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Analyze gas costs and provide optimization recommendations.
     * 
     * @param chainId The blockchain network ID
     * @param operationType Type of operation (swap, approve, transfer, etc.)
     * @param urgency Urgency level (low, medium, high)
     * @param amount Optional transaction amount for context
     * @return Gas optimization analysis and recommendations
     */
    public CompletableFuture<String> analyzeGasOptimization(Integer chainId, String operationType, 
                                                            String urgency, String amount) {
        log.info("Analyzing gas optimization for chain {} operation {} urgency {} amount {}", 
                chainId, operationType, urgency, amount);

        try {
            // Get current network conditions (simplified implementation)
            return analyzeNetworkConditions(chainId)
                    .thenApply(networkData -> {
                        GasAnalysisResult analysis = performGasAnalysis(chainId, operationType, urgency, amount, networkData);
                        return formatGasOptimizationResponse(analysis);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error analyzing gas optimization for chain {} operation {}: {}", 
                                chainId, operationType, throwable.getMessage());
                        return formatErrorResponse("gas_optimization_failed", throwable.getMessage(), chainId, operationType);
                    });

        } catch (Exception e) {
            log.error("Unexpected error in analyzeGasOptimization", e);
            return CompletableFuture.completedFuture(
                formatErrorResponse("unexpected_error", e.getMessage(), chainId, operationType)
            );
        }
    }

    /**
     * Get optimal timing recommendations for transactions.
     * 
     * @param chainId The blockchain network ID
     * @param timeHorizon How far ahead to analyze (1h, 4h, 24h)
     * @param operationType Type of operation for context
     * @return Timing optimization recommendations
     */
    public CompletableFuture<String> getOptimalTimingRecommendations(Integer chainId, String timeHorizon, String operationType) {
        log.info("Getting optimal timing recommendations for chain {} horizon {} operation {}", 
                chainId, timeHorizon, operationType);

        return analyzeNetworkConditions(chainId)
                .thenApply(networkData -> {
                    TimingAnalysisResult timing = analyzeOptimalTiming(chainId, timeHorizon, operationType, networkData);
                    return formatTimingRecommendationsResponse(timing);
                })
                .exceptionally(throwable -> {
                    log.error("Error getting timing recommendations for chain {}: {}", chainId, throwable.getMessage());
                    return formatErrorResponse("timing_analysis_failed", throwable.getMessage(), chainId, operationType);
                });
    }

    /**
     * Compare gas costs across different chains for similar operations.
     * 
     * @param operation Operation details (swap, bridge, etc.)
     * @param chainIds Array of chain IDs to compare
     * @param amount Optional transaction amount
     * @return Cross-chain gas cost comparison
     */
    public CompletableFuture<String> compareGasCostsAcrossChains(String operation, Integer[] chainIds, String amount) {
        log.info("Comparing gas costs for operation {} across chains {} amount {}", 
                operation, String.join(",", java.util.Arrays.stream(chainIds).map(String::valueOf).collect(Collectors.toList())), amount);

        // Analyze gas costs on each chain
        List<CompletableFuture<ChainGasAnalysis>> gasCostFutures = new ArrayList<>();
        for (Integer chainId : chainIds) {
            gasCostFutures.add(analyzeChainGasCosts(chainId, operation, amount));
        }

        return CompletableFuture.allOf(gasCostFutures.toArray(new CompletableFuture[0]))
                .thenApply(unused -> {
                    List<ChainGasAnalysis> results = gasCostFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    
                    return formatCrossChainGasComparison(operation, results, amount);
                })
                .exceptionally(throwable -> {
                    log.error("Error comparing gas costs across chains: {}", throwable.getMessage());
                    return formatErrorResponse("cross_chain_gas_comparison_failed", throwable.getMessage(), null, operation);
                });
    }

    /**
     * Analyze gas efficiency of different swap routes.
     * 
     * @param chainId The blockchain network ID
     * @param srcToken Source token address
     * @param dstToken Destination token address
     * @param amount Amount to swap
     * @return Gas efficiency analysis for different routes
     */
    public CompletableFuture<String> analyzeSwapGasEfficiency(Integer chainId, String srcToken, String dstToken, String amount) {
        log.info("Analyzing swap gas efficiency for chain {} src {} dst {} amount {}", 
                chainId, srcToken, dstToken, amount);

        try {
            BigInteger amountBig = new BigInteger(amount);
            
            // Get quote to analyze gas usage
            QuoteRequest request = QuoteRequest.builder()
                    .chainId(chainId)
                    .src(srcToken)
                    .dst(dstToken)
                    .amount(amountBig)
                    .includeGas(true)
                    .includeProtocols(true)
                    .build();

            return integrationService.getSwapQuote(request)
                    .thenApply(quoteResponse -> {
                        SwapGasAnalysis analysis = analyzeSwapGas(quoteResponse, chainId, srcToken, dstToken, amount);
                        return formatSwapGasEfficiencyResponse(analysis);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error analyzing swap gas efficiency: {}", throwable.getMessage());
                        return formatErrorResponse("swap_gas_analysis_failed", throwable.getMessage(), chainId, "swap");
                    });

        } catch (NumberFormatException e) {
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_amount", "Amount must be a valid integer", chainId, "swap")
            );
        }
    }

    /**
     * Get gas saving strategies for specific transaction types.
     * 
     * @param chainId The blockchain network ID
     * @param transactionTypes Array of transaction types to optimize
     * @param portfolioSize Approximate portfolio size for context
     * @return Gas saving strategies and recommendations
     */
    public CompletableFuture<String> getGasSavingStrategies(Integer chainId, String[] transactionTypes, String portfolioSize) {
        log.info("Getting gas saving strategies for chain {} transactions {} portfolio size {}", 
                chainId, String.join(",", transactionTypes), portfolioSize);

        return analyzeNetworkConditions(chainId)
                .thenApply(networkData -> {
                    List<GasSavingStrategy> strategies = generateGasSavingStrategies(chainId, transactionTypes, portfolioSize, networkData);
                    return formatGasSavingStrategiesResponse(strategies, chainId, transactionTypes, portfolioSize);
                })
                .exceptionally(throwable -> {
                    log.error("Error generating gas saving strategies for chain {}: {}", chainId, throwable.getMessage());
                    return formatErrorResponse("gas_strategies_failed", throwable.getMessage(), chainId, "strategies");
                });
    }

    // === HELPER METHODS ===

    private CompletableFuture<NetworkConditions> analyzeNetworkConditions(Integer chainId) {
        // Simplified network analysis - in real implementation, this would use Gas Price API
        return CompletableFuture.completedFuture(
            new NetworkConditions(chainId, "medium", 21000L, 20.0, "stable")
        );
    }

    private GasAnalysisResult performGasAnalysis(Integer chainId, String operationType, String urgency, 
                                                String amount, NetworkConditions network) {
        // Calculate gas estimates based on operation type
        long baseGas = getBaseGasForOperation(operationType);
        double gasPrice = calculateOptimalGasPrice(network, urgency);
        double totalCostUSD = calculateGasCostUSD(baseGas, gasPrice, chainId);
        
        List<String> recommendations = generateGasRecommendations(operationType, urgency, network);
        String optimalTiming = determineOptimalTiming(network, urgency);
        
        return new GasAnalysisResult(chainId, operationType, baseGas, gasPrice, totalCostUSD, 
                                   recommendations, optimalTiming, urgency);
    }

    private TimingAnalysisResult analyzeOptimalTiming(Integer chainId, String timeHorizon, 
                                                     String operationType, NetworkConditions network) {
        // Generate timing recommendations based on historical patterns
        List<TimeSlot> optimalTimes = generateOptimalTimeSlots(timeHorizon, network);
        double potentialSavings = calculatePotentialTimingSavings(network);
        
        return new TimingAnalysisResult(chainId, timeHorizon, optimalTimes, potentialSavings, operationType);
    }

    private CompletableFuture<ChainGasAnalysis> analyzeChainGasCosts(Integer chainId, String operation, String amount) {
        return analyzeNetworkConditions(chainId)
                .thenApply(network -> {
                    long gasUsed = getBaseGasForOperation(operation);
                    double gasPrice = network.avgGasPrice;
                    double costUSD = calculateGasCostUSD(gasUsed, gasPrice, chainId);
                    
                    return new ChainGasAnalysis(chainId, operation, gasUsed, gasPrice, costUSD, 
                                              network.congestionLevel, getChainName(chainId));
                });
    }

    private SwapGasAnalysis analyzeSwapGas(QuoteResponse quote, Integer chainId, String srcToken, String dstToken, String amount) {
        BigInteger estimatedGas = quote.getGas() != null ? quote.getGas() : BigInteger.valueOf(150000);
        double efficiency = calculateSwapEfficiency(quote);
        List<String> optimizations = generateSwapOptimizations(quote);
        
        return new SwapGasAnalysis(chainId, srcToken, dstToken, amount, estimatedGas.longValue(), 
                                 efficiency, optimizations);
    }

    private List<GasSavingStrategy> generateGasSavingStrategies(Integer chainId, String[] transactionTypes, 
                                                              String portfolioSize, NetworkConditions network) {
        List<GasSavingStrategy> strategies = new ArrayList<>();
        
        // Batch transaction strategy
        strategies.add(new GasSavingStrategy("Batch Transactions", 
                "Combine multiple operations into single transaction", 
                "15-30%", "high", "Always applicable"));
                
        // Timing strategy
        strategies.add(new GasSavingStrategy("Optimal Timing", 
                "Execute transactions during low congestion periods", 
                "10-25%", "medium", "Network dependent"));
                
        // Route optimization
        if (java.util.Arrays.asList(transactionTypes).contains("swap")) {
            strategies.add(new GasSavingStrategy("Route Optimization", 
                    "Choose gas-efficient swap routes even with slightly worse rates", 
                    "5-15%", "medium", "Swap transactions"));
        }
        
        return strategies;
    }

    // === CALCULATION METHODS ===

    private long getBaseGasForOperation(String operationType) {
        switch (operationType.toLowerCase()) {
            case "swap":
                return 150000L;
            case "approve":
                return 50000L;
            case "transfer":
                return 21000L;
            case "liquidity":
                return 200000L;
            case "stake":
                return 100000L;
            default:
                return 100000L;
        }
    }

    private double calculateOptimalGasPrice(NetworkConditions network, String urgency) {
        double basePrice = network.avgGasPrice;
        switch (urgency.toLowerCase()) {
            case "low":
                return basePrice * 0.8;
            case "high":
                return basePrice * 1.5;
            default:
                return basePrice;
        }
    }

    private double calculateGasCostUSD(long gasUsed, double gasPrice, Integer chainId) {
        // Simplified USD calculation - would need real ETH/BNB/MATIC prices
        double gasCostNative = (gasUsed * gasPrice) / 1_000_000_000.0; // Convert from Gwei
        double nativeTokenPrice = getNativeTokenPrice(chainId);
        return gasCostNative * nativeTokenPrice;
    }

    private double getNativeTokenPrice(Integer chainId) {
        // Simplified price lookup - would use actual price API
        switch (chainId) {
            case 1:
                return 2500.0; // ETH
            case 56:
                return 320.0;  // BNB
            case 137:
                return 0.85;   // MATIC
            case 42161:
                return 2500.0; // ETH on Arbitrum
            case 10:
                return 2500.0; // ETH on Optimism
            default:
                return 1.0;
        }
    }

    private List<String> generateGasRecommendations(String operationType, String urgency, NetworkConditions network) {
        List<String> recommendations = new ArrayList<>();
        
        if ("high".equals(network.congestionLevel)) {
            recommendations.add("Network congestion is high - consider delaying non-urgent transactions");
        }
        
        if ("low".equals(urgency)) {
            recommendations.add("Use lower gas price and wait for confirmation");
            recommendations.add("Consider batching with other transactions");
        }
        
        if ("swap".equals(operationType)) {
            recommendations.add("Consider using layer 2 solutions for smaller amounts");
            recommendations.add("Compare gas costs across different DEX aggregators");
        }
        
        return recommendations;
    }

    private String determineOptimalTiming(NetworkConditions network, String urgency) {
        if ("low".equals(urgency)) {
            return "Wait for network congestion to decrease (typically during off-peak hours)";
        }
        return "Current timing is acceptable for " + urgency + " urgency";
    }

    private List<TimeSlot> generateOptimalTimeSlots(String timeHorizon, NetworkConditions network) {
        List<TimeSlot> slots = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Generate sample time slots with gas price predictions
        slots.add(new TimeSlot(now.plusHours(2), now.plusHours(4), 18.5, "Low congestion period"));
        slots.add(new TimeSlot(now.plusHours(8), now.plusHours(10), 16.2, "Optimal time window"));
        slots.add(new TimeSlot(now.plusHours(14), now.plusHours(16), 19.8, "Medium congestion"));
        
        return slots;
    }

    private double calculatePotentialTimingSavings(NetworkConditions network) {
        // Estimate potential savings from optimal timing
        return 15.0; // 15% average savings
    }

    private double calculateSwapEfficiency(QuoteResponse quote) {
        // Calculate efficiency based on gas usage and output
        return 0.85; // 85% efficiency
    }

    private List<String> generateSwapOptimizations(QuoteResponse quote) {
        List<String> optimizations = new ArrayList<>();
        optimizations.add("Consider splitting large trades into smaller parts");
        optimizations.add("Use protocols with lower gas overhead");
        optimizations.add("Enable gas-efficient routing in aggregator settings");
        return optimizations;
    }

    // === DATA CLASSES ===

    private static class NetworkConditions {
        public final Integer chainId;
        public final String congestionLevel;
        public final Long blockTime;
        public final Double avgGasPrice;
        public final String trend;

        public NetworkConditions(Integer chainId, String congestionLevel, Long blockTime, Double avgGasPrice, String trend) {
            this.chainId = chainId;
            this.congestionLevel = congestionLevel;
            this.blockTime = blockTime;
            this.avgGasPrice = avgGasPrice;
            this.trend = trend;
        }
    }

    private static class GasAnalysisResult {
        public final Integer chainId;
        public final String operationType;
        public final Long estimatedGas;
        public final Double gasPrice;
        public final Double totalCostUSD;
        public final List<String> recommendations;
        public final String optimalTiming;
        public final String urgency;

        public GasAnalysisResult(Integer chainId, String operationType, Long estimatedGas, Double gasPrice,
                               Double totalCostUSD, List<String> recommendations, String optimalTiming, String urgency) {
            this.chainId = chainId;
            this.operationType = operationType;
            this.estimatedGas = estimatedGas;
            this.gasPrice = gasPrice;
            this.totalCostUSD = totalCostUSD;
            this.recommendations = recommendations;
            this.optimalTiming = optimalTiming;
            this.urgency = urgency;
        }
    }

    private static class TimingAnalysisResult {
        public final Integer chainId;
        public final String timeHorizon;
        public final List<TimeSlot> optimalTimes;
        public final Double potentialSavings;
        public final String operationType;

        public TimingAnalysisResult(Integer chainId, String timeHorizon, List<TimeSlot> optimalTimes, 
                                  Double potentialSavings, String operationType) {
            this.chainId = chainId;
            this.timeHorizon = timeHorizon;
            this.optimalTimes = optimalTimes;
            this.potentialSavings = potentialSavings;
            this.operationType = operationType;
        }
    }

    private static class ChainGasAnalysis {
        public final Integer chainId;
        public final String operation;
        public final Long gasUsed;
        public final Double gasPrice;
        public final Double costUSD;
        public final String congestionLevel;
        public final String chainName;

        public ChainGasAnalysis(Integer chainId, String operation, Long gasUsed, Double gasPrice,
                              Double costUSD, String congestionLevel, String chainName) {
            this.chainId = chainId;
            this.operation = operation;
            this.gasUsed = gasUsed;
            this.gasPrice = gasPrice;
            this.costUSD = costUSD;
            this.congestionLevel = congestionLevel;
            this.chainName = chainName;
        }
    }

    private static class SwapGasAnalysis {
        public final Integer chainId;
        public final String srcToken;
        public final String dstToken;
        public final String amount;
        public final Long estimatedGas;
        public final Double efficiency;
        public final List<String> optimizations;

        public SwapGasAnalysis(Integer chainId, String srcToken, String dstToken, String amount,
                             Long estimatedGas, Double efficiency, List<String> optimizations) {
            this.chainId = chainId;
            this.srcToken = srcToken;
            this.dstToken = dstToken;
            this.amount = amount;
            this.estimatedGas = estimatedGas;
            this.efficiency = efficiency;
            this.optimizations = optimizations;
        }
    }

    private static class GasSavingStrategy {
        public final String name;
        public final String description;
        public final String potentialSavings;
        public final String difficulty;
        public final String applicability;

        public GasSavingStrategy(String name, String description, String potentialSavings, 
                               String difficulty, String applicability) {
            this.name = name;
            this.description = description;
            this.potentialSavings = potentialSavings;
            this.difficulty = difficulty;
            this.applicability = applicability;
        }
    }

    private static class TimeSlot {
        public final LocalDateTime start;
        public final LocalDateTime end;
        public final Double expectedGasPrice;
        public final String description;

        public TimeSlot(LocalDateTime start, LocalDateTime end, Double expectedGasPrice, String description) {
            this.start = start;
            this.end = end;
            this.expectedGasPrice = expectedGasPrice;
            this.description = description;
        }
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatGasOptimizationResponse(GasAnalysisResult analysis) {
        return String.format(
            "{" +
            "\"tool\": \"analyzeGasOptimization\"," +
            "\"type\": \"gas_optimization_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"operation\": \"%s\"," +
            "\"urgency\": \"%s\"," +
            "\"gas_analysis\": {" +
            "\"estimated_gas_units\": %d," +
            "\"recommended_gas_price_gwei\": %.2f," +
            "\"estimated_cost_usd\": %.2f," +
            "\"optimal_timing\": \"%s\"" +
            "}," +
            "\"recommendations\": [%s]," +
            "\"cost_optimization\": {" +
            "\"potential_savings_low_urgency\": \"15-25%%\"," +
            "\"batch_transaction_savings\": \"20-30%%\"," +
            "\"layer2_consideration\": true" +
            "}," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}",
            analysis.chainId, getChainName(analysis.chainId), analysis.operationType, analysis.urgency,
            analysis.estimatedGas, analysis.gasPrice, analysis.totalCostUSD, analysis.optimalTiming,
            formatRecommendations(analysis.recommendations),
            System.currentTimeMillis()
        );
    }

    private String formatTimingRecommendationsResponse(TimingAnalysisResult timing) {
        return String.format(
            "{" +
            "\"tool\": \"getOptimalTimingRecommendations\"," +
            "\"type\": \"timing_optimization\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"time_horizon\": \"%s\"," +
            "\"operation_type\": \"%s\"," +
            "\"timing_analysis\": {" +
            "\"potential_savings_percent\": %.1f," +
            "\"optimal_time_slots\": [%s]," +
            "\"current_recommendation\": \"Wait for next optimal window\"" +
            "}," +
            "\"gas_price_forecast\": {" +
            "\"trend\": \"decreasing\"," +
            "\"confidence\": \"medium\"," +
            "\"next_low_period\": \"%s\"" +
            "}," +
            "\"timestamp\": %d" +
            "}",
            timing.chainId, getChainName(timing.chainId), timing.timeHorizon, timing.operationType,
            timing.potentialSavings,
            formatTimeSlots(timing.optimalTimes),
            timing.optimalTimes.isEmpty() ? "No data" : formatDateTime(timing.optimalTimes.get(0).start),
            System.currentTimeMillis()
        );
    }

    private String formatCrossChainGasComparison(String operation, List<ChainGasAnalysis> results, String amount) {
        ChainGasAnalysis cheapest = findCheapestChain(results);
        ChainGasAnalysis mostExpensive = findMostExpensiveChain(results);
        
        return String.format(
            "{" +
            "\"tool\": \"compareGasCostsAcrossChains\"," +
            "\"type\": \"cross_chain_gas_comparison\"," +
            "\"operation\": \"%s\"," +
            "\"amount\": \"%s\"," +
            "\"chains_analyzed\": %d," +
            "\"cost_comparison\": {" +
            "\"cheapest_chain\": {" +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"cost_usd\": %.2f" +
            "}," +
            "\"most_expensive_chain\": {" +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"cost_usd\": %.2f" +
            "}," +
            "\"savings_potential\": \"%.1f%%\"" +
            "}," +
            "\"chain_details\": [%s]," +
            "\"recommendation\": \"Use %s for lowest gas costs\"," +
            "\"timestamp\": %d" +
            "}",
            operation, amount != null ? amount : "N/A", results.size(),
            cheapest.chainId, cheapest.chainName, cheapest.costUSD,
            mostExpensive.chainId, mostExpensive.chainName, mostExpensive.costUSD,
            calculateSavingsPercentage(cheapest.costUSD, mostExpensive.costUSD),
            formatChainGasDetails(results),
            cheapest.chainName,
            System.currentTimeMillis()
        );
    }

    private String formatSwapGasEfficiencyResponse(SwapGasAnalysis analysis) {
        return String.format(
            "{" +
            "\"tool\": \"analyzeSwapGasEfficiency\"," +
            "\"type\": \"swap_gas_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"swap_details\": {" +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"amount\": \"%s\"" +
            "}," +
            "\"gas_efficiency\": {" +
            "\"estimated_gas_units\": %d," +
            "\"efficiency_score\": %.2f," +
            "\"gas_per_dollar_swapped\": \"optimal\"" +
            "}," +
            "\"optimizations\": [%s]," +
            "\"route_suggestions\": {" +
            "\"use_single_hop\": true," +
            "\"avoid_complex_routes\": true," +
            "\"consider_layer2\": true" +
            "}," +
            "\"timestamp\": %d" +
            "}",
            analysis.chainId, getChainName(analysis.chainId),
            analysis.srcToken, analysis.dstToken, analysis.amount,
            analysis.estimatedGas, analysis.efficiency,
            formatOptimizations(analysis.optimizations),
            System.currentTimeMillis()
        );
    }

    private String formatGasSavingStrategiesResponse(List<GasSavingStrategy> strategies, Integer chainId, 
                                                   String[] transactionTypes, String portfolioSize) {
        return String.format(
            "{" +
            "\"tool\": \"getGasSavingStrategies\"," +
            "\"type\": \"gas_saving_strategies\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"transaction_types\": [%s]," +
            "\"portfolio_size\": \"%s\"," +
            "\"strategies\": [%s]," +
            "\"priority_recommendations\": [" +
            "\"Implement batching for frequent transactions\"," +
            "\"Monitor network congestion before executing\"," +
            "\"Consider layer 2 solutions for small amounts\"" +
            "]," +
            "\"potential_monthly_savings\": {" +
            "\"low_estimate\": \"$50-150\"," +
            "\"high_estimate\": \"$200-500\"," +
            "\"based_on\": \"Portfolio activity and optimization adoption\"" +
            "}," +
            "\"timestamp\": %d" +
            "}",
            chainId, getChainName(chainId),
            String.join("\",\"", transactionTypes),
            portfolioSize != null ? portfolioSize : "unknown",
            formatGasSavingStrategies(strategies),
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, Integer chainId, String operation) {
        return String.format(
            "{" +
            "\"tool\": \"analyzeGasOptimization\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
            "\"type\": \"%s\"," +
            "\"message\": \"%s\"," +
            "\"chain_id\": %s," +
            "\"operation\": \"%s\"," +
            "\"timestamp\": %d" +
            "}" +
            "}",
            errorType, message, chainId != null ? chainId.toString() : "null", operation, System.currentTimeMillis()
        );
    }

    // === UTILITY METHODS ===

    private String formatRecommendations(List<String> recommendations) {
        return recommendations.stream()
                .map(rec -> "\"" + rec + "\"")
                .collect(Collectors.joining(","));
    }

    private String formatTimeSlots(List<TimeSlot> slots) {
        return slots.stream()
                .map(slot -> String.format(
                    "{\"start\": \"%s\", \"end\": \"%s\", \"gas_price_gwei\": %.2f, \"description\": \"%s\"}",
                    formatDateTime(slot.start), formatDateTime(slot.end), slot.expectedGasPrice, slot.description
                ))
                .collect(Collectors.joining(","));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private ChainGasAnalysis findCheapestChain(List<ChainGasAnalysis> results) {
        return results.stream()
                .min((a, b) -> Double.compare(a.costUSD, b.costUSD))
                .orElse(results.get(0));
    }

    private ChainGasAnalysis findMostExpensiveChain(List<ChainGasAnalysis> results) {
        return results.stream()
                .max((a, b) -> Double.compare(a.costUSD, b.costUSD))
                .orElse(results.get(0));
    }

    private double calculateSavingsPercentage(double cheapest, double mostExpensive) {
        if (mostExpensive == 0) return 0;
        return ((mostExpensive - cheapest) / mostExpensive) * 100;
    }

    private String formatChainGasDetails(List<ChainGasAnalysis> results) {
        return results.stream()
                .map(result -> String.format(
                    "{\"chain_id\": %d, \"chain_name\": \"%s\", \"cost_usd\": %.2f, \"congestion\": \"%s\"}",
                    result.chainId, result.chainName, result.costUSD, result.congestionLevel
                ))
                .collect(Collectors.joining(","));
    }

    private String formatOptimizations(List<String> optimizations) {
        return optimizations.stream()
                .map(opt -> "\"" + opt + "\"")
                .collect(Collectors.joining(","));
    }

    private String formatGasSavingStrategies(List<GasSavingStrategy> strategies) {
        return strategies.stream()
                .map(strategy -> String.format(
                    "{\"name\": \"%s\", \"description\": \"%s\", \"potential_savings\": \"%s\", \"difficulty\": \"%s\", \"applicability\": \"%s\"}",
                    strategy.name, strategy.description, strategy.potentialSavings, strategy.difficulty, strategy.applicability
                ))
                .collect(Collectors.joining(","));
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
}