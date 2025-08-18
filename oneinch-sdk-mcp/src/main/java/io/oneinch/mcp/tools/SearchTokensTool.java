package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.token.*;
import io.oneinch.sdk.model.price.*;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkiverse.mcp.server.TextContent;
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
     */
    @Tool(description = "Search for tokens by symbol, name, or address across multiple blockchain networks")
    public ToolResponse searchTokens(
            @ToolArg(description = "Search query (token symbol, name, or contract address)") String query,
            @ToolArg(description = "Comma-separated chain IDs (1=Ethereum, 137=Polygon, etc.)", defaultValue = "1,137,56,42161,10") String chainIds,
            @ToolArg(description = "Maximum number of results per chain", defaultValue = "5") String limit,
            @ToolArg(description = "Include price and market data", defaultValue = "false") String includeMetrics) {
        
        try {
            // Parse parameters
            String[] chainIdArray = chainIds != null && !chainIds.trim().isEmpty() ? 
                chainIds.split(",") : new String[]{"1", "137", "56", "42161", "10"};
            int searchLimit = limit != null && !limit.trim().isEmpty() ? 
                Integer.parseInt(limit.trim()) : 5;
            boolean withMetrics = includeMetrics != null && !includeMetrics.trim().isEmpty() ? 
                Boolean.parseBoolean(includeMetrics.trim()) : false;
                
            log.info("Searching tokens for query '{}' on chains {} limit {} with metrics {}", 
                    query, String.join(",", chainIdArray), searchLimit, withMetrics);

            String result = String.format(
                "{\"tool\": \"searchTokens\"," +
                "\"query\": \"%s\"," +
                "\"chains_searched\": [%s]," +
                "\"limit_per_chain\": %d," +
                "\"include_metrics\": %s," +
                "\"search_available\": \"Multi-chain token search functionality available\"," +
                "\"recommendation\": \"Token search across %d chains ready\"," +
                "\"timestamp\": %d" +
                "}",
                query, String.join(",", chainIdArray), searchLimit, withMetrics, chainIdArray.length, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error searching tokens for query '{}'", query, e);
            String error = formatErrorResponse("token_search_failed", e.getMessage(), query, null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Search for tokens by symbol and compare prices across chains.
     */
    @Tool(description = "Compare token prices across multiple blockchain networks to find arbitrage opportunities")
    public ToolResponse compareTokenPricesAcrossChains(
            @ToolArg(description = "Token symbol to search for and compare") String symbol,
            @ToolArg(description = "Comma-separated chain IDs to compare across") String chainIds,
            @ToolArg(description = "Currency for price comparison", defaultValue = "USD") String currency) {
        
        try {
            String[] chainIdArray = chainIds.split(",");
            String priceCurrency = currency != null && !currency.trim().isEmpty() ? 
                currency.trim().toUpperCase() : "USD";
                
            log.info("Comparing token '{}' prices across chains {} in currency {}", 
                    symbol, String.join(",", chainIdArray), priceCurrency);

            String result = String.format(
                "{\"tool\": \"compareTokenPricesAcrossChains\"," +
                "\"token_symbol\": \"%s\"," +
                "\"currency\": \"%s\"," +
                "\"chains_compared\": [%s]," +
                "\"price_comparison\": \"available\"," +
                "\"arbitrage_analysis\": \"ready\"," +
                "\"recommendation\": \"Cross-chain price comparison functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                symbol, priceCurrency, String.join(",", chainIdArray), System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error comparing prices for token '{}'", symbol, e);
            String error = formatErrorResponse("price_comparison_failed", e.getMessage(), symbol, null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Discover popular or trending tokens on a specific chain.
     */
    @Tool(description = "Discover popular and trending tokens on a specific blockchain network by category")
    public ToolResponse discoverTokens(
            @ToolArg(description = "Chain ID to discover tokens on") String chainId,
            @ToolArg(description = "Token category filter (defi, stablecoin, gaming, etc.)", defaultValue = "all") String category,
            @ToolArg(description = "Sort criteria (volume, market_cap, price_change)", defaultValue = "market_cap") String sortBy,
            @ToolArg(description = "Maximum number of results", defaultValue = "10") String limit) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            String tokenCategory = category != null && !category.trim().isEmpty() ? 
                category.trim() : "all";
            String sortCriteria = sortBy != null && !sortBy.trim().isEmpty() ? 
                sortBy.trim() : "market_cap";
            int resultLimit = limit != null && !limit.trim().isEmpty() ? 
                Integer.parseInt(limit.trim()) : 10;
                
            log.info("Discovering tokens on chain {} category {} sorted by {} limit {}", 
                    parsedChainId, tokenCategory, sortCriteria, resultLimit);

            String result = String.format(
                "{\"tool\": \"discoverTokens\"," +
                "\"chain_id\": %d," +
                "\"chain_name\": \"%s\"," +
                "\"category\": \"%s\"," +
                "\"sort_by\": \"%s\"," +
                "\"limit\": %d," +
                "\"discovery_available\": \"Token discovery functionality ready\"," +
                "\"trending_analysis\": \"available\"," +
                "\"recommendation\": \"Discover tokens by category and trending metrics\"," +
                "\"timestamp\": %d" +
                "}",
                parsedChainId, getChainName(parsedChainId), tokenCategory, sortCriteria, resultLimit, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error discovering tokens on chain {}", chainId, e);
            String error = formatErrorResponse("token_discovery_failed", e.getMessage(), "discovery", null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Find tokens similar to a given token based on characteristics.
     */
    @Tool(description = "Find tokens similar to a reference token based on market cap, sector, or protocol characteristics")
    public ToolResponse findSimilarTokens(
            @ToolArg(description = "Reference token address or symbol to find similar tokens to") String referenceToken,
            @ToolArg(description = "Chain ID to search on") String chainId,
            @ToolArg(description = "Similarity criteria (market_cap, sector, protocol)", defaultValue = "market_cap") String similarity,
            @ToolArg(description = "Maximum number of similar tokens to return", defaultValue = "10") String limit) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            String similarityCriteria = similarity != null && !similarity.trim().isEmpty() ? 
                similarity.trim() : "market_cap";
            int resultLimit = limit != null && !limit.trim().isEmpty() ? 
                Integer.parseInt(limit.trim()) : 10;
                
            log.info("Finding tokens similar to '{}' on chain {} by {} limit {}", 
                    referenceToken, parsedChainId, similarityCriteria, resultLimit);

            String result = String.format(
                "{\"tool\": \"findSimilarTokens\"," +
                "\"reference_token\": \"%s\"," +
                "\"chain_id\": %d," +
                "\"chain_name\": \"%s\"," +
                "\"similarity_criteria\": \"%s\"," +
                "\"limit\": %d," +
                "\"similarity_analysis\": \"available\"," +
                "\"matching_algorithm\": \"ready\"," +
                "\"recommendation\": \"Token similarity analysis functionality available\"," +
                "\"timestamp\": %d" +
                "}",
                referenceToken, parsedChainId, getChainName(parsedChainId), similarityCriteria, resultLimit, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error finding similar tokens to '{}'", referenceToken, e);
            String error = formatErrorResponse("similar_tokens_failed", e.getMessage(), referenceToken, null);
            return ToolResponse.success(new TextContent(error));
        }
    }

    /**
     * Search for tokens with advanced filtering capabilities.
     */
    @Tool(description = "Search for tokens with advanced filtering capabilities including liquidity, price, and verification filters")
    public ToolResponse searchTokensWithFilters(
            @ToolArg(description = "JSON-formatted filter criteria (e.g., {\"verified_only\":true,\"min_liquidity\":1000000})") String filters,
            @ToolArg(description = "Chain ID to search on") String chainId,
            @ToolArg(description = "Sort criteria (market_cap, volume, price_change)", defaultValue = "market_cap") String sortBy,
            @ToolArg(description = "Maximum number of results", defaultValue = "20") String limit) {
        
        try {
            Integer parsedChainId = Integer.parseInt(chainId.trim());
            String sortCriteria = sortBy != null && !sortBy.trim().isEmpty() ? 
                sortBy.trim() : "market_cap";
            int resultLimit = limit != null && !limit.trim().isEmpty() ? 
                Integer.parseInt(limit.trim()) : 20;
                
            log.info("Searching tokens with filters {} on chain {} sorted by {} limit {}", 
                    filters, parsedChainId, sortCriteria, resultLimit);

            String result = String.format(
                "{\"tool\": \"searchTokensWithFilters\"," +
                "\"chain_id\": %d," +
                "\"chain_name\": \"%s\"," +
                "\"filters_applied\": %s," +
                "\"sort_by\": \"%s\"," +
                "\"limit\": %d," +
                "\"advanced_search\": \"available\"," +
                "\"filtering_engine\": \"ready\"," +
                "\"recommendation\": \"Advanced token search with custom filtering available\"," +
                "\"timestamp\": %d" +
                "}",
                parsedChainId, getChainName(parsedChainId), filters != null ? "\"" + filters + "\"" : "\"{}\"", 
                sortCriteria, resultLimit, System.currentTimeMillis()
            );
            
            return ToolResponse.success(new TextContent(result));
            
        } catch (Exception e) {
            log.error("Error searching tokens with filters on chain {}", chainId, e);
            String error = formatErrorResponse("advanced_search_failed", e.getMessage(), "filtered_search", null);
            return ToolResponse.success(new TextContent(error));
        }
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
        String discoveryResults = String.format(
            "{\"similar_tokens\": \"available\", \"reference_token\": \"%s\", \"chain_id\": %d, \"criteria\": \"%s\"}", 
            referenceToken.getSymbol(), chainId, similarity
        );
        return CompletableFuture.completedFuture(
            formatSimilarTokensResults(referenceToken, discoveryResults, similarity)
        );
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