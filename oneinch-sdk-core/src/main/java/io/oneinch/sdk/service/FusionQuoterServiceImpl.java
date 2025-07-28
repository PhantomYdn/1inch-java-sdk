package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchFusionQuoterApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.CustomPresetInput;
import io.oneinch.sdk.model.fusion.FusionQuoteRequest;
import io.oneinch.sdk.model.fusion.GetQuoteOutput;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of FusionQuoterService providing quote generation with preset configurations.
 * Supports reactive, synchronous, and asynchronous programming models.
 */
@Slf4j
@RequiredArgsConstructor
public class FusionQuoterServiceImpl implements FusionQuoterService {
    
    private final OneInchFusionQuoterApiService apiService;
    
    // ==================== STANDARD QUOTES ====================
    
    @Override
    public Single<GetQuoteOutput> getQuoteRx(FusionQuoteRequest request) {
        log.info("Getting Fusion quote (reactive) for chain {} from {} to {} with amount {}", 
                request.getChainId(), request.getFromTokenAddress(), 
                request.getToTokenAddress(), request.getAmount());
        
        return apiService.getQuote(
                        request.getChainId(),
                        request.getFromTokenAddress(),
                        request.getToTokenAddress(),
                        request.getAmount(),
                        request.getWalletAddress(),
                        request.getEnableEstimate(),
                        request.getFee(),
                        request.getShowDestAmountMinusFee(),
                        request.getIsPermit2(),
                        request.getSurplus(),
                        request.getPermit(),
                        request.getSlippage(),
                        request.getSource())
                .doOnSuccess(response -> log.debug("Fusion quote retrieved: quoteId={}, recommended_preset={}", 
                        response.getQuoteId(), response.getRecommendedPreset()))
                .doOnError(error -> log.error("Fusion quote request failed for chain {}", 
                        request.getChainId(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetQuoteOutput> getQuoteAsync(FusionQuoteRequest request) {
        return getQuoteRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetQuoteOutput getQuote(FusionQuoteRequest request) throws OneInchException {
        try {
            return getQuoteRx(request).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== CUSTOM PRESET QUOTES ====================
    
    @Override
    public Single<GetQuoteOutput> getQuoteWithCustomPresetsRx(FusionQuoteRequest request, CustomPresetInput customPreset) {
        log.info("Getting Fusion quote with custom presets (reactive) for chain {} from {} to {} with amount {} and custom preset duration={}", 
                request.getChainId(), request.getFromTokenAddress(), 
                request.getToTokenAddress(), request.getAmount(), 
                customPreset.getAuctionDuration());
        
        return apiService.getQuoteWithCustomPresets(
                        request.getChainId(),
                        request.getFromTokenAddress(),
                        request.getToTokenAddress(),
                        request.getAmount(),
                        request.getWalletAddress(),
                        request.getEnableEstimate(),
                        request.getFee(),
                        request.getShowDestAmountMinusFee(),
                        request.getIsPermit2(),
                        request.getSurplus(),
                        request.getPermit(),
                        request.getSource(),
                        customPreset)
                .doOnSuccess(response -> log.debug("Fusion quote with custom presets retrieved: quoteId={}, custom preset included", 
                        response.getQuoteId()))
                .doOnError(error -> log.error("Fusion quote with custom presets request failed for chain {}", 
                        request.getChainId(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetQuoteOutput> getQuoteWithCustomPresetsAsync(FusionQuoteRequest request, CustomPresetInput customPreset) {
        return getQuoteWithCustomPresetsRx(request, customPreset).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetQuoteOutput getQuoteWithCustomPresets(FusionQuoteRequest request, CustomPresetInput customPreset) throws OneInchException {
        try {
            return getQuoteWithCustomPresetsRx(request, customPreset).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
}