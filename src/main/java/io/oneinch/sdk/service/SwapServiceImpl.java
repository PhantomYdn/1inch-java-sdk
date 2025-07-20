package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchApiService;
import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class SwapServiceImpl implements SwapService {
    
    private final OneInchApiService apiService;
    
    // Reactive methods (preferred)
    @Override
    public Single<QuoteResponse> getQuoteRx(QuoteRequest request) {
        log.info("Getting quote (reactive) for swap from {} to {} with amount {}", 
                request.getSrc(), request.getDst(), request.getAmount());
        
        return apiService.getQuote(
                request.getSrc(),
                request.getDst(),
                request.getAmount(),
                request.getProtocols(),
                request.getFee(),
                request.getGasPrice(),
                request.getComplexityLevel(),
                request.getParts(),
                request.getMainRouteParts(),
                request.getGasLimit(),
                request.getIncludeTokensInfo(),
                request.getIncludeProtocols(),
                request.getIncludeGas(),
                request.getConnectorTokens(),
                request.getExcludedProtocols()
        ).doOnSuccess(response -> log.debug("Quote response: {}", response.getDstAmount()))
         .doOnError(error -> log.error("Quote request failed", error))
         .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public Single<SwapResponse> getSwapRx(SwapRequest request) {
        log.info("Getting swap (reactive) from {} to {} with amount {} for address {}", 
                request.getSrc(), request.getDst(), request.getAmount(), request.getFrom());
        
        return apiService.getSwap(
                request.getSrc(),
                request.getDst(),
                request.getAmount(),
                request.getFrom(),
                request.getOrigin(),
                request.getSlippage(),
                request.getProtocols(),
                request.getFee(),
                request.getGasPrice(),
                request.getComplexityLevel(),
                request.getParts(),
                request.getMainRouteParts(),
                request.getGasLimit(),
                request.getIncludeTokensInfo(),
                request.getIncludeProtocols(),
                request.getIncludeGas(),
                request.getConnectorTokens(),
                request.getExcludedProtocols(),
                request.getPermit(),
                request.getReceiver(),
                request.getReferrer(),
                request.getAllowPartialFill(),
                request.getDisableEstimate(),
                request.getUsePermit2()
        ).doOnSuccess(response -> log.debug("Swap response: {}", response.getDstAmount()))
         .doOnError(error -> log.error("Swap request failed", error))
         .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public Single<SpenderResponse> getSpenderRx() {
        log.info("Getting spender address (reactive)");
        return apiService.getSpender()
                .doOnSuccess(response -> log.debug("Spender address: {}", response.getAddress()))
                .doOnError(error -> log.error("Spender request failed", error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public Single<ApproveCallDataResponse> getApproveTransactionRx(ApproveTransactionRequest request) {
        log.info("Getting approve transaction (reactive) for token {} with amount {}", 
                request.getTokenAddress(), request.getAmount());
        
        return apiService.getApproveTransaction(request.getTokenAddress(), request.getAmount())
                .doOnSuccess(response -> log.debug("Approve transaction data: {}", response.getData()))
                .doOnError(error -> log.error("Approve transaction request failed", error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public Single<AllowanceResponse> getAllowanceRx(AllowanceRequest request) {
        log.info("Getting allowance (reactive) for token {} and wallet {}", 
                request.getTokenAddress(), request.getWalletAddress());
        
        return apiService.getAllowance(request.getTokenAddress(), request.getWalletAddress())
                .doOnSuccess(response -> log.debug("Allowance: {}", response.getAllowance()))
                .doOnError(error -> log.error("Allowance request failed", error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    // Legacy methods (backward compatibility)
    @Override
    @Deprecated
    public QuoteResponse getQuote(QuoteRequest request) throws OneInchException {
        try {
            return getQuoteRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Quote request failed", e);
        }
    }
    
    @Override
    @Deprecated
    public CompletableFuture<QuoteResponse> getQuoteAsync(QuoteRequest request) {
        return getQuoteRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    @Deprecated
    public SwapResponse getSwap(SwapRequest request) throws OneInchException {
        try {
            return getSwapRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Swap request failed", e);
        }
    }
    
    @Override
    @Deprecated
    public CompletableFuture<SwapResponse> getSwapAsync(SwapRequest request) {
        return getSwapRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    @Deprecated
    public SpenderResponse getSpender() throws OneInchException {
        try {
            return getSpenderRx().blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Spender request failed", e);
        }
    }
    
    @Override
    @Deprecated
    public CompletableFuture<SpenderResponse> getSpenderAsync() {
        return getSpenderRx().toCompletionStage().toCompletableFuture();
    }
    
    @Override
    @Deprecated
    public ApproveCallDataResponse getApproveTransaction(ApproveTransactionRequest request) throws OneInchException {
        try {
            return getApproveTransactionRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Approve transaction request failed", e);
        }
    }
    
    @Override
    @Deprecated
    public CompletableFuture<ApproveCallDataResponse> getApproveTransactionAsync(ApproveTransactionRequest request) {
        return getApproveTransactionRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    @Deprecated
    public AllowanceResponse getAllowance(AllowanceRequest request) throws OneInchException {
        try {
            return getAllowanceRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Allowance request failed", e);
        }
    }
    
    @Override
    @Deprecated
    public CompletableFuture<AllowanceResponse> getAllowanceAsync(AllowanceRequest request) {
        return getAllowanceRx(request).toCompletionStage().toCompletableFuture();
    }
}