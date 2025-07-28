package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.price.Currency;
import io.oneinch.sdk.model.price.PriceRequest;
import io.reactivex.rxjava3.core.Single;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for Price API operations.
 * 
 * The Price API provides real-time token pricing across multiple blockchains
 * and currencies. It supports:
 * - Prices for all whitelisted tokens on a chain
 * - Prices for specific token addresses
 * - Conversion to 60+ fiat currencies
 * - Multi-chain support across 13+ networks
 */
public interface PriceService {

    // ==================== WHITELIST PRICES ====================

    /**
     * Get prices for all whitelisted tokens on a specific chain
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param currency currency for price conversion (null for Wei)
     * @return map of token addresses to their prices
     */
    Map<String, BigInteger> getWhitelistPrices(Integer chainId, Currency currency) throws OneInchException;

    /**
     * Get whitelist prices (async)
     */
    CompletableFuture<Map<String, BigInteger>> getWhitelistPricesAsync(Integer chainId, Currency currency);

    /**
     * Get whitelist prices (reactive)
     */
    Single<Map<String, BigInteger>> getWhitelistPricesRx(Integer chainId, Currency currency);

    // ==================== SPECIFIC TOKEN PRICES ====================

    /**
     * Get prices for specific token addresses
     * @param request request parameters including chainId, addresses, and currency
     * @return map of token addresses to their prices
     */
    Map<String, BigInteger> getPrices(PriceRequest request) throws OneInchException;

    /**
     * Get prices (async)
     */
    CompletableFuture<Map<String, BigInteger>> getPricesAsync(PriceRequest request);

    /**
     * Get prices (reactive)
     */
    Single<Map<String, BigInteger>> getPricesRx(PriceRequest request);

    // ==================== SINGLE TOKEN PRICE ====================

    /**
     * Get price for a single token address
     * @param chainId blockchain network identifier
     * @param address token contract address
     * @param currency currency for price conversion (null for Wei)
     * @return token price in specified currency
     */
    BigInteger getPrice(Integer chainId, String address, Currency currency) throws OneInchException;

    /**
     * Get single price (async)
     */
    CompletableFuture<BigInteger> getPriceAsync(Integer chainId, String address, Currency currency);

    /**
     * Get single price (reactive)
     */
    Single<BigInteger> getPriceRx(Integer chainId, String address, Currency currency);

    // ==================== SUPPORTED CURRENCIES ====================

    /**
     * Get list of supported currencies for price conversion
     * @param chainId blockchain network identifier
     * @return list of supported currency codes
     */
    List<String> getSupportedCurrencies(Integer chainId) throws OneInchException;

    /**
     * Get supported currencies (async)
     */
    CompletableFuture<List<String>> getSupportedCurrenciesAsync(Integer chainId);

    /**
     * Get supported currencies (reactive)
     */
    Single<List<String>> getSupportedCurrenciesRx(Integer chainId);
}