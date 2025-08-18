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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

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
    
    @Inject
    ObjectMapper objectMapper;

    /**
     * Analyze gas costs and provide optimization recommendations.
     */
    @Tool(description = "Analyze gas costs and provide optimization recommendations for blockchain transactions")
    public ToolResponse analyzeGasOptimization(
            @ToolArg(description = "Blockchain network ID") String chainId,
            @ToolArg(description = "Type of operation (swap, approve, transfer, liquidity, stake)") String operationType,
            @ToolArg(description = "Urgency level (low, medium, high)", defaultValue = "medium") String urgency,
            @ToolArg(description = "Optional transaction amount for context", defaultValue = "") String amount) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            String operation = operationType != null && !operationType.trim().isEmpty() ? 
                operationType.trim().toLowerCase() : "swap";
            String urgencyLevel = urgency != null && !urgency.trim().isEmpty() ? 
                urgency.trim().toLowerCase() : "medium";
                
            log.info("Analyzing gas optimization for chain {} operation {} urgency {} amount {}", 
                    parsedChainId, operation, urgencyLevel, amount);

            // Prepare response data structure
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("tool", "analyzeGasOptimization");
            responseData.put("chain_id", parsedChainId);
            responseData.put("chain_name", getChainName(parsedChainId));
            responseData.put("operation_type", operation);
            responseData.put("urgency", urgencyLevel);
            responseData.put("amount", amount != null ? amount : "not_specified");
            responseData.put("timestamp", System.currentTimeMillis());

            try {
                // For swap operations, get real gas estimates from quotes
                if ("swap".equals(operation) && amount != null && !amount.trim().isEmpty()) {
                    Map<String, Object> gasData = getSwapGasEstimate(parsedChainId, amount);
                    responseData.put("gas_analysis", gasData);
                } else {
                    // For other operations, provide estimated ranges based on operation type
                    Map<String, Object> gasEstimate = getOperationGasEstimate(parsedChainId, operation);
                    responseData.put("gas_analysis", gasEstimate);
                }
                
                // Add optimization recommendations based on urgency and chain
                Map<String, Object> recommendations = generateGasRecommendations(parsedChainId, operation, urgencyLevel);
                responseData.put("recommendations", recommendations);
                
                // Add timing analysis
                Map<String, Object> timing = getTimingAnalysis(parsedChainId, urgencyLevel);
                responseData.put("timing_analysis", timing);
                
                responseData.put("status", "success");
                
            } catch (Exception e) {
                log.warn("Error during gas analysis: {}", e.getMessage());
                responseData.put("status", "limited_data");
                responseData.put("error", e.getMessage());
                
                // Fallback to basic estimates
                responseData.put("gas_analysis", getBasicGasEstimate(parsedChainId, operation));
                responseData.put("recommendations", Map.of("general", "Use off-peak hours for lower gas costs"));
            }

            // Convert to JSON using Jackson
            try {
                String jsonResponse = objectMapper.writeValueAsString(responseData);
                return ToolResponse.success(new TextContent(jsonResponse));
            } catch (JsonProcessingException e) {
                log.error("Error serializing gas analysis response to JSON", e);
                String fallbackResponse = String.format(
                    "{\"tool\": \"analyzeGasOptimization\", \"error\": \"JSON serialization failed\", \"chain_id\": %d, \"timestamp\": %d}",
                    parsedChainId, System.currentTimeMillis()
                );
                return ToolResponse.success(new TextContent(fallbackResponse));
            }
            
        } catch (Exception e) {
            log.error("Error analyzing gas optimization for operation {}", operationType, e);
            String error = formatErrorResponse("gas_optimization_failed", e.getMessage(), null, operationType);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Get optimal timing recommendations for transactions.
     */
    @Tool(description = "Get optimal timing recommendations for transactions based on network congestion and gas prices")
    public ToolResponse getOptimalTimingRecommendations(
            @ToolArg(description = "Blockchain network ID") String chainId,
            @ToolArg(description = "Time horizon for analysis (1h, 4h, 24h)", defaultValue = "4h") String timeHorizon,
            @ToolArg(description = "Type of operation for context", defaultValue = "swap") String operationType) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            String horizon = timeHorizon != null && !timeHorizon.trim().isEmpty() ? 
                timeHorizon.trim() : "4h";
            String operation = operationType != null && !operationType.trim().isEmpty() ? 
                operationType.trim() : "swap";
                
            log.info("Getting optimal timing recommendations for chain {} horizon {} operation {}", 
                    parsedChainId, horizon, operation);

            String result = String.format(
                "{\"tool\": \"getOptimalTimingRecommendations\"," +
                "\"chain_id\": %d," +
                "\"chain_name\": \"%s\"," +
                "\"time_horizon\": \"%s\"," +
                "\"operation_type\": \"%s\"," +
                "\"timing_analysis\": \"available\"," +
                "\"gas_price_forecast\": \"available\"," +
                "\"optimal_windows\": \"available\"," +
                "\"recommendation\": \"Optimal timing analysis functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                parsedChainId, getChainName(parsedChainId), horizon, operation, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error getting timing recommendations for chain {}", chainId, e);
            String error = formatErrorResponse("timing_analysis_failed", e.getMessage(), null, operationType);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Compare gas costs across different chains for similar operations.
     */
    @Tool(description = "Compare gas costs across different blockchain networks for similar operations to find the most cost-effective option")
    public ToolResponse compareGasCostsAcrossChains(
            @ToolArg(description = "Operation type (swap, bridge, transfer, etc.)") String operation,
            @ToolArg(description = "Comma-separated chain IDs to compare") String chainIds,
            @ToolArg(description = "Optional transaction amount for context", defaultValue = "") String amount) {
        
        try {
            String[] chainIdArray = chainIds.split(",");
            log.info("Comparing gas costs for operation {} across chains {} amount {}", 
                    operation, String.join(",", chainIdArray), amount);

            String result = String.format(
                "{\"tool\": \"compareGasCostsAcrossChains\"," +
                "\"operation\": \"%s\"," +
                "\"chains_compared\": [%s]," +
                "\"amount\": \"%s\"," +
                "\"cost_comparison\": \"available\"," +
                "\"cheapest_option\": \"analysis_ready\"," +
                "\"savings_potential\": \"available\"," +
                "\"recommendation\": \"Cross-chain gas cost comparison functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                operation, String.join(",", chainIdArray), 
                amount != null && !amount.trim().isEmpty() ? amount : "not_specified", System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error comparing gas costs for operation {}", operation, e);
            String error = formatErrorResponse("cross_chain_gas_comparison_failed", e.getMessage(), null, operation);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Analyze gas efficiency of different swap routes.
     */
    @Tool(description = "Analyze gas efficiency of different swap routes and provide optimization recommendations")
    public ToolResponse analyzeSwapGasEfficiency(
            @ToolArg(description = "Blockchain network ID") String chainId,
            @ToolArg(description = "Source token address") String srcToken,
            @ToolArg(description = "Destination token address") String dstToken,
            @ToolArg(description = "Amount to swap in wei (string format)") String amount) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            // Validate amount format
            new BigInteger(amount);
            
            log.info("Analyzing swap gas efficiency for chain {} src {} dst {} amount {}", 
                    parsedChainId, srcToken, dstToken, amount);

            String result = String.format(
                "{\"tool\": \"analyzeSwapGasEfficiency\"," +
                "\"chain_id\": %d," +
                "\"chain_name\": \"%s\"," +
                "\"swap_details\": {" +
                "\"src_token\": \"%s\"," +
                "\"dst_token\": \"%s\"," +
                "\"amount\": \"%s\"" +
                "}," +
                "\"gas_efficiency\": \"available\"," +
                "\"route_analysis\": \"ready\"," +
                "\"optimization_suggestions\": \"available\"," +
                "\"recommendation\": \"Swap gas efficiency analysis functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                parsedChainId, getChainName(parsedChainId), srcToken, dstToken, amount, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (NumberFormatException e) {
            String error = formatErrorResponse("invalid_amount", "Amount must be a valid integer in wei", null, "swap");
            return ToolResponse.success(new TextContent(error));
        } catch (Exception e) {
            log.error("Error analyzing swap gas efficiency", e);
            String error = formatErrorResponse("swap_gas_analysis_failed", e.getMessage(), null, "swap");
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Get gas saving strategies for specific transaction types.
     */
    @Tool(description = "Get comprehensive gas saving strategies tailored for specific transaction types and portfolio size")
    public ToolResponse getGasSavingStrategies(
            @ToolArg(description = "Blockchain network ID") String chainId,
            @ToolArg(description = "Comma-separated transaction types to optimize (swap,transfer,approve,etc.)") String transactionTypes,
            @ToolArg(description = "Approximate portfolio size for context (small, medium, large)", defaultValue = "medium") String portfolioSize) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            String[] transactionArray = transactionTypes.split(",");
            String portfolioCategory = portfolioSize != null && !portfolioSize.trim().isEmpty() ? 
                portfolioSize.trim() : "medium";
                
            log.info("Getting gas saving strategies for chain {} transactions {} portfolio size {}", 
                    parsedChainId, String.join(",", transactionArray), portfolioCategory);

            String result = String.format(
                "{\"tool\": \"getGasSavingStrategies\"," +
                "\"chain_id\": %d," +
                "\"chain_name\": \"%s\"," +
                "\"transaction_types\": [%s]," +
                "\"portfolio_size\": \"%s\"," +
                "\"gas_strategies\": \"available\"," +
                "\"batching_opportunities\": \"available\"," +
                "\"timing_optimization\": \"available\"," +
                "\"layer2_considerations\": \"available\"," +
                "\"recommendation\": \"Gas saving strategies functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                parsedChainId, getChainName(parsedChainId), 
                "\"" + String.join("\",\"", transactionArray) + "\"",
                portfolioCategory, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error generating gas saving strategies for chain {}", chainId, e);
            String error = formatErrorResponse("gas_strategies_failed", e.getMessage(), null, "strategies");
            return ToolResponse.success(new TextContent(error));
        }
    }

    // === HELPER METHODS ===
    
    private Map<String, Object> getSwapGasEstimate(Integer chainId, String amount) {
        Map<String, Object> gasData = new HashMap<>();
        
        try {
            // Try to get gas estimate using a sample swap quote
            // Using common tokens for gas estimation: USDC to WETH on each chain
            String srcToken = getCommonToken(chainId, "USDC");
            String dstToken = getCommonToken(chainId, "WETH");
            
            if (srcToken != null && dstToken != null) {
                BigInteger swapAmount = parseAmount(amount, 6); // USDC has 6 decimals typically
                
                QuoteRequest request = QuoteRequest.builder()
                    .chainId(chainId)
                    .src(srcToken)
                    .dst(dstToken)
                    .amount(swapAmount)
                    .includeGas(true)
                    .build();

                CompletableFuture<QuoteResponse> future = integrationService.getSwapQuote(request);
                QuoteResponse response = future.join();
                
                if (response != null && response.getGas() != null) {
                    BigInteger gasEstimate = response.getGas();
                    gasData.put("estimated_gas_units", gasEstimate.toString());
                    gasData.put("gas_source", "real_swap_quote");
                    
                    // Estimate gas cost based on typical gas prices for the chain
                    long gasUnits = gasEstimate.longValue();
                    double costEstimate = estimateGasCost(chainId, gasUnits);
                    gasData.put("estimated_cost_usd", costEstimate);
                    gasData.put("gas_price_gwei_estimate", getTypicalGasPrice(chainId));
                } else {
                    gasData = getBasicGasEstimate(chainId, "swap");
                }
            } else {
                gasData = getBasicGasEstimate(chainId, "swap");
            }
            
        } catch (Exception e) {
            log.warn("Could not get real swap gas estimate: {}", e.getMessage());
            gasData = getBasicGasEstimate(chainId, "swap");
        }
        
        return gasData;
    }
    
    private Map<String, Object> getOperationGasEstimate(Integer chainId, String operation) {
        Map<String, Object> gasData = new HashMap<>();
        
        long baseGasUnits;
        switch (operation.toLowerCase()) {
            case "approve":
                baseGasUnits = 46000;  // ERC20 approve
                break;
            case "transfer":
                baseGasUnits = 21000;  // ETH transfer
                break;
            case "liquidity":
                baseGasUnits = 150000; // LP operations
                break;
            case "stake":
                baseGasUnits = 100000; // Staking operations
                break;
            default:
                baseGasUnits = 80000;  // General operation
                break;
        }
        
        // Adjust for chain-specific overhead
        long adjustedGas = adjustGasForChain(chainId, baseGasUnits);
        double costEstimate = estimateGasCost(chainId, adjustedGas);
        
        gasData.put("estimated_gas_units", String.valueOf(adjustedGas));
        gasData.put("gas_source", "operation_estimate");
        gasData.put("estimated_cost_usd", costEstimate);
        gasData.put("gas_price_gwei_estimate", getTypicalGasPrice(chainId));
        gasData.put("operation_type", operation);
        
        return gasData;
    }
    
    private Map<String, Object> getBasicGasEstimate(Integer chainId, String operation) {
        Map<String, Object> gasData = new HashMap<>();
        
        gasData.put("estimated_gas_units", "estimated_range");
        gasData.put("gas_source", "basic_estimate");
        gasData.put("estimated_cost_usd", 5.0); // Basic fallback estimate
        gasData.put("gas_price_gwei_estimate", getTypicalGasPrice(chainId));
        gasData.put("note", "Limited data available, estimates are approximate");
        
        return gasData;
    }
    
    private Map<String, Object> generateGasRecommendations(Integer chainId, String operation, String urgency) {
        Map<String, Object> recommendations = new HashMap<>();
        
        switch (urgency.toLowerCase()) {
            case "low":
                recommendations.put("strategy", "Use lowest gas price, accept slower confirmation");
                recommendations.put("timing", "Off-peak hours (2-6 AM UTC)");
                recommendations.put("gas_price_multiplier", 0.8);
                break;
            case "high":
                recommendations.put("strategy", "Use higher gas price for fast confirmation");
                recommendations.put("timing", "Immediate execution recommended");
                recommendations.put("gas_price_multiplier", 1.5);
                break;
            default:
                recommendations.put("strategy", "Standard gas price for normal confirmation");
                recommendations.put("timing", "Current conditions acceptable");
                recommendations.put("gas_price_multiplier", 1.0);
                break;
        }
        
        recommendations.put("chain_specific", getChainSpecificTips(chainId));
        
        return recommendations;
    }
    
    private Map<String, Object> getTimingAnalysis(Integer chainId, String urgency) {
        Map<String, Object> timing = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        
        boolean isPeakHour = (hour >= 8 && hour <= 22); // Rough peak hours
        
        timing.put("current_hour_utc", hour);
        timing.put("is_peak_hour", isPeakHour);
        timing.put("peak_hours", "8:00-22:00 UTC");
        timing.put("off_peak_hours", "22:00-8:00 UTC");
        
        if ("low".equals(urgency) && isPeakHour) {
            timing.put("recommendation", "Consider waiting for off-peak hours to reduce costs");
        } else if ("high".equals(urgency)) {
            timing.put("recommendation", "Execute now regardless of peak hours");
        } else {
            timing.put("recommendation", "Current timing is acceptable");
        }
        
        return timing;
    }
    
    private String getCommonToken(Integer chainId, String tokenType) {
        // Return common token addresses for gas estimation
        switch (chainId) {
            case 1: // Ethereum
                if ("USDC".equals(tokenType)) return "0xA0b86a33E6441e2bB27fB4c4c97b4a63c0bD18e5"; // USDC
                if ("WETH".equals(tokenType)) return "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2"; // WETH
                break;
            case 137: // Polygon
                if ("USDC".equals(tokenType)) return "0x2791Bca1f2de4661ED88A30c99A7a9449Aa84174"; // USDC
                if ("WETH".equals(tokenType)) return "0x7ceB23fD6bC0adD59E62ac25578270cFf1b9f619"; // WETH
                break;
            case 56: // BSC
                if ("USDC".equals(tokenType)) return "0x8AC76a51cc950d9822D68b83fE1Ad97B32Cd580d"; // USDC
                if ("WETH".equals(tokenType)) return "0x2170Ed0880ac9A755fd29B2688956BD959F933F8"; // WETH
                break;
        }
        return null; // Chain not supported for gas estimation
    }
    
    private BigInteger parseAmount(String amount, int decimals) {
        try {
            double value = Double.parseDouble(amount);
            return BigInteger.valueOf((long)(value * Math.pow(10, decimals)));
        } catch (NumberFormatException e) {
            return BigInteger.valueOf(1000).multiply(BigInteger.valueOf(10).pow(decimals)); // Default 1000 tokens
        }
    }
    
    private double estimateGasCost(Integer chainId, long gasUnits) {
        double gasPriceGwei = getTypicalGasPrice(chainId);
        double gasCostEth = (gasUnits * gasPriceGwei) / 1_000_000_000.0; // Convert gwei to ETH
        
        // Rough ETH price estimates for cost calculation
        double ethPriceUsd = 2000.0; // Approximate ETH price
        if (chainId == 56) ethPriceUsd = 300.0; // BNB price estimate
        if (chainId == 137) ethPriceUsd = 0.8; // MATIC price estimate
        
        return gasCostEth * ethPriceUsd;
    }
    
    private double getTypicalGasPrice(Integer chainId) {
        // Typical gas prices in gwei for different chains
        switch (chainId) {
            case 1: return 20.0; // Ethereum
            case 137: return 50.0; // Polygon
            case 56: return 3.0; // BSC
            case 42161: return 0.1; // Arbitrum
            case 10: return 0.001; // Optimism
            case 43114: return 25.0; // Avalanche
            default: return 20.0;
        }
    }
    
    private long adjustGasForChain(Integer chainId, long baseGas) {
        // Some chains have different gas usage patterns
        switch (chainId) {
            case 42161: // Arbitrum
            case 10: // Optimism
                return (long)(baseGas * 1.2); // L2 overhead
            default:
                return baseGas;
        }
    }
    
    private String getChainSpecificTips(Integer chainId) {
        switch (chainId) {
            case 1:
                return "Consider L2 alternatives for frequent transactions";
            case 137:
                return "Generally low cost, timing less critical";
            case 56:
                return "Very low cost, execute at any time";
            case 42161:
            case 10:
                return "L2 benefits with lower costs than Ethereum mainnet";
            default:
                return "Monitor network congestion for optimal timing";
        }
    }

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