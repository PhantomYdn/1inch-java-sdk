package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TokenDetailsService {
    
    // Token details operations
    /**
     * Get native token details (synchronous).
     * @param request Token details request parameters
     * @return Token details response
     * @throws OneInchException if the request fails
     */
    TokenDetailsResponse getNativeTokenDetails(TokenDetailsRequest request) throws OneInchException;
    
    /**
     * Get native token details (asynchronous with CompletableFuture).
     * @param request Token details request parameters
     * @return CompletableFuture containing token details response
     */
    CompletableFuture<TokenDetailsResponse> getNativeTokenDetailsAsync(TokenDetailsRequest request);
    
    /**
     * Get native token details (reactive with RxJava).
     * @param request Token details request parameters
     * @return Single containing token details response
     */
    Single<TokenDetailsResponse> getNativeTokenDetailsRx(TokenDetailsRequest request);
    
    /**
     * Get token details with contract address (synchronous).
     * @param request Token details request parameters (contractAddress required)
     * @return Token details response
     * @throws OneInchException if the request fails
     */
    TokenDetailsResponse getTokenDetails(TokenDetailsRequest request) throws OneInchException;
    
    /**
     * Get token details with contract address (asynchronous with CompletableFuture).
     * @param request Token details request parameters (contractAddress required)
     * @return CompletableFuture containing token details response
     */
    CompletableFuture<TokenDetailsResponse> getTokenDetailsAsync(TokenDetailsRequest request);
    
    /**
     * Get token details with contract address (reactive with RxJava).
     * @param request Token details request parameters (contractAddress required)
     * @return Single containing token details response
     */
    Single<TokenDetailsResponse> getTokenDetailsRx(TokenDetailsRequest request);
    
    // Chart operations - Range based
    /**
     * Get native token historical price chart by time range (synchronous).
     * @param request Chart range request parameters
     * @return Chart data response
     * @throws OneInchException if the request fails
     */
    ChartDataResponse getNativeTokenChartByRange(ChartRangeRequest request) throws OneInchException;
    
    /**
     * Get native token historical price chart by time range (asynchronous with CompletableFuture).
     * @param request Chart range request parameters
     * @return CompletableFuture containing chart data response
     */
    CompletableFuture<ChartDataResponse> getNativeTokenChartByRangeAsync(ChartRangeRequest request);
    
    /**
     * Get native token historical price chart by time range (reactive with RxJava).
     * @param request Chart range request parameters
     * @return Single containing chart data response
     */
    Single<ChartDataResponse> getNativeTokenChartByRangeRx(ChartRangeRequest request);
    
    /**
     * Get token historical price chart by time range (synchronous).
     * @param request Chart range request parameters (tokenAddress required)
     * @return Chart data response
     * @throws OneInchException if the request fails
     */
    ChartDataResponse getTokenChartByRange(ChartRangeRequest request) throws OneInchException;
    
    /**
     * Get token historical price chart by time range (asynchronous with CompletableFuture).
     * @param request Chart range request parameters (tokenAddress required)
     * @return CompletableFuture containing chart data response
     */
    CompletableFuture<ChartDataResponse> getTokenChartByRangeAsync(ChartRangeRequest request);
    
    /**
     * Get token historical price chart by time range (reactive with RxJava).
     * @param request Chart range request parameters (tokenAddress required)
     * @return Single containing chart data response
     */
    Single<ChartDataResponse> getTokenChartByRangeRx(ChartRangeRequest request);
    
    // Chart operations - Interval based
    /**
     * Get native token historical price chart by time interval (synchronous).
     * @param request Chart interval request parameters
     * @return Chart data response
     * @throws OneInchException if the request fails
     */
    ChartDataResponse getNativeTokenChartByInterval(ChartIntervalRequest request) throws OneInchException;
    
    /**
     * Get native token historical price chart by time interval (asynchronous with CompletableFuture).
     * @param request Chart interval request parameters
     * @return CompletableFuture containing chart data response
     */
    CompletableFuture<ChartDataResponse> getNativeTokenChartByIntervalAsync(ChartIntervalRequest request);
    
    /**
     * Get native token historical price chart by time interval (reactive with RxJava).
     * @param request Chart interval request parameters
     * @return Single containing chart data response
     */
    Single<ChartDataResponse> getNativeTokenChartByIntervalRx(ChartIntervalRequest request);
    
    /**
     * Get token historical price chart by time interval (synchronous).
     * @param request Chart interval request parameters (tokenAddress required)
     * @return Chart data response
     * @throws OneInchException if the request fails
     */
    ChartDataResponse getTokenChartByInterval(ChartIntervalRequest request) throws OneInchException;
    
    /**
     * Get token historical price chart by time interval (asynchronous with CompletableFuture).
     * @param request Chart interval request parameters (tokenAddress required)
     * @return CompletableFuture containing chart data response
     */
    CompletableFuture<ChartDataResponse> getTokenChartByIntervalAsync(ChartIntervalRequest request);
    
    /**
     * Get token historical price chart by time interval (reactive with RxJava).
     * @param request Chart interval request parameters (tokenAddress required)
     * @return Single containing chart data response
     */
    Single<ChartDataResponse> getTokenChartByIntervalRx(ChartIntervalRequest request);
    
    // Price change operations
    /**
     * Get native token price change by interval (synchronous).
     * @param request Price change request parameters
     * @return Token price change response
     * @throws OneInchException if the request fails
     */
    TokenPriceChangeResponse getNativeTokenPriceChange(PriceChangeRequest request) throws OneInchException;
    
    /**
     * Get native token price change by interval (asynchronous with CompletableFuture).
     * @param request Price change request parameters
     * @return CompletableFuture containing token price change response
     */
    CompletableFuture<TokenPriceChangeResponse> getNativeTokenPriceChangeAsync(PriceChangeRequest request);
    
    /**
     * Get native token price change by interval (reactive with RxJava).
     * @param request Price change request parameters
     * @return Single containing token price change response
     */
    Single<TokenPriceChangeResponse> getNativeTokenPriceChangeRx(PriceChangeRequest request);
    
    /**
     * Get token price change by interval (synchronous).
     * @param request Price change request parameters (tokenAddress required)
     * @return Token price change response
     * @throws OneInchException if the request fails
     */
    TokenPriceChangeResponse getTokenPriceChange(PriceChangeRequest request) throws OneInchException;
    
    /**
     * Get token price change by interval (asynchronous with CompletableFuture).
     * @param request Price change request parameters (tokenAddress required)
     * @return CompletableFuture containing token price change response
     */
    CompletableFuture<TokenPriceChangeResponse> getTokenPriceChangeAsync(PriceChangeRequest request);
    
    /**
     * Get token price change by interval (reactive with RxJava).
     * @param request Price change request parameters (tokenAddress required)
     * @return Single containing token price change response
     */
    Single<TokenPriceChangeResponse> getTokenPriceChangeRx(PriceChangeRequest request);
    
    /**
     * Get price changes for a list of tokens by interval (synchronous).
     * @param request Token list price change request parameters
     * @return List of token price change responses
     * @throws OneInchException if the request fails
     */
    List<TokenListPriceChangeResponse> getTokenListPriceChange(TokenListPriceChangeRequest request) throws OneInchException;
    
    /**
     * Get price changes for a list of tokens by interval (asynchronous with CompletableFuture).
     * @param request Token list price change request parameters
     * @return CompletableFuture containing list of token price change responses
     */
    CompletableFuture<List<TokenListPriceChangeResponse>> getTokenListPriceChangeAsync(TokenListPriceChangeRequest request);
    
    /**
     * Get price changes for a list of tokens by interval (reactive with RxJava).
     * @param request Token list price change request parameters
     * @return Single containing list of token price change responses
     */
    Single<List<TokenListPriceChangeResponse>> getTokenListPriceChangeRx(TokenListPriceChangeRequest request);
}