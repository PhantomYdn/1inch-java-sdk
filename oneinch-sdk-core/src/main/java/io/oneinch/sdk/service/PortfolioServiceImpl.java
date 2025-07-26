package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.PortfolioApiClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of Portfolio API service
 */
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioApiClient portfolioApiClient;

    public PortfolioServiceImpl(PortfolioApiClient portfolioApiClient) {
        this.portfolioApiClient = portfolioApiClient;
    }

    // ==================== GENERAL OVERVIEW ====================

    @Override
    public Object getGeneralCurrentValue(PortfolioOverviewRequest request) throws OneInchException {
        log.info("Getting general current value for {} addresses", request.getAddresses().size());
        try {
            return getGeneralCurrentValueRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("General current value request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getGeneralCurrentValueAsync(PortfolioOverviewRequest request) {
        log.info("Getting general current value (async) for {} addresses", request.getAddresses().size());
        return getGeneralCurrentValueRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getGeneralCurrentValueRx(PortfolioOverviewRequest request) {
        log.info("Getting general current value (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getGeneralCurrentValue(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("General current value request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public Object getGeneralProfitAndLoss(PortfolioOverviewRequest request) throws OneInchException {
        log.info("Getting general profit and loss for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        try {
            return getGeneralProfitAndLossRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("General profit and loss request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getGeneralProfitAndLossAsync(PortfolioOverviewRequest request) {
        log.info("Getting general profit and loss (async) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return getGeneralProfitAndLossRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getGeneralProfitAndLossRx(PortfolioOverviewRequest request) {
        log.info("Getting general profit and loss (reactive) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return portfolioApiClient.getGeneralProfitAndLoss(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("General profit and loss request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public PortfolioValueChartResponse getGeneralValueChart(PortfolioValueChartRequest request) throws OneInchException {
        log.info("Getting general value chart for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        try {
            return getGeneralValueChartRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("General value chart request failed", e);
        }
    }

    @Override
    public CompletableFuture<PortfolioValueChartResponse> getGeneralValueChartAsync(PortfolioValueChartRequest request) {
        log.info("Getting general value chart (async) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return getGeneralValueChartRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<PortfolioValueChartResponse> getGeneralValueChartRx(PortfolioValueChartRequest request) {
        log.info("Getting general value chart (reactive) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return portfolioApiClient.getGeneralValueChart(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("General value chart request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    // ==================== PROTOCOLS OVERVIEW ====================

    @Override
    public Object getProtocolsCurrentValue(PortfolioOverviewRequest request) throws OneInchException {
        log.info("Getting protocols current value for {} addresses", request.getAddresses().size());
        try {
            return getProtocolsCurrentValueRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Protocols current value request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getProtocolsCurrentValueAsync(PortfolioOverviewRequest request) {
        log.info("Getting protocols current value (async) for {} addresses", request.getAddresses().size());
        return getProtocolsCurrentValueRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getProtocolsCurrentValueRx(PortfolioOverviewRequest request) {
        log.info("Getting protocols current value (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getProtocolsCurrentValue(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Protocols current value request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public Object getProtocolsProfitAndLoss(PortfolioOverviewRequest request) throws OneInchException {
        log.info("Getting protocols profit and loss for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        try {
            return getProtocolsProfitAndLossRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Protocols profit and loss request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getProtocolsProfitAndLossAsync(PortfolioOverviewRequest request) {
        log.info("Getting protocols profit and loss (async) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return getProtocolsProfitAndLossRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getProtocolsProfitAndLossRx(PortfolioOverviewRequest request) {
        log.info("Getting protocols profit and loss (reactive) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return portfolioApiClient.getProtocolsProfitAndLoss(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Protocols profit and loss request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public PortfolioProtocolsResponse getProtocolsDetails(PortfolioDetailsRequest request) throws OneInchException {
        log.info("Getting protocols details for {} addresses", request.getAddresses().size());
        try {
            return getProtocolsDetailsRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Protocols details request failed", e);
        }
    }

    @Override
    public CompletableFuture<PortfolioProtocolsResponse> getProtocolsDetailsAsync(PortfolioDetailsRequest request) {
        log.info("Getting protocols details (async) for {} addresses", request.getAddresses().size());
        return getProtocolsDetailsRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<PortfolioProtocolsResponse> getProtocolsDetailsRx(PortfolioDetailsRequest request) {
        log.info("Getting protocols details (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getProtocolsDetails(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getClosed(),
                        request.getClosedThreshold(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Protocols details request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    // ==================== ERC20 TOKENS OVERVIEW ====================

    @Override
    public Object getTokensCurrentValue(PortfolioOverviewRequest request) throws OneInchException {
        log.info("Getting tokens current value for {} addresses", request.getAddresses().size());
        try {
            return getTokensCurrentValueRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Tokens current value request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getTokensCurrentValueAsync(PortfolioOverviewRequest request) {
        log.info("Getting tokens current value (async) for {} addresses", request.getAddresses().size());
        return getTokensCurrentValueRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getTokensCurrentValueRx(PortfolioOverviewRequest request) {
        log.info("Getting tokens current value (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getTokensCurrentValue(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Tokens current value request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public Object getTokensProfitAndLoss(PortfolioOverviewRequest request) throws OneInchException {
        log.info("Getting tokens profit and loss for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        try {
            return getTokensProfitAndLossRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Tokens profit and loss request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getTokensProfitAndLossAsync(PortfolioOverviewRequest request) {
        log.info("Getting tokens profit and loss (async) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return getTokensProfitAndLossRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getTokensProfitAndLossRx(PortfolioOverviewRequest request) {
        log.info("Getting tokens profit and loss (reactive) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return portfolioApiClient.getTokensProfitAndLoss(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Tokens profit and loss request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public PortfolioTokensResponse getTokensDetails(PortfolioDetailsRequest request) throws OneInchException {
        log.info("Getting tokens details for {} addresses", request.getAddresses().size());
        try {
            return getTokensDetailsRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Tokens details request failed", e);
        }
    }

    @Override
    public CompletableFuture<PortfolioTokensResponse> getTokensDetailsAsync(PortfolioDetailsRequest request) {
        log.info("Getting tokens details (async) for {} addresses", request.getAddresses().size());
        return getTokensDetailsRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<PortfolioTokensResponse> getTokensDetailsRx(PortfolioDetailsRequest request) {
        log.info("Getting tokens details (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getTokensDetails(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        request.getClosed(),
                        request.getClosedThreshold(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Tokens details request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    // ==================== GENERAL INFORMATION ====================

    @Override
    public Object getServiceAvailability() throws OneInchException {
        log.info("Checking Portfolio service availability");
        try {
            return getServiceAvailabilityRx().blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Service availability check failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getServiceAvailabilityAsync() {
        log.info("Checking Portfolio service availability (async)");
        return getServiceAvailabilityRx().toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getServiceAvailabilityRx() {
        log.info("Checking Portfolio service availability (reactive)");
        return portfolioApiClient.getServiceAvailability()
                .doOnError(throwable -> log.error("Service availability check failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public List<Object> getSupportedChains() throws OneInchException {
        log.info("Getting supported chains");
        try {
            return getSupportedChainsRx().blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Get supported chains request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<Object>> getSupportedChainsAsync() {
        log.info("Getting supported chains (async)");
        return getSupportedChainsRx().toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<Object>> getSupportedChainsRx() {
        log.info("Getting supported chains (reactive)");
        return portfolioApiClient.getSupportedChains()
                .doOnError(throwable -> log.error("Get supported chains request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }

    @Override
    public List<Object> getSupportedProtocols() throws OneInchException {
        log.info("Getting supported protocols");
        try {
            return getSupportedProtocolsRx().blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Get supported protocols request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<Object>> getSupportedProtocolsAsync() {
        log.info("Getting supported protocols (async)");
        return getSupportedProtocolsRx().toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<Object>> getSupportedProtocolsRx() {
        log.info("Getting supported protocols (reactive)");
        return portfolioApiClient.getSupportedProtocols()
                .doOnError(throwable -> log.error("Get supported protocols request failed", throwable))
                .onErrorResumeNext(throwable -> Single.error(OneInchErrorHandler.handleError(throwable)));
    }
}