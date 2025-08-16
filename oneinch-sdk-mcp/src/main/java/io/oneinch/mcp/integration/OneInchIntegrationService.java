package io.oneinch.mcp.integration;

import io.oneinch.mcp.cache.CacheService;
import io.oneinch.mcp.monitoring.Monitored;
import io.oneinch.mcp.ratelimit.RateLimit;
import io.oneinch.mcp.service.OneInchClientService;
import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.oneinch.sdk.model.swap.*;
import io.oneinch.sdk.model.token.*;
import io.oneinch.sdk.model.price.*;
import io.oneinch.sdk.model.portfolio.*;
import io.oneinch.sdk.model.balance.*;
import io.oneinch.sdk.model.history.*;
import io.oneinch.sdk.model.tokendetails.CurrentValueResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service that integrates MCP server with the 1inch SDK.
 * Provides rate-limited and cached access to all 1inch API functionality.
 */
@ApplicationScoped
public class OneInchIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(OneInchIntegrationService.class);

    @Inject
    OneInchClientService clientService;

    @Inject
    CacheService cacheService;

    @Inject
    ApiResponseMapper responseMapper;

    // === SWAP API INTEGRATION ===

    /**
     * Gets a swap quote with caching and rate limiting.
     */
    @RateLimit(clientId = "swap-quote")
    @Monitored
    public CompletableFuture<QuoteResponse> getSwapQuote(QuoteRequest request) {
        log.debug("Getting swap quote for {}:{} -> {} amount {}", 
                request.getChainId(), request.getSrc(), request.getDst(), request.getAmount());

        return cacheService.cacheSwapRoute(
            request.getChainId(),
            request.getSrc(),
            request.getDst(),
            request.getAmount().toString(),
            () -> executeSwapQuote(request)
        ).thenApply(data -> convertToQuoteResponse(data));
    }

    private CompletableFuture<Map<String, Object>> executeSwapQuote(QuoteRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                QuoteResponse response = client.swap().getQuote(request);
                return convertQuoteToMap(response);
            } catch (OneInchException e) {
                log.error("Error getting swap quote", e);
                throw new RuntimeException("Failed to get swap quote: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Gets swap transaction data with rate limiting.
     */
    @RateLimit(clientId = "swap-transaction")
    public CompletableFuture<SwapResponse> getSwapTransaction(SwapRequest request) {
        log.debug("Getting swap transaction for {}:{} -> {} amount {}", 
                request.getChainId(), request.getSrc(), request.getDst(), request.getAmount());

        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                return client.swap().getSwap(request);
            } catch (OneInchException e) {
                log.error("Error getting swap transaction", e);
                throw new RuntimeException("Failed to get swap transaction: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Gets spender address for approvals.
     */
    @RateLimit(clientId = "swap-spender")
    public CompletableFuture<SpenderResponse> getSpender(Integer chainId) {
        log.debug("Getting spender for chain {}", chainId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                return client.swap().getSpender(chainId);
            } catch (OneInchException e) {
                log.error("Error getting spender", e);
                throw new RuntimeException("Failed to get spender: " + e.getMessage(), e);
            }
        });
    }

    // === TOKEN API INTEGRATION ===

    /**
     * Gets token list for a specific chain with caching and rate limiting.
     */
    @RateLimit(clientId = "token-list")
    @Monitored
    public CompletableFuture<TokenListResponse> getTokenList(TokenListRequest request) {
        log.debug("Getting token list for chain {} provider {}", 
                request.getChainId(), request.getProvider());

        return cacheService.cacheTokenData(
                request.getChainId(),
                "list-" + request.getProvider(),
                () -> {
                    try {
                        TokenListResponse response = clientService.getClient().token().getTokenList(request);
                        return CompletableFuture.completedFuture(
                            responseMapper.mapTokenList(response)
                        );
                    } catch (OneInchException e) {
                        log.error("Error fetching token list: {}", e.getMessage());
                        return CompletableFuture.failedFuture(e);
                    }
                }
        ).thenApply(cachedData -> {
            // Convert cached data back to TokenListResponse if needed
            return responseMapper.unmapTokenListResponse(cachedData);
        });
    }

    /**
     * Searches tokens with caching and rate limiting.
     */

    /**
     * Gets custom token information with caching and rate limiting.
     */
    @RateLimit(clientId = "token-custom")
    @Monitored
    public CompletableFuture<List<Token>> getCustomTokens(CustomTokenRequest request) {
        log.debug("Getting custom tokens for chain {} addresses {}", 
                request.getChainId(), request.getAddresses());

        return cacheService.cacheTokenData(
                request.getChainId(),
                "custom-" + String.join(",", request.getAddresses()),
                () -> {
                    try {
                        Map<String, Token> tokens = clientService.getClient().token().getCustomTokens(request);
                        return CompletableFuture.completedFuture(
                            responseMapper.mapCustomTokens(tokens)
                        );
                    } catch (OneInchException e) {
                        log.error("Error fetching custom tokens: {}", e.getMessage());
                        return CompletableFuture.failedFuture(e);
                    }
                }
        ).thenApply(cachedData -> {
            return responseMapper.unmapTokenList(cachedData);
        });
    }

    /**
     * Gets multi-chain token information with caching and rate limiting.
     */
    @RateLimit(clientId = "token-multichain")
    @Monitored
    public CompletableFuture<Map<Integer, List<Token>>> getMultiChainTokens() {
        log.debug("Getting multi-chain token information");

        return cacheService.cacheTokenData(
                null, // No specific chain
                "multi-chain-all",
                () -> {
                    try {
                        TokenListRequest multiChainRequest = TokenListRequest.builder()
                                .provider("1inch")
                                .build();
                        List<ProviderTokenDto> tokens = clientService.getClient().token().getMultiChainTokens(multiChainRequest);
                        return CompletableFuture.completedFuture(
                            responseMapper.mapMultiChainTokens(tokens)
                        );
                    } catch (OneInchException e) {
                        log.error("Error fetching multi-chain tokens: {}", e.getMessage());
                        return CompletableFuture.failedFuture(e);
                    }
                }
        ).thenApply(cachedData -> {
            return responseMapper.unmapMultiChainTokens(cachedData);
        });
    }

    /**
     * Gets token lists with caching and rate limiting.
     */
    @RateLimit(clientId = "tokens-list")
    public CompletableFuture<Map<String, ProviderTokenDto>> getTokens(TokenListRequest request) {
        log.debug("Getting tokens for chain {} with provider {}", request.getChainId(), request.getProvider());

        String cacheKey = String.format("%s:%s", request.getChainId(), request.getProvider());
        
        return cacheService.cacheTokenData(
            request.getChainId(),
            cacheKey,
            () -> executeGetTokens(request)
        ).thenApply(data -> convertToTokenMap(data));
    }

    private CompletableFuture<Map<String, Object>> executeGetTokens(TokenListRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                Map<String, ProviderTokenDto> response = client.token().getTokens(request);
                return convertTokenMapToGeneric(response);
            } catch (OneInchException e) {
                log.error("Error getting tokens", e);
                throw new RuntimeException("Failed to get tokens: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Searches tokens with caching and rate limiting.
     */
    @RateLimit(clientId = "tokens-search")
    public CompletableFuture<List<Token>> searchTokens(TokenSearchRequest request) {
        log.debug("Searching tokens: {} on chain {}", request.getQuery(), request.getChainId());

        String cacheKey = String.format("search:%s:%s", request.getChainId(), request.getQuery());
        
        return cacheService.cacheTokenData(
            request.getChainId(),
            cacheKey,
            () -> executeSearchTokens(request)
        ).thenApply(data -> convertToTokenList(data));
    }

    private CompletableFuture<Map<String, Object>> executeSearchTokens(TokenSearchRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                List<Token> response = client.token().searchTokens(request);
                return Map.of("tokens", response);
            } catch (OneInchException e) {
                log.error("Error searching tokens", e);
                throw new RuntimeException("Failed to search tokens: " + e.getMessage(), e);
            }
        });
    }

    // === PRICE API INTEGRATION ===

    /**
     * Gets token prices with caching and rate limiting.
     */
    @RateLimit(clientId = "price-request")
    public CompletableFuture<Map<String, BigInteger>> getTokenPrices(PriceRequest request) {
        log.debug("Getting prices for chain {} with currency {}", request.getChainId(), request.getCurrency());

        return cacheService.cacheTokenPrice(
            request.getChainId(),
            "prices:" + request.getCurrency(),
            () -> executeGetPrices(request)
        ).thenApply(price -> Map.of("price", price));
    }

    private CompletableFuture<BigInteger> executeGetPrices(PriceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                Map<String, BigInteger> response = client.price().getPrices(request);
                // Return first price as example - real implementation would return full map
                return response.values().stream().findFirst().orElse(BigInteger.ZERO);
            } catch (OneInchException e) {
                log.error("Error getting prices", e);
                throw new RuntimeException("Failed to get prices: " + e.getMessage(), e);
            }
        });
    }

    // === PORTFOLIO API INTEGRATION ===

    /**
     * Gets portfolio data with caching and rate limiting.
     */
    @RateLimit(clientId = "portfolio-value")
    public CompletableFuture<CurrentValueResponse> getPortfolioValue(PortfolioV5OverviewRequest request) {
        log.debug("Getting portfolio value for addresses: {}", request.getAddresses());

        String addressKey = String.join(",", request.getAddresses());
        String chainKey = request.getChainId() != null ? request.getChainId().toString() : "all";

        return cacheService.cachePortfolioData(
            addressKey,
            chainKey,
            () -> executeGetPortfolioValue(request)
        ).thenApply(data -> convertToCurrentValueResponse(data));
    }

    private CompletableFuture<Map<String, Object>> executeGetPortfolioValue(PortfolioV5OverviewRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                CurrentValueResponse response = client.portfolio().getCurrentValue(request);
                return convertPortfolioToMap(response);
            } catch (OneInchException e) {
                log.error("Error getting portfolio value", e);
                throw new RuntimeException("Failed to get portfolio value: " + e.getMessage(), e);
            }
        });
    }

    // === BALANCE API INTEGRATION ===

    /**
     * Gets token balances with caching and rate limiting.
     */
    @RateLimit(clientId = "balance-request")
    public CompletableFuture<Map<String, BigInteger>> getBalances(BalanceRequest request) {
        log.debug("Getting balances for {}:{}", request.getChainId(), request.getWalletAddress());

        return cacheService.cacheBalanceData(
            request.getChainId(),
            request.getWalletAddress(),
            "balances",
            () -> executeGetBalances(request)
        );
    }

    private CompletableFuture<Map<String, BigInteger>> executeGetBalances(BalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                return client.balance().getBalances(request);
            } catch (OneInchException e) {
                log.error("Error getting balances", e);
                throw new RuntimeException("Failed to get balances: " + e.getMessage(), e);
            }
        });
    }

    // === HISTORY API INTEGRATION ===

    /**
     * Gets transaction history with caching and rate limiting.
     */
    @RateLimit(clientId = "history-request")
    public CompletableFuture<HistoryResponseDto> getHistory(HistoryEventsRequest request) {
        log.debug("Getting history for address: {}", request.getAddress());

        String chainKey = request.getChainId() != null ? request.getChainId().toString() : "all";
        Integer limit = request.getLimit() != null ? request.getLimit() : 10;

        return cacheService.cacheHistoryData(
            request.getAddress(),
            chainKey,
            limit,
            () -> executeGetHistory(request)
        ).thenApply(data -> convertToHistoryResponse(data));
    }

    private CompletableFuture<List<Map<String, Object>>> executeGetHistory(HistoryEventsRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                HistoryResponseDto response = client.history().getHistoryEvents(request);
                return List.of(convertHistoryToMap(response));
            } catch (OneInchException e) {
                log.error("Error getting history", e);
                throw new RuntimeException("Failed to get history: " + e.getMessage(), e);
            }
        });
    }

    // === UTILITY METHODS FOR DATA CONVERSION ===

    private QuoteResponse convertToQuoteResponse(Map<String, Object> data) {
        // Implementation would convert cached map back to QuoteResponse
        // For now, return a basic response
        QuoteResponse response = new QuoteResponse();
        response.setDstAmount((BigInteger) data.get("dstAmount"));
        return response;
    }

    private Map<String, Object> convertQuoteToMap(QuoteResponse response) {
        return Map.of(
            "dstAmount", response.getDstAmount(),
            "fromToken", response.getSrcToken(),
            "toToken", response.getDstToken()
        );
    }

    private Map<String, ProviderTokenDto> convertToTokenMap(Map<String, Object> data) {
        // Implementation would convert cached data back to proper token map
        return Map.of();
    }

    private Map<String, Object> convertTokenMapToGeneric(Map<String, ProviderTokenDto> tokens) {
        return Map.of("tokens", tokens);
    }

    private List<Token> convertToTokenList(Map<String, Object> data) {
        @SuppressWarnings("unchecked")
        List<Token> tokens = (List<Token>) data.get("tokens");
        return tokens != null ? tokens : List.of();
    }

    private CurrentValueResponse convertToCurrentValueResponse(Map<String, Object> data) {
        // Implementation would convert cached data back to CurrentValueResponse
        return new CurrentValueResponse();
    }

    private Map<String, Object> convertPortfolioToMap(CurrentValueResponse response) {
        return Map.of(
            "total", response.getTotal(),
            "byAddress", response.getByAddress(),
            "byChain", response.getByChain()
        );
    }

    private HistoryResponseDto convertToHistoryResponse(List<Map<String, Object>> data) {
        // Implementation would convert cached data back to HistoryResponseDto
        return new HistoryResponseDto();
    }

    private Map<String, Object> convertHistoryToMap(HistoryResponseDto response) {
        return Map.of(
            "items", response.getItems(),
            "cacheCounter", response.getCacheCounter()
        );
    }
}