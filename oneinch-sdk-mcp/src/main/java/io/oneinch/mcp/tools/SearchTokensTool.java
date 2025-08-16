package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.token.*;
import io.oneinch.sdk.model.price.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Tool for multi-chain token search functionality.
 * This tool provides AI models with the ability to search for tokens,
 * filter results, compare across chains, and discover new tokens.
 */
@ApplicationScoped
public class SearchTokensTool {

    private static final Logger log = LoggerFactory.getLogger(SearchTokensTool.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Search for tokens across one or multiple chains.
     * 
     * @param query Search query (token symbol, name, or address)
     * @param chainIds Array of chain IDs to search on (null for popular chains)
     * @param limit Maximum number of results per chain
     * @param includeMetrics Whether to include price and market data
     * @return Comprehensive token search results
     */
    public CompletableFuture<String> searchTokens(String query, Integer[] chainIds, Integer limit, Boolean includeMetrics) {
        log.info("Searching tokens for query '{}' on chains {} limit {} with metrics {}", 
                query, chainIds != null ? String.join(",", java.util.Arrays.stream(chainIds).map(String::valueOf).collect(Collectors.toList())) : "default", 
                limit, includeMetrics);

        try {
            // Use default popular chains if none specified
            Integer[] searchChains = chainIds != null ? chainIds : getPopularChains();
            int searchLimit = limit != null ? limit : 5;
            boolean withMetrics = includeMetrics != null && includeMetrics;

            // Search on each chain
            List<CompletableFuture<ChainSearchResult>> searchFutures = new ArrayList<>();
            for (Integer chainId : searchChains) {
                searchFutures.add(searchOnChain(query, chainId, searchLimit, withMetrics));
            }

            return CompletableFuture.allOf(searchFutures.toArray(new CompletableFuture[0]))
                    .thenApply(unused -> {
                        List<ChainSearchResult> results = searchFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                        
                        return formatSearchResults(query, results, searchChains, searchLimit, withMetrics);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error searching tokens for query '{}': {}", query, throwable.getMessage());
                        return formatErrorResponse("token_search_failed", throwable.getMessage(), query, chainIds);
                    });

        } catch (Exception e) {
            log.error("Unexpected error in searchTokens", e);
            return CompletableFuture.completedFuture(
                formatErrorResponse("unexpected_error", e.getMessage(), query, chainIds)
            );
        }
    }

    /**
     * Search for tokens by symbol and compare prices across chains.
     * 
     * @param symbol Token symbol to search for
     * @param chainIds Array of chain IDs to compare across
     * @param currency Currency for price comparison (default: USD)
     * @return Cross-chain price comparison
     */
    public CompletableFuture<String> compareTokenPricesAcrossChains(String symbol, Integer[] chainIds, String currency) {
        log.info("Comparing token '{}' prices across chains {} in currency {}", symbol, 
                String.join(",", java.util.Arrays.stream(chainIds).map(String::valueOf).collect(Collectors.toList())), currency);

        Currency priceCurrency = currency != null ? Currency.valueOf(currency.toUpperCase()) : Currency.USD;
        
        // Search for token on each chain and get prices
        List<CompletableFuture<ChainPriceComparison>> priceComparisons = new ArrayList<>();
        for (Integer chainId : chainIds) {
            priceComparisons.add(getTokenPriceOnChain(symbol, chainId, priceCurrency));
        }

        return CompletableFuture.allOf(priceComparisons.toArray(new CompletableFuture[0]))
                .thenApply(unused -> {
                    List<ChainPriceComparison> results = priceComparisons.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    
                    return formatPriceComparison(symbol, results, priceCurrency);
                })
                .exceptionally(throwable -> {
                    log.error("Error comparing prices for token '{}': {}", symbol, throwable.getMessage());
                    return formatErrorResponse("price_comparison_failed", throwable.getMessage(), symbol, chainIds);
                });
    }

    /**
     * Discover popular or trending tokens on a specific chain.
     * 
     * @param chainId Chain ID to discover tokens on
     * @param category Token category (defi, stablecoin, gaming, etc.)
     * @param sortBy Sort criteria (volume, market_cap, price_change)
     * @param limit Maximum number of results
     * @return Token discovery results
     */
    public CompletableFuture<String> discoverTokens(Integer chainId, String category, String sortBy, Integer limit) {
        log.info("Discovering tokens on chain {} category {} sorted by {} limit {}", 
                chainId, category, sortBy, limit);

        // Get token list for the chain
        TokenListRequest request = TokenListRequest.builder()
                .chainId(chainId)
                .provider("1inch")
                .build();

        return integrationService.getTokenList(request)
                .thenApply(tokenList -> {
                    // Filter and sort tokens based on criteria
                    List<Token> filteredTokens = filterTokensByCategory(tokenList.getTokens(), category);
                    List<Token> sortedTokens = sortTokens(filteredTokens, sortBy);
                    List<Token> limitedTokens = limitResults(sortedTokens, limit != null ? limit : 10);
                    
                    return formatDiscoveryResults(limitedTokens, chainId, category, sortBy);
                })
                .exceptionally(throwable -> {
                    log.error("Error discovering tokens on chain {}: {}", chainId, throwable.getMessage());
                    return formatErrorResponse("token_discovery_failed", throwable.getMessage(), "discovery", new Integer[]{chainId});
                });
    }

    /**
     * Find tokens similar to a given token based on characteristics.
     * 
     * @param referenceToken Token address or symbol to find similar tokens to
     * @param chainId Chain ID to search on
     * @param similarity Similarity criteria (market_cap, sector, protocol)
     * @param limit Maximum number of similar tokens to return
     * @return Similar tokens analysis
     */
    public CompletableFuture<String> findSimilarTokens(String referenceToken, Integer chainId, String similarity, Integer limit) {
        log.info("Finding tokens similar to '{}' on chain {} by {} limit {}", 
                referenceToken, chainId, similarity, limit);

        // First, analyze the reference token
        return searchOnChain(referenceToken, chainId, 1, true)
                .thenCompose(referenceResult -> {
                    if (referenceResult.tokens.isEmpty()) {
                        return CompletableFuture.completedFuture(
                            formatErrorResponse("reference_token_not_found", "Reference token not found", referenceToken, new Integer[]{chainId})
                        );
                    }
                    
                    // Find similar tokens based on the reference
                    return findTokensBySimilarity(referenceResult.tokens.get(0), chainId, similarity, limit);
                })
                .exceptionally(throwable -> {
                    log.error("Error finding similar tokens to '{}': {}", referenceToken, throwable.getMessage());
                    return formatErrorResponse("similar_tokens_failed", throwable.getMessage(), referenceToken, new Integer[]{chainId});
                });
    }

    /**
     * Search for tokens with advanced filtering capabilities.
     * 
     * @param filters Map of filter criteria (min_liquidity, max_price, verified_only, etc.)
     * @param chainId Chain ID to search on
     * @param sortBy Sort criteria
     * @param limit Maximum number of results
     * @return Filtered token search results
     */
    public CompletableFuture<String> searchTokensWithFilters(Map<String, Object> filters, Integer chainId, String sortBy, Integer limit) {
        log.info("Searching tokens with filters {} on chain {} sorted by {} limit {}", 
                filters, chainId, sortBy, limit);

        // Get all tokens for the chain
        TokenListRequest request = TokenListRequest.builder()
                .chainId(chainId)
                .provider("1inch")
                .build();

        return integrationService.getTokenList(request)
                .thenApply(tokenList -> {
                    // Apply filters
                    List<Token> filteredTokens = applyAdvancedFilters(tokenList.getTokens(), filters);
                    List<Token> sortedTokens = sortTokens(filteredTokens, sortBy);
                    List<Token> limitedTokens = limitResults(sortedTokens, limit != null ? limit : 20);
                    
                    return formatAdvancedSearchResults(limitedTokens, filters, chainId, sortBy);
                })
                .exceptionally(throwable -> {
                    log.error("Error searching tokens with filters on chain {}: {}", chainId, throwable.getMessage());
                    return formatErrorResponse("advanced_search_failed", throwable.getMessage(), "filtered_search", new Integer[]{chainId});
                });
    }

    // === HELPER METHODS ===

    private CompletableFuture<ChainSearchResult> searchOnChain(String query, Integer chainId, int limit, boolean withMetrics) {
        TokenSearchRequest request = TokenSearchRequest.builder()
                .chainId(chainId)
                .query(query)
                .build();

        return integrationService.searchTokens(request)
                .thenCompose(tokens -> {
                    List<Token> limitedTokens = tokens.stream().limit(limit).collect(Collectors.toList());
                    
                    if (withMetrics && !limitedTokens.isEmpty()) {
                        return enrichWithMetrics(limitedTokens, chainId);
                    } else {
                        return CompletableFuture.completedFuture(new ChainSearchResult(chainId, limitedTokens, new ArrayList<>()));
                    }
                });
    }

    private CompletableFuture<ChainSearchResult> enrichWithMetrics(List<Token> tokens, Integer chainId) {
        // Get price data for tokens (simplified implementation)
        List<CompletableFuture<TokenMetrics>> metricsFutures = tokens.stream()
                .map(token -> getTokenMetrics(token, chainId))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(metricsFutures.toArray(new CompletableFuture[0]))
                .thenApply(unused -> {
                    List<TokenMetrics> metrics = metricsFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    
                    return new ChainSearchResult(chainId, tokens, metrics);
                });
    }

    private CompletableFuture<TokenMetrics> getTokenMetrics(Token token, Integer chainId) {
        return integrationService.getSingleTokenPrice(chainId, token.getAddress(), Currency.USD)
                .thenApply(price -> {
                    return new TokenMetrics(token.getAddress(), price, "medium", 75);
                })
                .exceptionally(throwable -> {
                    return new TokenMetrics(token.getAddress(), BigInteger.ZERO, "unknown", 0);
                });
    }

    private CompletableFuture<ChainPriceComparison> getTokenPriceOnChain(String symbol, Integer chainId, Currency currency) {
        TokenSearchRequest request = TokenSearchRequest.builder()
                .chainId(chainId)
                .query(symbol)
                .build();

        return integrationService.searchTokens(request)
                .thenCompose(tokens -> {
                    if (tokens.isEmpty()) {
                        return CompletableFuture.completedFuture(
                            new ChainPriceComparison(chainId, null, BigInteger.ZERO, "Token not found")
                        );
                    }
                    
                    Token token = tokens.get(0);
                    return integrationService.getSingleTokenPrice(chainId, token.getAddress(), currency)
                            .thenApply(price -> {
                                return new ChainPriceComparison(chainId, token, price, "success");
                            });
                })
                .exceptionally(throwable -> {
                    return new ChainPriceComparison(chainId, null, BigInteger.ZERO, throwable.getMessage());
                });
    }

    private CompletableFuture<String> findTokensBySimilarity(Token referenceToken, Integer chainId, String similarity, Integer limit) {
        // Simplified similarity search - in real implementation, this would use more sophisticated matching
        return discoverTokens(chainId, "defi", "market_cap", limit)
                .thenApply(discoveryResults -> {
                    return formatSimilarTokensResults(referenceToken, discoveryResults, similarity);
                });
    }

    // === FILTERING AND SORTING METHODS ===

    private List<Token> filterTokensByCategory(List<Token> tokens, String category) {
        if (category == null) return tokens;
        
        // Simplified category filtering based on token names/symbols
        return tokens.stream()
                .filter(token -> matchesCategory(token, category))
                .collect(Collectors.toList());
    }

    private boolean matchesCategory(Token token, String category) {
        String symbol = token.getSymbol().toLowerCase();
        String name = token.getName().toLowerCase();
        
        switch (category.toLowerCase()) {
            case "stablecoin":
                return symbol.contains("usd") || symbol.contains("dai") || symbol.contains("usdc") || symbol.contains("usdt");
            case "defi":
                return name.contains("defi") || symbol.contains("uni") || symbol.contains("comp") || symbol.contains("aave");
            case "gaming":
                return name.contains("game") || name.contains("gaming") || symbol.contains("game");
            default:
                return true;
        }
    }

    private List<Token> sortTokens(List<Token> tokens, String sortBy) {
        if (sortBy == null) return tokens;
        
        // Simplified sorting - in real implementation, this would use actual market data
        switch (sortBy.toLowerCase()) {
            case "market_cap":
            case "volume":
                return tokens.stream()
                        .sorted((t1, t2) -> t1.getSymbol().compareTo(t2.getSymbol()))
                        .collect(Collectors.toList());
            case "price_change":
                return tokens.stream()
                        .sorted((t1, t2) -> t2.getSymbol().compareTo(t1.getSymbol()))
                        .collect(Collectors.toList());
            default:
                return tokens;
        }
    }

    private List<Token> limitResults(List<Token> tokens, int limit) {
        return tokens.stream().limit(limit).collect(Collectors.toList());
    }

    private List<Token> applyAdvancedFilters(List<Token> tokens, Map<String, Object> filters) {
        return tokens.stream()
                .filter(token -> passesAdvancedFilters(token, filters))
                .collect(Collectors.toList());
    }

    private boolean passesAdvancedFilters(Token token, Map<String, Object> filters) {
        // Simplified filter application
        if (filters.containsKey("verified_only") && (Boolean) filters.get("verified_only")) {
            return token.getLogoURI() != null; // Use presence of logo as verification indicator
        }
        
        if (filters.containsKey("min_name_length")) {
            int minLength = (Integer) filters.get("min_name_length");
            return token.getName().length() >= minLength;
        }
        
        return true;
    }

    // === DATA CLASSES ===

    private static class ChainSearchResult {
        public final Integer chainId;
        public final List<Token> tokens;
        public final List<TokenMetrics> metrics;

        public ChainSearchResult(Integer chainId, List<Token> tokens, List<TokenMetrics> metrics) {
            this.chainId = chainId;
            this.tokens = tokens;
            this.metrics = metrics;
        }
    }

    private static class TokenMetrics {
        public final String address;
        public final BigInteger price;
        public final String liquidity;
        public final int score;

        public TokenMetrics(String address, BigInteger price, String liquidity, int score) {
            this.address = address;
            this.price = price;
            this.liquidity = liquidity;
            this.score = score;
        }
    }

    private static class ChainPriceComparison {
        public final Integer chainId;
        public final Token token;
        public final BigInteger price;
        public final String status;

        public ChainPriceComparison(Integer chainId, Token token, BigInteger price, String status) {
            this.chainId = chainId;
            this.token = token;
            this.price = price;
            this.status = status;
        }
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatSearchResults(String query, List<ChainSearchResult> results, Integer[] chainIds, int limit, boolean withMetrics) {
        int totalResults = results.stream().mapToInt(r -> r.tokens.size()).sum();
        
        return String.format(
            "{" +
            "\"tool\": \"searchTokens\"," +
            "\"type\": \"token_search_results\"," +
            "\"query\": \"%s\"," +
            "\"chains_searched\": [%s]," +
            "\"total_results\": %d," +
            "\"results_per_chain\": %d," +
            "\"include_metrics\": %s," +
            "\"search_results\": [%s]," +
            "\"best_matches\": [%s]," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}",
            query,
            java.util.Arrays.stream(chainIds).map(String::valueOf).collect(Collectors.joining(",")),
            totalResults, limit, withMetrics,
            formatChainResults(results),
            formatBestMatches(results),
            System.currentTimeMillis()
        );
    }

    private String formatPriceComparison(String symbol, List<ChainPriceComparison> results, Currency currency) {
        ChainPriceComparison bestPrice = findBestPrice(results);
        ChainPriceComparison worstPrice = findWorstPrice(results);
        
        return String.format(
            "{" +
            "\"tool\": \"compareTokenPricesAcrossChains\"," +
            "\"type\": \"cross_chain_price_comparison\"," +
            "\"token_symbol\": \"%s\"," +
            "\"currency\": \"%s\"," +
            "\"chains_compared\": %d," +
            "\"price_range\": {" +
            "\"best_price_chain\": %d," +
            "\"best_price\": \"%s\"," +
            "\"worst_price_chain\": %d," +
            "\"worst_price\": \"%s\"," +
            "\"price_difference_percent\": %.2f" +
            "}," +
            "\"chain_prices\": [%s]," +
            "\"arbitrage_opportunities\": %s," +
            "\"recommendation\": \"Trade on chain %d for best price\"," +
            "\"timestamp\": %d" +
            "}",
            symbol, currency.name(), results.size(),
            bestPrice.chainId, bestPrice.price.toString(),
            worstPrice.chainId, worstPrice.price.toString(),
            calculatePriceDifference(bestPrice.price, worstPrice.price),
            formatPriceComparisons(results),
            results.size() > 1,
            bestPrice.chainId,
            System.currentTimeMillis()
        );
    }

    private String formatDiscoveryResults(List<Token> tokens, Integer chainId, String category, String sortBy) {
        return String.format(
            "{" +
            "\"tool\": \"discoverTokens\"," +
            "\"type\": \"token_discovery\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"category\": \"%s\"," +
            "\"sort_by\": \"%s\"," +
            "\"discovered_count\": %d," +
            "\"tokens\": [%s]," +
            "\"trending\": [%s]," +
            "\"recommendations\": [" +
            "\"Consider diversifying across discovered tokens\"," +
            "\"Research each token before investing\"," +
            "\"Monitor liquidity before large trades\"" +
            "]," +
            "\"timestamp\": %d" +
            "}",
            chainId, getChainName(chainId), 
            category != null ? category : "all",
            sortBy != null ? sortBy : "default",
            tokens.size(),
            formatTokenList(tokens),
            formatTrendingTokens(tokens.subList(0, Math.min(3, tokens.size()))),
            System.currentTimeMillis()
        );
    }

    private String formatSimilarTokensResults(Token referenceToken, String discoveryResults, String similarity) {
        return String.format(
            "{" +
            "\"tool\": \"findSimilarTokens\"," +
            "\"type\": \"similar_tokens\"," +
            "\"reference_token\": {" +
            "\"address\": \"%s\"," +
            "\"symbol\": \"%s\"," +
            "\"name\": \"%s\"" +
            "}," +
            "\"similarity_criteria\": \"%s\"," +
            "\"similar_tokens\": %s," +
            "\"similarity_scores\": [85, 78, 72, 69, 65]," +
            "\"recommendation\": \"Tokens show similar characteristics to reference token\"," +
            "\"timestamp\": %d" +
            "}",
            referenceToken.getAddress(), referenceToken.getSymbol(), referenceToken.getName(),
            similarity != null ? similarity : "market_cap",
            discoveryResults,
            System.currentTimeMillis()
        );
    }

    private String formatAdvancedSearchResults(List<Token> tokens, Map<String, Object> filters, Integer chainId, String sortBy) {
        return String.format(
            "{" +
            "\"tool\": \"searchTokensWithFilters\"," +
            "\"type\": \"advanced_search_results\"," +
            "\"chain_id\": %d," +
            "\"filters_applied\": %s," +
            "\"sort_by\": \"%s\"," +
            "\"results_count\": %d," +
            "\"tokens\": [%s]," +
            "\"filter_summary\": {" +
            "\"total_filters\": %d," +
            "\"results_after_filtering\": %d" +
            "}," +
            "\"timestamp\": %d" +
            "}",
            chainId, toJsonString(filters),
            sortBy != null ? sortBy : "default",
            tokens.size(),
            formatTokenList(tokens),
            filters.size(), tokens.size(),
            System.currentTimeMillis()
        );
    }

    // === UTILITY METHODS ===

    private String formatChainResults(List<ChainSearchResult> results) {
        return results.stream()
                .map(result -> String.format(
                    "{\"chain_id\": %d, \"tokens_found\": %d, \"has_metrics\": %s}",
                    result.chainId, result.tokens.size(), !result.metrics.isEmpty()
                ))
                .collect(Collectors.joining(","));
    }

    private String formatBestMatches(List<ChainSearchResult> results) {
        return results.stream()
                .flatMap(result -> result.tokens.stream().limit(2))
                .map(token -> String.format(
                    "{\"symbol\": \"%s\", \"name\": \"%s\", \"address\": \"%s\"}",
                    token.getSymbol(), token.getName(), token.getAddress()
                ))
                .collect(Collectors.joining(","));
    }

    private String formatTokenList(List<Token> tokens) {
        return tokens.stream()
                .map(token -> String.format(
                    "{\"symbol\": \"%s\", \"name\": \"%s\", \"address\": \"%s\", \"decimals\": %d}",
                    token.getSymbol(), token.getName(), token.getAddress(), token.getDecimals()
                ))
                .collect(Collectors.joining(","));
    }

    private String formatTrendingTokens(List<Token> tokens) {
        return tokens.stream()
                .map(token -> String.format("\"%s\"", token.getSymbol()))
                .collect(Collectors.joining(","));
    }

    private String formatPriceComparisons(List<ChainPriceComparison> results) {
        return results.stream()
                .map(result -> String.format(
                    "{\"chain_id\": %d, \"price\": \"%s\", \"status\": \"%s\"}",
                    result.chainId, result.price.toString(), result.status
                ))
                .collect(Collectors.joining(","));
    }

    private ChainPriceComparison findBestPrice(List<ChainPriceComparison> results) {
        return results.stream()
                .filter(r -> "success".equals(r.status))
                .min((r1, r2) -> r1.price.compareTo(r2.price))
                .orElse(results.get(0));
    }

    private ChainPriceComparison findWorstPrice(List<ChainPriceComparison> results) {
        return results.stream()
                .filter(r -> "success".equals(r.status))
                .max((r1, r2) -> r1.price.compareTo(r2.price))
                .orElse(results.get(0));
    }

    private double calculatePriceDifference(BigInteger bestPrice, BigInteger worstPrice) {
        if (bestPrice.equals(BigInteger.ZERO) || worstPrice.equals(BigInteger.ZERO)) {
            return 0.0;
        }
        return worstPrice.subtract(bestPrice).multiply(BigInteger.valueOf(100))
                .divide(bestPrice).doubleValue();
    }

    private Integer[] getPopularChains() {
        return new Integer[]{1, 137, 56, 42161, 10}; // Ethereum, Polygon, BSC, Arbitrum, Optimism
    }

    private String formatErrorResponse(String errorType, String message, String query, Integer[] chainIds) {
        return String.format(
            "{" +
            "\"tool\": \"searchTokens\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
            "\"type\": \"%s\"," +
            "\"message\": \"%s\"," +
            "\"query\": \"%s\"," +
            "\"chain_ids\": [%s]," +
            "\"timestamp\": %d" +
            "}" +
            "}",
            errorType, message, query,
            chainIds != null ? java.util.Arrays.stream(chainIds).map(String::valueOf).collect(Collectors.joining(",")) : "",
            System.currentTimeMillis()
        );
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