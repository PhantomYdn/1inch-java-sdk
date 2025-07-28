package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchFusionPlusQuoterApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of FusionPlusQuoterService providing cross-chain quote generation and order building.
 * Supports reactive, synchronous, and asynchronous programming models.
 */
@Slf4j
@RequiredArgsConstructor
public class FusionPlusQuoterServiceImpl implements FusionPlusQuoterService {
    
    private final OneInchFusionPlusQuoterApiService apiService;
    
    // ==================== CROSS-CHAIN QUOTE GENERATION ====================
    
    @Override
    public Single<GetQuoteOutput> getQuoteRx(FusionPlusQuoteRequest request) {
        log.info("Getting FusionPlus quote (reactive) for srcChain={}, dstChain={}, srcToken={}, dstToken={}, amount={}", 
                request.getSrcChain(), request.getDstChain(), request.getSrcTokenAddress(), 
                request.getDstTokenAddress(), request.getAmount());
        
        return apiService.getQuote(
                        request.getSrcChain(),
                        request.getDstChain(),
                        request.getSrcTokenAddress(),
                        request.getDstTokenAddress(),
                        request.getAmount(),
                        request.getWalletAddress(),
                        request.getEnableEstimate(),
                        request.getFee(),
                        request.getIsPermit2(),
                        request.getPermit())
                .doOnSuccess(response -> log.debug("FusionPlus quote generated: quoteId={}, srcAmount={}, dstAmount={}, recommendedPreset={}", 
                        response.getQuoteId(), response.getSrcTokenAmount(), response.getDstTokenAmount(), response.getRecommendedPreset()))
                .doOnError(error -> log.error("FusionPlus quote request failed for srcChain={}, dstChain={}", 
                        request.getSrcChain(), request.getDstChain(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetQuoteOutput> getQuoteAsync(FusionPlusQuoteRequest request) {
        return getQuoteRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetQuoteOutput getQuote(FusionPlusQuoteRequest request) throws OneInchException {
        try {
            return getQuoteRx(request).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== CUSTOM PRESET QUOTES ====================
    
    @Override
    public Single<GetQuoteOutput> getQuoteWithCustomPresetsRx(FusionPlusQuoteRequest request, CustomPresetParams customPresetParams) {
        log.info("Getting FusionPlus quote with custom presets (reactive) for srcChain={}, dstChain={}, customPreset with auctionDuration={}", 
                request.getSrcChain(), request.getDstChain(), customPresetParams.getAuctionDuration());
        
        return apiService.getQuoteWithCustomPresets(
                        request.getSrcChain(),
                        request.getDstChain(),
                        request.getSrcTokenAddress(),
                        request.getDstTokenAddress(),
                        request.getAmount(),
                        request.getWalletAddress(),
                        request.getEnableEstimate(),
                        request.getFee(),
                        request.getIsPermit2(),
                        request.getPermit(),
                        customPresetParams)
                .doOnSuccess(response -> log.debug("FusionPlus custom preset quote generated: quoteId={}, recommendedPreset={}", 
                        response.getQuoteId(), response.getRecommendedPreset()))
                .doOnError(error -> log.error("FusionPlus custom preset quote request failed for srcChain={}, dstChain={}", 
                        request.getSrcChain(), request.getDstChain(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetQuoteOutput> getQuoteWithCustomPresetsAsync(FusionPlusQuoteRequest request, CustomPresetParams customPresetParams) {
        return getQuoteWithCustomPresetsRx(request, customPresetParams).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetQuoteOutput getQuoteWithCustomPresets(FusionPlusQuoteRequest request, CustomPresetParams customPresetParams) throws OneInchException {
        try {
            return getQuoteWithCustomPresetsRx(request, customPresetParams).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== ORDER BUILDING ====================
    
    @Override
    public Single<BuildOrderOutput> buildQuoteTypedDataRx(FusionPlusBuildOrderRequest request, BuildOrderBody buildOrderBody) {
        log.info("Building FusionPlus order typed data (reactive) for srcChain={}, dstChain={}, preset={}", 
                request.getSrcChain(), request.getDstChain(), request.getPreset());
        
        return apiService.buildQuoteTypedData(
                        request.getSrcChain(),
                        request.getDstChain(),
                        request.getSrcTokenAddress(),
                        request.getDstTokenAddress(),
                        request.getAmount(),
                        request.getWalletAddress(),
                        request.getFee(),
                        request.getSource(),
                        request.getIsPermit2(),
                        request.getIsMobile(),
                        request.getFeeReceiver(),
                        request.getPermit(),
                        request.getPreset(),
                        buildOrderBody)
                .doOnSuccess(response -> log.debug("FusionPlus order built: orderHash={}, extension length={}", 
                        response.getOrderHash(), response.getExtension() != null ? response.getExtension().length() : 0))
                .doOnError(error -> log.error("FusionPlus order building failed for srcChain={}, dstChain={}", 
                        request.getSrcChain(), request.getDstChain(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<BuildOrderOutput> buildQuoteTypedDataAsync(FusionPlusBuildOrderRequest request, BuildOrderBody buildOrderBody) {
        return buildQuoteTypedDataRx(request, buildOrderBody).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public BuildOrderOutput buildQuoteTypedData(FusionPlusBuildOrderRequest request, BuildOrderBody buildOrderBody) throws OneInchException {
        try {
            return buildQuoteTypedDataRx(request, buildOrderBody).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
}