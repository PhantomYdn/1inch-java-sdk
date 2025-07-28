package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for FusionPlus Quoter API operations.
 * Provides access to cross-chain price curves and quote generation with enhanced features.
 * 
 * Supports three programming approaches:
 * - Reactive (RxJava): Methods ending with 'Rx' returning Single&lt;T&gt;
 * - Synchronous: Standard methods that block until completion
 * - Asynchronous: Methods ending with 'Async' returning CompletableFuture&lt;T&gt;
 */
public interface FusionPlusQuoterService {
    
    // ==================== CROSS-CHAIN QUOTE GENERATION ====================
    
    /**
     * Get cross-chain quote details based on input data (reactive).
     *
     * @param request Cross-chain quote request parameters
     * @return Single containing cross-chain quote details
     */
    Single<GetQuoteOutput> getQuoteRx(FusionPlusQuoteRequest request);
    
    /**
     * Get cross-chain quote details based on input data (asynchronous).
     *
     * @param request Cross-chain quote request parameters
     * @return CompletableFuture containing cross-chain quote details
     */
    CompletableFuture<GetQuoteOutput> getQuoteAsync(FusionPlusQuoteRequest request);
    
    /**
     * Get cross-chain quote details based on input data (synchronous).
     *
     * @param request Cross-chain quote request parameters
     * @return Cross-chain quote details
     * @throws OneInchException if the request fails
     */
    GetQuoteOutput getQuote(FusionPlusQuoteRequest request) throws OneInchException;
    
    // ==================== CUSTOM PRESET QUOTES ====================
    
    /**
     * Get cross-chain quote with custom preset details (reactive).
     *
     * @param request Cross-chain quote request parameters
     * @param customPresetParams Custom preset configuration
     * @return Single containing quote with custom preset details
     */
    Single<GetQuoteOutput> getQuoteWithCustomPresetsRx(FusionPlusQuoteRequest request, CustomPresetParams customPresetParams);
    
    /**
     * Get cross-chain quote with custom preset details (asynchronous).
     *
     * @param request Cross-chain quote request parameters
     * @param customPresetParams Custom preset configuration
     * @return CompletableFuture containing quote with custom preset details
     */
    CompletableFuture<GetQuoteOutput> getQuoteWithCustomPresetsAsync(FusionPlusQuoteRequest request, CustomPresetParams customPresetParams);
    
    /**
     * Get cross-chain quote with custom preset details (synchronous).
     *
     * @param request Cross-chain quote request parameters
     * @param customPresetParams Custom preset configuration
     * @return Quote with custom preset details
     * @throws OneInchException if the request fails
     */
    GetQuoteOutput getQuoteWithCustomPresets(FusionPlusQuoteRequest request, CustomPresetParams customPresetParams) throws OneInchException;
    
    // ==================== ORDER BUILDING ====================
    
    /**
     * Build cross-chain order by given quote (reactive).
     *
     * @param request Build order request parameters
     * @param buildOrderBody Request body containing quote and secret hashes
     * @return Single containing cross-chain order details with EIP712 typed data
     */
    Single<BuildOrderOutput> buildQuoteTypedDataRx(FusionPlusBuildOrderRequest request, BuildOrderBody buildOrderBody);
    
    /**
     * Build cross-chain order by given quote (asynchronous).
     *
     * @param request Build order request parameters
     * @param buildOrderBody Request body containing quote and secret hashes
     * @return CompletableFuture containing cross-chain order details with EIP712 typed data
     */
    CompletableFuture<BuildOrderOutput> buildQuoteTypedDataAsync(FusionPlusBuildOrderRequest request, BuildOrderBody buildOrderBody);
    
    /**
     * Build cross-chain order by given quote (synchronous).
     *
     * @param request Build order request parameters
     * @param buildOrderBody Request body containing quote and secret hashes
     * @return Cross-chain order details with EIP712 typed data
     * @throws OneInchException if the request fails
     */
    BuildOrderOutput buildQuoteTypedData(FusionPlusBuildOrderRequest request, BuildOrderBody buildOrderBody) throws OneInchException;
}