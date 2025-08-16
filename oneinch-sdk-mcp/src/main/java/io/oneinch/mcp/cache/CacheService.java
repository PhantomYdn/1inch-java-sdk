package io.oneinch.mcp.cache;

import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheKey;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Centralized caching service for 1inch MCP Server.
 * Provides intelligent caching for different types of DeFi data with appropriate TTL.
 */
@ApplicationScoped
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    /**
     * Caches token prices with short TTL (30 seconds).
     * 
     * @param chainId blockchain chain ID
     * @param tokenAddress token contract address
     * @param supplier function to fetch price if not cached
     * @return cached or fresh price data
     */
    @CacheResult(cacheName = "prices")
    public CompletableFuture<BigInteger> cacheTokenPrice(
            @CacheKey Integer chainId, 
            @CacheKey String tokenAddress,
            java.util.function.Supplier<CompletableFuture<BigInteger>> supplier) {
        
        log.debug("Fetching token price for {}:{}", chainId, tokenAddress);
        return supplier.get();
    }

    /**
     * Caches token metadata with longer TTL (1 hour).
     * 
     * @param chainId blockchain chain ID
     * @param query search query or specific identifier
     * @param supplier function to fetch tokens if not cached
     * @return cached or fresh token data
     */
    @CacheResult(cacheName = "tokens")
    public CompletableFuture<Map<String, Object>> cacheTokenData(
            @CacheKey Integer chainId,
            @CacheKey String query,
            java.util.function.Supplier<CompletableFuture<Map<String, Object>>> supplier) {
        
        log.debug("Fetching token data for {} with query: {}", chainId, query);
        return supplier.get();
    }

    /**
     * Caches portfolio data with medium TTL (5 minutes).
     * 
     * @param address wallet address
     * @param chainId optional chain ID filter
     * @param supplier function to fetch portfolio if not cached
     * @return cached or fresh portfolio data
     */
    @CacheResult(cacheName = "portfolio")
    public CompletableFuture<Map<String, Object>> cachePortfolioData(
            @CacheKey String address,
            @CacheKey String chainId, // nullable
            java.util.function.Supplier<CompletableFuture<Map<String, Object>>> supplier) {
        
        log.debug("Fetching portfolio data for {} on chain {}", address, chainId);
        return supplier.get();
    }

    /**
     * Caches swap route analysis with short TTL (30 seconds).
     * 
     * @param chainId blockchain chain ID
     * @param src source token address
     * @param dst destination token address  
     * @param amount swap amount
     * @param supplier function to fetch route if not cached
     * @return cached or fresh route data
     */
    @CacheResult(cacheName = "prices") // Reuse prices cache for similar TTL
    public CompletableFuture<Map<String, Object>> cacheSwapRoute(
            @CacheKey Integer chainId,
            @CacheKey String src,
            @CacheKey String dst,
            @CacheKey String amount, // Use string to avoid BigInteger serialization issues
            java.util.function.Supplier<CompletableFuture<Map<String, Object>>> supplier) {
        
        log.debug("Fetching swap route for {}:{} -> {} amount {}", chainId, src, dst, amount);
        return supplier.get();
    }

    /**
     * Caches gas price data with short TTL (30 seconds).
     * 
     * @param chainId blockchain chain ID
     * @param supplier function to fetch gas prices if not cached
     * @return cached or fresh gas price data
     */
    @CacheResult(cacheName = "prices") // Reuse prices cache
    public CompletableFuture<Map<String, BigInteger>> cacheGasPrices(
            @CacheKey Integer chainId,
            java.util.function.Supplier<CompletableFuture<Map<String, BigInteger>>> supplier) {
        
        log.debug("Fetching gas prices for chain {}", chainId);
        return supplier.get();
    }

    /**
     * Caches balance data with medium TTL.
     * 
     * @param chainId blockchain chain ID
     * @param address wallet address
     * @param tokenFilter optional token filter
     * @param supplier function to fetch balances if not cached
     * @return cached or fresh balance data
     */
    @CacheResult(cacheName = "portfolio") // Reuse portfolio cache for similar TTL
    public CompletableFuture<Map<String, BigInteger>> cacheBalanceData(
            @CacheKey Integer chainId,
            @CacheKey String address,
            @CacheKey String tokenFilter, // nullable
            java.util.function.Supplier<CompletableFuture<Map<String, BigInteger>>> supplier) {
        
        log.debug("Fetching balance data for {}:{} filter {}", chainId, address, tokenFilter);
        return supplier.get();
    }

    /**
     * Caches transaction history with longer TTL.
     * 
     * @param address wallet address
     * @param chainId optional chain ID filter
     * @param limit result limit
     * @param supplier function to fetch history if not cached
     * @return cached or fresh history data
     */
    @CacheResult(cacheName = "tokens") // Reuse tokens cache for longer TTL
    public CompletableFuture<List<Map<String, Object>>> cacheHistoryData(
            @CacheKey String address,
            @CacheKey String chainId, // nullable
            @CacheKey Integer limit,
            java.util.function.Supplier<CompletableFuture<List<Map<String, Object>>>> supplier) {
        
        log.debug("Fetching history data for {} on chain {} limit {}", address, chainId, limit);
        return supplier.get();
    }

    /**
     * Logs cache statistics for monitoring.
     */
    public void logCacheStats() {
        // This would integrate with Quarkus cache metrics
        log.info("Cache statistics available via /health endpoint");
    }
}