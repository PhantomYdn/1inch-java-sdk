package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;

public interface SwapService {
    
    // Reactive methods (preferred)
    Single<QuoteResponse> getQuoteRx(QuoteRequest request);
    
    Single<SwapResponse> getSwapRx(SwapRequest request);
    
    Single<SpenderResponse> getSpenderRx();
    
    Single<ApproveCallDataResponse> getApproveTransactionRx(ApproveTransactionRequest request);
    
    Single<AllowanceResponse> getAllowanceRx(AllowanceRequest request);
    
    // Legacy methods (for backward compatibility)
    @Deprecated
    QuoteResponse getQuote(QuoteRequest request) throws OneInchException;
    
    @Deprecated
    CompletableFuture<QuoteResponse> getQuoteAsync(QuoteRequest request);
    
    @Deprecated
    SwapResponse getSwap(SwapRequest request) throws OneInchException;
    
    @Deprecated
    CompletableFuture<SwapResponse> getSwapAsync(SwapRequest request);
    
    @Deprecated
    SpenderResponse getSpender() throws OneInchException;
    
    @Deprecated
    CompletableFuture<SpenderResponse> getSpenderAsync();
    
    @Deprecated
    ApproveCallDataResponse getApproveTransaction(ApproveTransactionRequest request) throws OneInchException;
    
    @Deprecated
    CompletableFuture<ApproveCallDataResponse> getApproveTransactionAsync(ApproveTransactionRequest request);
    
    @Deprecated
    AllowanceResponse getAllowance(AllowanceRequest request) throws OneInchException;
    
    @Deprecated
    CompletableFuture<AllowanceResponse> getAllowanceAsync(AllowanceRequest request);
}