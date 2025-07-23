package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;

public interface SwapService {
    
    // Quote operations
    /**
     * Get a quote for token swap (synchronous).
     * @param request Quote request parameters
     * @return Quote response with swap details
     * @throws OneInchException if the request fails
     */
    QuoteResponse getQuote(QuoteRequest request) throws OneInchException;
    
    /**
     * Get a quote for token swap (asynchronous with CompletableFuture).
     * @param request Quote request parameters
     * @return CompletableFuture containing quote response
     */
    CompletableFuture<QuoteResponse> getQuoteAsync(QuoteRequest request);
    
    /**
     * Get a quote for token swap (reactive with RxJava).
     * @param request Quote request parameters
     * @return Single containing quote response
     */
    Single<QuoteResponse> getQuoteRx(QuoteRequest request);
    
    // Swap operations
    /**
     * Get swap transaction data (synchronous).
     * @param request Swap request parameters
     * @return Swap response with transaction data
     * @throws OneInchException if the request fails
     */
    SwapResponse getSwap(SwapRequest request) throws OneInchException;
    
    /**
     * Get swap transaction data (asynchronous with CompletableFuture).
     * @param request Swap request parameters
     * @return CompletableFuture containing swap response
     */
    CompletableFuture<SwapResponse> getSwapAsync(SwapRequest request);
    
    /**
     * Get swap transaction data (reactive with RxJava).
     * @param request Swap request parameters
     * @return Single containing swap response
     */
    Single<SwapResponse> getSwapRx(SwapRequest request);
    
    // Spender operations
    /**
     * Get the 1inch router spender address (synchronous).
     * @return Spender response with router address
     * @throws OneInchException if the request fails
     */
    SpenderResponse getSpender(Integer chainId) throws OneInchException;
    
    /**
     * Get the 1inch router spender address (asynchronous with CompletableFuture).
     * @return CompletableFuture containing spender response
     */
    CompletableFuture<SpenderResponse> getSpenderAsync(Integer chainId);
    
    /**
     * Get the 1inch router spender address (reactive with RxJava).
     * @return Single containing spender response
     */
    Single<SpenderResponse> getSpenderRx(Integer chainId);
    
    // Approve operations
    /**
     * Get approve transaction data (synchronous).
     * @param request Approve transaction request parameters
     * @return Approve call data response
     * @throws OneInchException if the request fails
     */
    ApproveCallDataResponse getApproveTransaction(ApproveTransactionRequest request) throws OneInchException;
    
    /**
     * Get approve transaction data (asynchronous with CompletableFuture).
     * @param request Approve transaction request parameters
     * @return CompletableFuture containing approve call data response
     */
    CompletableFuture<ApproveCallDataResponse> getApproveTransactionAsync(ApproveTransactionRequest request);
    
    /**
     * Get approve transaction data (reactive with RxJava).
     * @param request Approve transaction request parameters
     * @return Single containing approve call data response
     */
    Single<ApproveCallDataResponse> getApproveTransactionRx(ApproveTransactionRequest request);
    
    // Allowance operations
    /**
     * Check token allowance (synchronous).
     * @param request Allowance request parameters
     * @return Allowance response with current allowance
     * @throws OneInchException if the request fails
     */
    AllowanceResponse getAllowance(AllowanceRequest request) throws OneInchException;
    
    /**
     * Check token allowance (asynchronous with CompletableFuture).
     * @param request Allowance request parameters
     * @return CompletableFuture containing allowance response
     */
    CompletableFuture<AllowanceResponse> getAllowanceAsync(AllowanceRequest request);
    
    /**
     * Check token allowance (reactive with RxJava).
     * @param request Allowance request parameters
     * @return Single containing allowance response
     */
    Single<AllowanceResponse> getAllowanceRx(AllowanceRequest request);
}