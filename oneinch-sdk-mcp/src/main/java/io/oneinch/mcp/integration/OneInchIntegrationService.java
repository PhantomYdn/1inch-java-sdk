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
import java.util.HashMap;
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
    @Monitored
    public CompletableFuture<Map<String, BigInteger>> getTokenPrices(PriceRequest request) {
        log.debug("Getting prices for chain {} addresses {} currency {}", 
                request.getChainId(), request.getAddresses(), request.getCurrency());

        String cacheKey = buildPriceCacheKey(request);
        return cacheService.cacheTokenData(
            request.getChainId(),
            cacheKey,
            () -> executeGetPrices(request)
        ).thenApply(cachedData -> {
            return responseMapper.unmapPrices(cachedData);
        });
    }

    /**
     * Gets whitelist prices (all tokens) with caching and rate limiting.
     */
    @RateLimit(clientId = "price-whitelist")
    @Monitored
    public CompletableFuture<Map<String, BigInteger>> getWhitelistPrices(Integer chainId, Currency currency) {
        log.debug("Getting whitelist prices for chain {} currency {}", chainId, currency);

        String currencyKey = currency != null ? currency.name() : "native";
        return cacheService.cacheTokenData(
            chainId,
            "whitelist-" + currencyKey,
            () -> executeGetWhitelistPrices(chainId, currency)
        ).thenApply(cachedData -> {
            return responseMapper.unmapPrices(cachedData);
        });
    }

    /**
     * Gets single token price with caching and rate limiting.
     */
    @RateLimit(clientId = "price-single")
    @Monitored
    public CompletableFuture<BigInteger> getSingleTokenPrice(Integer chainId, String address, Currency currency) {
        log.debug("Getting single token price for {}:{} currency {}", chainId, address, currency);

        String currencyKey = currency != null ? currency.name() : "native";
        return cacheService.cacheTokenData(
            chainId,
            "single-" + address + "-" + currencyKey,
            () -> executeGetSinglePrice(chainId, address, currency)
        ).thenApply(cachedData -> {
            if (cachedData instanceof Map && ((Map<?, ?>) cachedData).containsKey("price")) {
                Object priceValue = ((Map<String, Object>) cachedData).get("price");
                return new BigInteger(priceValue.toString());
            }
            return BigInteger.ZERO;
        });
    }

    /**
     * Gets supported currencies with caching and rate limiting.
     */
    @RateLimit(clientId = "price-currencies")
    @Monitored
    public CompletableFuture<List<String>> getSupportedCurrencies(Integer chainId) {
        log.debug("Getting supported currencies for chain {}", chainId);

        return cacheService.cacheTokenData(
            chainId,
            "currencies",
            () -> executeGetSupportedCurrencies(chainId)
        ).thenApply(cachedData -> {
            if (cachedData instanceof Map && ((Map<?, ?>) cachedData).containsKey("currencies")) {
                return (List<String>) ((Map<String, Object>) cachedData).get("currencies");
            }
            return List.of();
        });
    }

    // === PRICE API EXECUTION METHODS ===

    private CompletableFuture<Map<String, Object>> executeGetPrices(PriceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                Map<String, BigInteger> response = client.price().getPrices(request);
                return responseMapper.mapPrices(response);
            } catch (OneInchException e) {
                log.error("Error getting prices", e);
                throw new RuntimeException("Failed to get prices: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetWhitelistPrices(Integer chainId, Currency currency) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                Map<String, BigInteger> response = client.price().getWhitelistPrices(chainId, currency);
                return responseMapper.mapPrices(response);
            } catch (OneInchException e) {
                log.error("Error getting whitelist prices", e);
                throw new RuntimeException("Failed to get whitelist prices: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetSinglePrice(Integer chainId, String address, Currency currency) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                BigInteger response = client.price().getPrice(chainId, address, currency);
                return Map.of("price", response.toString());
            } catch (OneInchException e) {
                log.error("Error getting single price", e);
                throw new RuntimeException("Failed to get single price: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetSupportedCurrencies(Integer chainId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                List<String> response = client.price().getSupportedCurrencies(chainId);
                return Map.of("currencies", response);
            } catch (OneInchException e) {
                log.error("Error getting supported currencies", e);
                throw new RuntimeException("Failed to get supported currencies: " + e.getMessage(), e);
            }
        });
    }

    private String buildPriceCacheKey(PriceRequest request) {
        StringBuilder key = new StringBuilder("prices");
        
        if (request.getCurrency() != null) {
            key.append("-").append(request.getCurrency().name());
        }
        
        if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
            key.append("-").append(String.join(",", request.getAddresses()));
        }
        
        return key.toString();
    }

    // === PORTFOLIO API INTEGRATION ===

    /**
     * Gets portfolio data with caching and rate limiting.
     */
    @RateLimit(clientId = "portfolio-value")
    @Monitored
    public CompletableFuture<CurrentValueResponse> getPortfolioValue(PortfolioV5OverviewRequest request) {
        log.debug("Getting portfolio value for addresses: {}", request.getAddresses());

        String addressKey = String.join(",", request.getAddresses());
        String chainKey = request.getChainId() != null ? request.getChainId().toString() : "all";

        return cacheService.cachePortfolioData(
            addressKey,
            chainKey,
            () -> executeGetPortfolioValue(request)
        ).thenApply(data -> responseMapper.unmapCurrentValueResponse(data));
    }

    /**
     * Gets protocols snapshot with caching and rate limiting.
     */
    @RateLimit(clientId = "portfolio-protocols")
    @Monitored
    public CompletableFuture<List<AdapterResult>> getProtocolsSnapshot(PortfolioV5SnapshotRequest request) {
        log.debug("Getting protocols snapshot for addresses: {}", request.getAddresses());

        String addressKey = String.join(",", request.getAddresses());
        String chainKey = request.getChainId() != null ? request.getChainId().toString() : "all";
        String timestampKey = request.getTimestamp() != null ? request.getTimestamp().toString() : "current";

        return cacheService.cachePortfolioData(
            addressKey,
            "protocols-" + chainKey + "-" + timestampKey,
            () -> executeGetProtocolsSnapshot(request)
        ).thenApply(data -> responseMapper.unmapAdapterResultList(data));
    }

    /**
     * Gets tokens snapshot with caching and rate limiting.
     */
    @RateLimit(clientId = "portfolio-tokens")
    @Monitored
    public CompletableFuture<List<AdapterResult>> getTokensSnapshot(PortfolioV5SnapshotRequest request) {
        log.debug("Getting tokens snapshot for addresses: {}", request.getAddresses());

        String addressKey = String.join(",", request.getAddresses());
        String chainKey = request.getChainId() != null ? request.getChainId().toString() : "all";
        String timestampKey = request.getTimestamp() != null ? request.getTimestamp().toString() : "current";

        return cacheService.cachePortfolioData(
            addressKey,
            "tokens-" + chainKey + "-" + timestampKey,
            () -> executeGetTokensSnapshot(request)
        ).thenApply(data -> responseMapper.unmapAdapterResultList(data));
    }

    /**
     * Gets supported chains with caching and rate limiting.
     */
    @RateLimit(clientId = "portfolio-chains")
    @Monitored
    public CompletableFuture<List<SupportedChainResponse>> getSupportedChains() {
        log.debug("Getting supported chains for portfolio");

        return cacheService.cachePortfolioData(
            "system",
            "supported-chains",
            () -> executeGetSupportedChains()
        ).thenApply(data -> responseMapper.unmapSupportedChainsList(data));
    }

    /**
     * Gets supported protocols with caching and rate limiting.
     */
    @RateLimit(clientId = "portfolio-protocols-list")
    @Monitored
    public CompletableFuture<List<SupportedProtocolGroupResponse>> getSupportedProtocols() {
        log.debug("Getting supported protocols for portfolio");

        return cacheService.cachePortfolioData(
            "system",
            "supported-protocols",
            () -> executeGetSupportedProtocols()
        ).thenApply(data -> responseMapper.unmapSupportedProtocolsList(data));
    }

    // === PORTFOLIO API EXECUTION METHODS ===

    private CompletableFuture<Map<String, Object>> executeGetPortfolioValue(PortfolioV5OverviewRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                CurrentValueResponse response = client.portfolio().getCurrentValue(request);
                return responseMapper.mapCurrentValueResponse(response);
            } catch (OneInchException e) {
                log.error("Error getting portfolio value", e);
                throw new RuntimeException("Failed to get portfolio value: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetProtocolsSnapshot(PortfolioV5SnapshotRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                List<AdapterResult> response = client.portfolio().getProtocolsSnapshot(request);
                return responseMapper.mapAdapterResultList(response);
            } catch (OneInchException e) {
                log.error("Error getting protocols snapshot", e);
                throw new RuntimeException("Failed to get protocols snapshot: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetTokensSnapshot(PortfolioV5SnapshotRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                List<AdapterResult> response = client.portfolio().getTokensSnapshot(request);
                return responseMapper.mapAdapterResultList(response);
            } catch (OneInchException e) {
                log.error("Error getting tokens snapshot", e);
                throw new RuntimeException("Failed to get tokens snapshot: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetSupportedChains() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                List<SupportedChainResponse> response = client.portfolio().getSupportedChains();
                return responseMapper.mapSupportedChainsList(response);
            } catch (OneInchException e) {
                log.error("Error getting supported chains", e);
                throw new RuntimeException("Failed to get supported chains: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetSupportedProtocols() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                List<SupportedProtocolGroupResponse> response = client.portfolio().getSupportedProtocols();
                return responseMapper.mapSupportedProtocolsList(response);
            } catch (OneInchException e) {
                log.error("Error getting supported protocols", e);
                throw new RuntimeException("Failed to get supported protocols: " + e.getMessage(), e);
            }
        });
    }

    // === BALANCE API EXECUTION METHODS ===

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

    private CompletableFuture<Map<String, BigInteger>> executeGetCustomBalances(CustomBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                return client.balance().getCustomBalances(request);
            } catch (OneInchException e) {
                log.error("Error getting custom balances", e);
                throw new RuntimeException("Failed to get custom balances: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, BigInteger>> executeGetAllowances(AllowanceBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                return client.balance().getAllowances(request);
            } catch (OneInchException e) {
                log.error("Error getting allowances", e);
                throw new RuntimeException("Failed to get allowances: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetBalancesAndAllowances(AllowanceBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                Map<String, BalanceAndAllowanceItem> response = client.balance().getAllowancesAndBalances(request);
                return responseMapper.mapBalanceAndAllowanceData(response);
            } catch (OneInchException e) {
                log.error("Error getting balances and allowances", e);
                throw new RuntimeException("Failed to get balances and allowances: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<Map<String, Object>> executeGetAggregatedBalances(AggregatedBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OneInchClient client = clientService.getClient();
                List<AggregatedBalanceResponse> response = client.balance().getAggregatedBalancesAndAllowances(request);
                return responseMapper.mapAggregatedBalanceList(response);
            } catch (OneInchException e) {
                log.error("Error getting aggregated balances", e);
                throw new RuntimeException("Failed to get aggregated balances: " + e.getMessage(), e);
            }
        });
    }

    // === BALANCE API INTEGRATION ===

    /**
     * Gets wallet token balances with caching and rate limiting.
     */
    @RateLimit(clientId = "balance-request")
    @Monitored
    public CompletableFuture<Map<String, BigInteger>> getBalances(BalanceRequest request) {
        log.debug("Getting balances for address {} on chain {}", request.getWalletAddress(), request.getChainId());

        String cacheKey = "balances-" + request.getWalletAddress();
        return cacheService.cacheBalanceData(
            request.getChainId(),
            request.getWalletAddress(),
            cacheKey,
            () -> executeGetBalances(request)
        );
    }

    /**
     * Gets custom token balances with caching and rate limiting.
     */
    @RateLimit(clientId = "balance-custom")
    @Monitored
    public CompletableFuture<Map<String, BigInteger>> getCustomBalances(CustomBalanceRequest request) {
        log.debug("Getting custom balances for address {} tokens {} on chain {}", 
                request.getWalletAddress(), request.getTokens(), request.getChainId());

        String tokenKey = String.join(",", request.getTokens());
        String cacheKey = "custom-" + tokenKey;
        return cacheService.cacheBalanceData(
            request.getChainId(),
            request.getWalletAddress(),
            cacheKey,
            () -> executeGetCustomBalances(request)
        );
    }

    /**
     * Gets token allowances with caching and rate limiting.
     */
    @RateLimit(clientId = "balance-allowances")
    @Monitored
    public CompletableFuture<Map<String, BigInteger>> getAllowances(AllowanceBalanceRequest request) {
        log.debug("Getting allowances for address {} spender {} on chain {}", 
                request.getWalletAddress(), request.getSpender(), request.getChainId());

        String cacheKey = "allowances-" + request.getSpender();
        return cacheService.cacheBalanceData(
            request.getChainId(),
            request.getWalletAddress(),
            cacheKey,
            () -> executeGetAllowances(request)
        );
    }

    /**
     * Gets combined balances and allowances with caching and rate limiting.
     */
    @RateLimit(clientId = "balance-combined")
    @Monitored
    public CompletableFuture<Map<String, BalanceAndAllowanceItem>> getBalancesAndAllowances(AllowanceBalanceRequest request) {
        log.debug("Getting combined balances and allowances for address {} spender {} on chain {}", 
                request.getWalletAddress(), request.getSpender(), request.getChainId());

        String cacheKey = "combined-" + request.getSpender();
        return cacheService.cacheTokenData(
            request.getChainId(),
            cacheKey,
            () -> executeGetBalancesAndAllowances(request)
        ).thenApply(cachedData -> {
            return responseMapper.unmapBalanceAndAllowanceData(cachedData);
        });
    }

    /**
     * Gets aggregated balance data with caching and rate limiting.
     */
    @RateLimit(clientId = "balance-aggregated")
    @Monitored
    public CompletableFuture<List<AggregatedBalanceResponse>> getAggregatedBalances(AggregatedBalanceRequest request) {
        log.debug("Getting aggregated balances for spender {} on chain {} addresses {}", 
                request.getSpender(), request.getChainId(), request.getWallets());

        String addressKey = String.join(",", request.getWallets());
        String cacheKey = "aggregated-" + request.getSpender();
        return cacheService.cacheTokenData(
            request.getChainId(),
            cacheKey,
            () -> executeGetAggregatedBalances(request)
        ).thenApply(cachedData -> {
            return responseMapper.unmapAggregatedBalanceList(cachedData);
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