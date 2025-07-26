package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchHistoryApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.HistoryEventsRequest;
import io.oneinch.sdk.model.HistoryResponseDto;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final OneInchHistoryApiService apiService;

    @Override
    public Single<HistoryResponseDto> getHistoryEventsRx(HistoryEventsRequest request) {
        log.info("Getting history events (reactive) for address {} with limit {}", 
                request.getAddress(), request.getLimit());

        return apiService.getHistoryEvents(
                request.getAddress(),
                request.getLimit(),
                request.getTokenAddress(),
                request.getChainId(),
                request.getToTimestampMs(),
                request.getFromTimestampMs()
        ).doOnSuccess(response -> log.debug("History events response: {} events retrieved", 
                response.getItems() != null ? response.getItems().size() : 0))
         .doOnError(error -> log.error("History events request failed for address {}", request.getAddress(), error))
         .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }

    @Override
    public HistoryResponseDto getHistoryEvents(HistoryEventsRequest request) throws OneInchException {
        log.info("Getting history events (synchronous) for address {} with limit {}", 
                request.getAddress(), request.getLimit());

        try {
            return getHistoryEventsRx(request).blockingGet();
        } catch (Exception e) {
            if (e instanceof OneInchException) {
                throw (OneInchException) e;
            }
            throw new OneInchException("Failed to get history events for address " + request.getAddress(), e);
        }
    }

    @Override
    public CompletableFuture<HistoryResponseDto> getHistoryEventsAsync(HistoryEventsRequest request) {
        log.info("Getting history events (asynchronous) for address {} with limit {}", 
                request.getAddress(), request.getLimit());

        return getHistoryEventsRx(request)
                .doOnError(error -> log.error("Async history events request failed for address {}", request.getAddress(), error))
                .toCompletionStage()
                .toCompletableFuture();
    }
}