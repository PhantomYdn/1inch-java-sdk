package io.oneinch.mcp.resources;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.balance.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Resource for accessing token balance and allowance data across blockchains.
 * Provides comprehensive balance information including token holdings and spending allowances.
 */
@ApplicationScoped
public class BalanceResource {

    private static final Logger log = LoggerFactory.getLogger(BalanceResource.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Get token balances for a wallet address.
     * Format: /balances/{chainId}/{address}
     */
    public CompletableFuture<String> getBalances(String chainId, String address) {
        log.info("Retrieving balances for address {} on chain {}", address, chainId);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            BalanceRequest request = BalanceRequest.builder()
                    .chainId(chain)
                    .walletAddress(address)
                    .build();
            
            return integrationService.getBalances(request)
                    .thenApply(balances -> {
                        Map<String, Object> result = responseMapper.mapBalanceData(balances);
                        return formatBalancesResponse(result, address, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving balances for {}:{}: {}", chainId, address, throwable.getMessage());
                        return formatErrorResponse("balances_error", throwable.getMessage(), address);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", address)
            );
        }
    }

    /**
     * Get custom token balances for specific tokens.
     * Format: /balances/{chainId}/{address}/custom
     */
    public CompletableFuture<String> getCustomBalances(String chainId, String address, List<String> tokenAddresses) {
        log.info("Retrieving custom balances for address {} on chain {} for {} tokens", address, chainId, tokenAddresses.size());
        
        if (tokenAddresses == null || tokenAddresses.isEmpty()) {
            return CompletableFuture.completedFuture(
                formatErrorResponse("missing_tokens", "Token addresses are required for custom balance lookup", address)
            );
        }
        
        try {
            int chain = Integer.parseInt(chainId);
            
            CustomBalanceRequest request = CustomBalanceRequest.builder()
                    .chainId(chain)
                    .walletAddress(address)
                    .tokens(tokenAddresses)
                    .build();
            
            return integrationService.getCustomBalances(request)
                    .thenApply(balances -> {
                        Map<String, Object> result = responseMapper.mapBalanceData(balances);
                        return formatCustomBalancesResponse(result, address, chainId, tokenAddresses);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving custom balances for {}:{}: {}", chainId, address, throwable.getMessage());
                        return formatErrorResponse("custom_balances_error", throwable.getMessage(), address);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", address)
            );
        }
    }

    /**
     * Get token allowances for a spender.
     * Format: /balances/{chainId}/{address}/allowances/{spender}
     */
    public CompletableFuture<String> getAllowances(String chainId, String address, String spender) {
        log.info("Retrieving allowances for address {} spender {} on chain {}", address, spender, chainId);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
                    .chainId(chain)
                    .walletAddress(address)
                    .spender(spender)
                    .build();
            
            return integrationService.getAllowances(request)
                    .thenApply(allowances -> {
                        Map<String, Object> result = responseMapper.mapBalanceData(allowances);
                        return formatAllowancesResponse(result, address, spender, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving allowances for {}:{}:{}: {}", chainId, address, spender, throwable.getMessage());
                        return formatErrorResponse("allowances_error", throwable.getMessage(), address);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", address)
            );
        }
    }

    /**
     * Get combined balances and allowances for a spender.
     * Format: /balances/{chainId}/{address}/combined/{spender}
     */
    public CompletableFuture<String> getBalancesAndAllowances(String chainId, String address, String spender) {
        log.info("Retrieving combined balances and allowances for address {} spender {} on chain {}", address, spender, chainId);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
                    .chainId(chain)
                    .walletAddress(address)
                    .spender(spender)
                    .build();
            
            return integrationService.getBalancesAndAllowances(request)
                    .thenApply(combined -> {
                        Map<String, Object> result = responseMapper.mapBalanceAndAllowanceData(combined);
                        return formatCombinedResponse(result, address, spender, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving combined data for {}:{}:{}: {}", chainId, address, spender, throwable.getMessage());
                        return formatErrorResponse("combined_data_error", throwable.getMessage(), address);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", address)
            );
        }
    }

    /**
     * Get aggregated balance data for multiple wallets.
     * Format: /balances/{chainId}/aggregated/{spender}
     */
    public CompletableFuture<String> getAggregatedBalances(String chainId, String spender, List<String> addresses) {
        log.info("Retrieving aggregated balances for {} addresses with spender {} on chain {}", addresses.size(), spender, chainId);
        
        if (addresses == null || addresses.isEmpty()) {
            return CompletableFuture.completedFuture(
                formatErrorResponse("missing_addresses", "Wallet addresses are required for aggregated balance lookup", "multiple")
            );
        }
        
        try {
            int chain = Integer.parseInt(chainId);
            
            AggregatedBalanceRequest request = AggregatedBalanceRequest.builder()
                    .chainId(chain)
                    .spender(spender)
                    .wallets(addresses)
                    .build();
            
            return integrationService.getAggregatedBalances(request)
                    .thenApply(aggregated -> {
                        List<Map<String, Object>> result = aggregated.stream()
                                .map(responseMapper::mapAggregatedBalanceResponse)
                                .collect(Collectors.toList());
                        return formatAggregatedResponse(result, addresses, spender, chainId);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving aggregated balances for chain {}: {}", chainId, throwable.getMessage());
                        return formatErrorResponse("aggregated_balances_error", throwable.getMessage(), "multiple");
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", "multiple")
            );
        }
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatBalancesResponse(Map<String, Object> result, String address, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"balances/%s/%s\"," +
            "\"type\": \"token_balances\"," +
            "\"chain_id\": %s," +
            "\"chain_name\": \"%s\"," +
            "\"address\": \"%s\"," +
            "\"total_tokens\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, address, chainId,
            getChainName(Integer.parseInt(chainId)),
            address,
            result.containsKey("balances") ? ((Map<?, ?>) result.get("balances")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatCustomBalancesResponse(Map<String, Object> result, String address, String chainId, List<String> tokenAddresses) {
        return String.format(
            "{" +
            "\"resource\": \"balances/%s/%s/custom\"," +
            "\"type\": \"custom_balances\"," +
            "\"chain_id\": %s," +
            "\"chain_name\": \"%s\"," +
            "\"address\": \"%s\"," +
            "\"requested_tokens\": %d," +
            "\"found_balances\": %d," +
            "\"token_addresses\": %s," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, address, chainId,
            getChainName(Integer.parseInt(chainId)),
            address,
            tokenAddresses.size(),
            result.containsKey("balances") ? ((Map<?, ?>) result.get("balances")).size() : 0,
            toJsonString(tokenAddresses),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatAllowancesResponse(Map<String, Object> result, String address, String spender, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"balances/%s/%s/allowances/%s\"," +
            "\"type\": \"token_allowances\"," +
            "\"chain_id\": %s," +
            "\"chain_name\": \"%s\"," +
            "\"address\": \"%s\"," +
            "\"spender\": \"%s\"," +
            "\"total_allowances\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, address, spender, chainId,
            getChainName(Integer.parseInt(chainId)),
            address, spender,
            result.containsKey("balances") ? ((Map<?, ?>) result.get("balances")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatCombinedResponse(Map<String, Object> result, String address, String spender, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"balances/%s/%s/combined/%s\"," +
            "\"type\": \"balances_and_allowances\"," +
            "\"chain_id\": %s," +
            "\"chain_name\": \"%s\"," +
            "\"address\": \"%s\"," +
            "\"spender\": \"%s\"," +
            "\"total_tokens\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, address, spender, chainId,
            getChainName(Integer.parseInt(chainId)),
            address, spender,
            result.containsKey("items") ? ((Map<?, ?>) result.get("items")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatAggregatedResponse(List<Map<String, Object>> result, List<String> addresses, String spender, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"balances/%s/aggregated/%s\"," +
            "\"type\": \"aggregated_balances\"," +
            "\"chain_id\": %s," +
            "\"chain_name\": \"%s\"," +
            "\"spender\": \"%s\"," +
            "\"total_addresses\": %d," +
            "\"addresses\": %s," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, spender, chainId,
            getChainName(Integer.parseInt(chainId)),
            spender,
            addresses.size(),
            toJsonString(addresses),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, String resource) {
        return String.format(
            "{" +
            "\"resource\": \"balances/%s\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
                "\"type\": \"%s\"," +
                "\"message\": \"%s\"," +
                "\"timestamp\": %d" +
            "}" +
            "}", 
            resource, errorType, message.replace("\"", "\\\""), System.currentTimeMillis()
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
        if (obj instanceof String) return "\"" + ((String) obj).replace("\"", "\\\"") + "\"";
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