package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.CustomPresetInput;
import io.oneinch.sdk.model.fusion.FusionQuoteRequest;
import io.oneinch.sdk.model.fusion.GetQuoteOutput;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for Fusion Quoter API operations.
 * Provides quote generation with preset configurations and auction mechanisms.
 * 
 * Supports three programming approaches:
 * - Reactive (RxJava): Methods ending with 'Rx' returning Single&lt;T&gt;
 * - Synchronous: Standard methods that block until completion
 * - Asynchronous: Methods ending with 'Async' returning CompletableFuture&lt;T&gt;
 */
public interface FusionQuoterService {
    
    // ==================== STANDARD QUOTES ====================
    
    /**
     * Get quote details based on input data with default presets (reactive).
     *
     * @param request Quote request parameters
     * @return Single containing quote with presets and slippage
     */
    Single<GetQuoteOutput> getQuoteRx(FusionQuoteRequest request);
    
    /**
     * Get quote details based on input data with default presets (asynchronous).
     *
     * @param request Quote request parameters
     * @return CompletableFuture containing quote with presets and slippage
     */
    CompletableFuture<GetQuoteOutput> getQuoteAsync(FusionQuoteRequest request);
    
    /**
     * Get quote details based on input data with default presets (synchronous).
     *
     * @param request Quote request parameters
     * @return Quote with presets and slippage
     * @throws OneInchException if the request fails
     */
    GetQuoteOutput getQuote(FusionQuoteRequest request) throws OneInchException;
    
    // ==================== CUSTOM PRESET QUOTES ====================
    
    /**
     * Get quote with custom preset details (reactive).
     *
     * @param request Quote request parameters
     * @param customPreset Custom preset configuration
     * @return Single containing quote with custom preset details
     */
    Single<GetQuoteOutput> getQuoteWithCustomPresetsRx(FusionQuoteRequest request, CustomPresetInput customPreset);
    
    /**
     * Get quote with custom preset details (asynchronous).
     *
     * @param request Quote request parameters
     * @param customPreset Custom preset configuration
     * @return CompletableFuture containing quote with custom preset details
     */
    CompletableFuture<GetQuoteOutput> getQuoteWithCustomPresetsAsync(FusionQuoteRequest request, CustomPresetInput customPreset);
    
    /**
     * Get quote with custom preset details (synchronous).
     *
     * @param request Quote request parameters
     * @param customPreset Custom preset configuration
     * @return Quote with custom preset details
     * @throws OneInchException if the request fails
     */
    GetQuoteOutput getQuoteWithCustomPresets(FusionQuoteRequest request, CustomPresetInput customPreset) throws OneInchException;
}