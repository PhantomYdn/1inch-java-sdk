package io.oneinch.mcp.resources;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.price.*;
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
 * MCP Resource for accessing real-time token pricing data across blockchains.
 * Supports 45+ currencies and multiple pricing endpoints for comprehensive market data.
 */
@ApplicationScoped
public class PriceResource {

    private static final Logger log = LoggerFactory.getLogger(PriceResource.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Get all whitelist token prices for a specific blockchain.
     * Format: /prices/{chainId}
     */
    public CompletableFuture<String> getWhitelistPrices(String chainId, String currency) {
        log.info("Retrieving whitelist prices for chain {} in currency {}", chainId, currency);
        
        try {
            int chain = Integer.parseInt(chainId);
            Currency curr = currency != null ? Currency.valueOf(currency.toUpperCase()) : null;
            
            return integrationService.getWhitelistPrices(chain, curr)
                    .thenApply(prices -> {
                        Map<String, Object> result = responseMapper.mapPrices(prices);
                        return formatWhitelistPricesResponse(result, chain, currency);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving whitelist prices for chain {}: {}", chainId, throwable.getMessage());
                        return formatErrorResponse("whitelist_prices_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        } catch (IllegalArgumentException e) {
            log.warn("Invalid currency format: {}", currency);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_currency", "Currency must be a supported currency code", chainId)
            );
        }
    }

    /**
     * Get price for a specific token.
     * Format: /prices/{chainId}/{tokenAddress}
     */
    public CompletableFuture<String> getTokenPrice(String chainId, String tokenAddress, String currency) {
        log.info("Retrieving price for token {}:{} in currency {}", chainId, tokenAddress, currency);
        
        try {
            int chain = Integer.parseInt(chainId);
            Currency curr = currency != null ? Currency.valueOf(currency.toUpperCase()) : null;
            
            return integrationService.getSingleTokenPrice(chain, tokenAddress, curr)
                    .thenApply(price -> {
                        return formatSinglePriceResponse(price, tokenAddress, chain, currency);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving price for token {}:{}: {}", chainId, tokenAddress, throwable.getMessage());
                        return formatErrorResponse("token_price_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        } catch (IllegalArgumentException e) {
            log.warn("Invalid currency format: {}", currency);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_currency", "Currency must be a supported currency code", chainId)
            );
        }
    }

    /**
     * Get prices for multiple tokens.
     * Format: /prices/{chainId}/multiple
     */
    public CompletableFuture<String> getMultipleTokenPrices(String chainId, List<String> addresses, String currency) {
        log.info("Retrieving prices for {} tokens on chain {} in currency {}", addresses.size(), chainId, currency);
        
        if (addresses == null || addresses.isEmpty()) {
            return CompletableFuture.completedFuture(
                formatErrorResponse("missing_addresses", "Token addresses are required", chainId)
            );
        }
        
        try {
            int chain = Integer.parseInt(chainId);
            Currency curr = currency != null ? Currency.valueOf(currency.toUpperCase()) : null;
            
            PriceRequest request = PriceRequest.builder()
                    .chainId(chain)
                    .addresses(addresses)
                    .currency(curr)
                    .build();
            
            return integrationService.getTokenPrices(request)
                    .thenApply(prices -> {
                        Map<String, Object> result = responseMapper.mapPrices(prices);
                        return formatMultiplePricesResponse(result, addresses, chain, currency);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving multiple prices on chain {}: {}", chainId, throwable.getMessage());
                        return formatErrorResponse("multiple_prices_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        } catch (IllegalArgumentException e) {
            log.warn("Invalid currency format: {}", currency);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_currency", "Currency must be a supported currency code", chainId)
            );
        }
    }

    /**
     * Get supported currencies for pricing.
     * Format: /prices/currencies/{chainId}
     */
    public CompletableFuture<String> getSupportedCurrencies(String chainId) {
        log.info("Retrieving supported currencies for chain {}", chainId);
        
        try {
            int chain = Integer.parseInt(chainId);
            
            return integrationService.getSupportedCurrencies(chain)
                    .thenApply(currencies -> {
                        return formatSupportedCurrenciesResponse(currencies, chain);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error retrieving supported currencies for chain {}: {}", chainId, throwable.getMessage());
                        return formatErrorResponse("currencies_error", throwable.getMessage(), chainId);
                    });
                    
        } catch (NumberFormatException e) {
            log.warn("Invalid chain ID format: {}", chainId);
            return CompletableFuture.completedFuture(
                formatErrorResponse("invalid_chain_id", "Chain ID must be a valid integer", chainId)
            );
        }
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatWhitelistPricesResponse(Map<String, Object> result, int chainId, String currency) {
        return String.format(
            "{" +
            "\"resource\": \"prices/%d\"," +
            "\"type\": \"whitelist_prices\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"currency\": \"%s\"," +
            "\"total_tokens\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, chainId,
            getChainName(chainId),
            currency != null ? currency : "native",
            result.containsKey("prices") ? ((Map<?, ?>) result.get("prices")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatSinglePriceResponse(BigInteger price, String tokenAddress, int chainId, String currency) {
        return String.format(
            "{" +
            "\"resource\": \"prices/%d/%s\"," +
            "\"type\": \"single_price\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"token_address\": \"%s\"," +
            "\"currency\": \"%s\"," +
            "\"price\": \"%s\"," +
            "\"price_formatted\": \"%s\"," +
            "\"timestamp\": %d" +
            "}", 
            chainId, tokenAddress, chainId,
            getChainName(chainId),
            tokenAddress,
            currency != null ? currency : "native",
            price.toString(),
            formatPrice(price, currency),
            System.currentTimeMillis()
        );
    }

    private String formatMultiplePricesResponse(Map<String, Object> result, List<String> addresses, int chainId, String currency) {
        return String.format(
            "{" +
            "\"resource\": \"prices/%d/multiple\"," +
            "\"type\": \"multiple_prices\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"currency\": \"%s\"," +
            "\"requested_tokens\": %d," +
            "\"found_prices\": %d," +
            "\"addresses\": %s," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, chainId,
            getChainName(chainId),
            currency != null ? currency : "native",
            addresses.size(),
            result.containsKey("prices") ? ((Map<?, ?>) result.get("prices")).size() : 0,
            toJsonString(addresses),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatSupportedCurrenciesResponse(List<String> currencies, int chainId) {
        return String.format(
            "{" +
            "\"resource\": \"prices/currencies/%d\"," +
            "\"type\": \"supported_currencies\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"total_currencies\": %d," +
            "\"currencies\": %s," +
            "\"timestamp\": %d" +
            "}", 
            chainId, chainId,
            getChainName(chainId),
            currencies.size(),
            toJsonString(currencies),
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, String chainId) {
        return String.format(
            "{" +
            "\"resource\": \"prices/%s\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
                "\"type\": \"%s\"," +
                "\"message\": \"%s\"," +
                "\"chain_id\": \"%s\"," +
                "\"timestamp\": %d" +
            "}" +
            "}", 
            chainId, errorType, message.replace("\"", "\\\""), chainId, System.currentTimeMillis()
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

    private String formatPrice(BigInteger price, String currency) {
        if (currency == null || "native".equals(currency)) {
            return price.toString() + " wei";
        }
        
        // For display purposes, convert from wei-based price to readable format
        // This is a simplified implementation - real implementation would handle decimal places properly
        return price.toString() + " " + currency.toUpperCase();
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