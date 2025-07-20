package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;

import java.util.concurrent.CompletableFuture;

public interface SwapService {
    
    QuoteResponse getQuote(QuoteRequest request) throws OneInchException;
    
    CompletableFuture<QuoteResponse> getQuoteAsync(QuoteRequest request);
    
    SwapResponse getSwap(SwapRequest request) throws OneInchException;
    
    CompletableFuture<SwapResponse> getSwapAsync(SwapRequest request);
    
    SpenderResponse getSpender() throws OneInchException;
    
    CompletableFuture<SpenderResponse> getSpenderAsync();
    
    ApproveCallDataResponse getApproveTransaction(ApproveTransactionRequest request) throws OneInchException;
    
    CompletableFuture<ApproveCallDataResponse> getApproveTransactionAsync(ApproveTransactionRequest request);
    
    AllowanceResponse getAllowance(AllowanceRequest request) throws OneInchException;
    
    CompletableFuture<AllowanceResponse> getAllowanceAsync(AllowanceRequest request);
}