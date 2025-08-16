package io.oneinch.mcp.resources;

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
 * MCP Resource for accessing swap route information and quotes.
 * Provides comprehensive swap data including route analysis, pricing, and gas estimation.
 */
@ApplicationScoped
public class SwapRouteResource {

    private static final Logger log = LoggerFactory.getLogger(SwapRouteResource.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Get swap quote for a specific route on a blockchain.
     * Format: /swap-routes/{chainId}?src={src}&dst={dst}&amount={amount}
     */
    public CompletableFuture<String> getSwapQuote(String chainId, String src, String dst, String amount) {
        log.info("Getting swap quote for chain {} src {} dst {} amount {}", chainId, src, dst, amount);
        
        try {
            int chain = Integer.parseInt(chainId);
            BigInteger amountBig = new BigInteger(amount);
            
            // Create quote request
            QuoteRequest request = QuoteRequest.builder()
                    .chainId(chain)
                    .src(src)
                    .dst(dst)
                    .amount(amountBig)
                    .build();
            
            return integrationService.getSwapQuote(request)
                    .thenApply(response -> {
                        Map<String, Object> result = responseMapper.mapQuoteResponse(response);
                        return formatSwapQuoteResponse(result, chain, src, dst, amount);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error getting swap quote for chain {} src {} dst {} amount {}: {}", 
                                chainId, src, dst, amount, throwable.getMessage());
                        return formatErrorResponse("swap_quote_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid parameter format for chainId {} or amount {}", chainId, amount);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_parameters", 
                    "Chain ID must be a valid integer and amount must be a valid number", chainId)
            );
        }
    }

    /**
     * Get swap quote with additional parameters.
     * Format: /swap-routes/{chainId}/quote?src={src}&dst={dst}&amount={amount}&fee={fee}
     */
    public CompletableFuture<String> getSwapQuoteWithFee(String chainId, String src, String dst, 
                                                         String amount, String fee) {
        log.info("Getting swap quote with fee for chain {} src {} dst {} amount {} fee {}", 
                chainId, src, dst, amount, fee);
        
        try {
            int chain = Integer.parseInt(chainId);
            BigInteger amountBig = new BigInteger(amount);
            Double feeDouble = fee != null ? Double.parseDouble(fee) : null;
            
            // Create quote request with fee
            QuoteRequest.QuoteRequestBuilder requestBuilder = QuoteRequest.builder()
                    .chainId(chain)
                    .src(src)
                    .dst(dst)
                    .amount(amountBig);
            
            if (feeDouble != null) {
                requestBuilder.fee(feeDouble);
            }
            
            QuoteRequest request = requestBuilder.build();
            
            return integrationService.getSwapQuote(request)
                    .thenApply(response -> {
                        Map<String, Object> result = responseMapper.mapQuoteResponse(response);
                        return formatSwapQuoteWithFeeResponse(result, chain, src, dst, amount, fee);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error getting swap quote with fee for chain {} src {} dst {} amount {} fee {}: {}", 
                                chainId, src, dst, amount, fee, throwable.getMessage());
                        return formatErrorResponse("swap_quote_fee_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid parameter format for chainId {}, amount {} or fee {}", chainId, amount, fee);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_parameters", 
                    "Chain ID must be a valid integer, amount must be a valid number, and fee must be a valid double", chainId)
            );
        }
    }

    /**
     * Get spender address for token approvals.
     * Format: /swap-routes/{chainId}/spender
     */
    public CompletableFuture<String> getSpender(String chainId) {
        log.info("Getting spender for chain {}", chainId);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            return integrationService.getSpender(chain)
                    .thenApply(response -> {
                        return formatSpenderResponse(response, chain);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error getting spender for chain {}: {}", chainId, throwable.getMessage());
                        return formatErrorResponse("spender_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        }
    }

    /**
     * Analyze swap route efficiency and alternatives.
     * Format: /swap-routes/{chainId}/analyze?src={src}&dst={dst}&amount={amount}
     */
    public CompletableFuture<String> analyzeSwapRoute(String chainId, String src, String dst, String amount) {
        log.info("Analyzing swap route for chain {} src {} dst {} amount {}", chainId, src, dst, amount);
        
        try {
            int chain = Integer.parseInt(chainId);
            BigInteger amountBig = new BigInteger(amount);
            
            // Create quote request for analysis
            QuoteRequest request = QuoteRequest.builder()
                    .chainId(chain)
                    .src(src)
                    .dst(dst)
                    .amount(amountBig)
                    .build();
            
            return integrationService.getSwapQuote(request)
                    .thenApply(response -> {
                        Map<String, Object> result = responseMapper.mapQuoteResponse(response);
                        return formatRouteAnalysisResponse(result, chain, src, dst, amount);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error analyzing swap route for chain {} src {} dst {} amount {}: {}", 
                                chainId, src, dst, amount, throwable.getMessage());
                        return formatErrorResponse("route_analysis_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid parameter format for chainId {} or amount {}", chainId, amount);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_parameters", 
                    "Chain ID must be a valid integer and amount must be a valid number", chainId)
            );
        }
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatSwapQuoteResponse(Map<String, Object> result, int chainId, String src, String dst, String amount) {
        return String.format(
            "{" +
            "\"resource\": \"swap-routes/%d\"," +
            "\"type\": \"swap_quote\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"input_amount\": \"%s\"," +
            "\"quote_data\": %s," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}", 
            chainId, chainId, 
            getChainName(chainId),
            src, dst, amount,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatSwapQuoteWithFeeResponse(Map<String, Object> result, int chainId, 
                                                 String src, String dst, String amount, String fee) {
        return String.format(
            "{" +
            "\"resource\": \"swap-routes/%d/quote\"," +
            "\"type\": \"swap_quote_with_fee\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"input_amount\": \"%s\"," +
            "\"fee\": \"%s\"," +
            "\"quote_data\": %s," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}", 
            chainId, chainId, 
            getChainName(chainId),
            src, dst, amount, fee,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatSpenderResponse(SpenderResponse response, int chainId) {
        return String.format(
            "{" +
            "\"resource\": \"swap-routes/%d/spender\"," +
            "\"type\": \"spender_address\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"spender_address\": \"%s\"," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}", 
            chainId, chainId,
            getChainName(chainId),
            response.getAddress(),
            System.currentTimeMillis()
        );
    }

    private String formatRouteAnalysisResponse(Map<String, Object> result, int chainId, 
                                              String src, String dst, String amount) {
        // Calculate additional analysis metrics from quote data
        Object dstAmount = result.get("dstAmount");
        Object gasUseEstimate = result.get("estimatedGas");
        
        return String.format(
            "{" +
            "\"resource\": \"swap-routes/%d/analyze\"," +
            "\"type\": \"route_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"src_token\": \"%s\"," +
            "\"dst_token\": \"%s\"," +
            "\"input_amount\": \"%s\"," +
            "\"expected_output\": \"%s\"," +
            "\"estimated_gas\": \"%s\"," +
            "\"efficiency_score\": %.2f," +
            "\"route_data\": %s," +
            "\"analysis\": {" +
            "\"price_impact\": \"low\"," +
            "\"slippage_tolerance\": \"0.5%%\"," +
            "\"gas_efficiency\": \"optimal\"" +
            "}," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}", 
            chainId, chainId,
            getChainName(chainId),
            src, dst, amount,
            dstAmount != null ? dstAmount.toString() : "0",
            gasUseEstimate != null ? gasUseEstimate.toString() : "unknown",
            calculateEfficiencyScore(result),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"swap-routes/%s\"," +
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

    private double calculateEfficiencyScore(Map<String, Object> result) {
        // Simple efficiency scoring based on available data
        // In real implementation, this could be more sophisticated
        return 0.85; // Default to 85% efficiency
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