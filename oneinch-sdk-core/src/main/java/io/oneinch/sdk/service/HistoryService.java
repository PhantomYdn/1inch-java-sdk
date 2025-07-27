package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.history.HistoryEventsRequest;
import io.oneinch.sdk.model.history.HistoryResponseDto;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;

/**
 * Service for interacting with the 1inch History API.
 * Provides transaction history for user addresses.
 */
public interface HistoryService {

    /**
     * Get history events for an address (synchronous).
     * 
     * @param request History events request parameters
     * @return History response with events
     * @throws OneInchException if the request fails
     */
    HistoryResponseDto getHistoryEvents(HistoryEventsRequest request) throws OneInchException;

    /**
     * Get history events for an address (asynchronous with CompletableFuture).
     * 
     * @param request History events request parameters
     * @return CompletableFuture containing history response
     */
    CompletableFuture<HistoryResponseDto> getHistoryEventsAsync(HistoryEventsRequest request);

    /**
     * Get history events for an address (reactive with RxJava).
     * 
     * @param request History events request parameters
     * @return Single containing history response
     */
    Single<HistoryResponseDto> getHistoryEventsRx(HistoryEventsRequest request);
}