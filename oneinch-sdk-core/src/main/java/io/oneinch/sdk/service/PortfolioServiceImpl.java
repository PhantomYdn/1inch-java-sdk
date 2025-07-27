package io.oneinch.sdk.service;

import io.oneinch.sdk.client.PortfolioApiClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.ApiStatusResponse;
import io.oneinch.sdk.model.ResponseEnvelope;
import io.oneinch.sdk.model.history.HistoryMetrics;
import io.oneinch.sdk.model.portfolio.*;
import io.oneinch.sdk.model.tokendetails.CurrentValueResponse;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of Portfolio API v5 service
 */
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioApiClient portfolioApiClient;

    public PortfolioServiceImpl(PortfolioApiClient portfolioApiClient) {
        this.portfolioApiClient = portfolioApiClient;
    }

    // ==================== GENERAL ENDPOINTS ====================

    @Override
    public ApiStatusResponse getServiceStatus() throws OneInchException {
        log.info("Getting Portfolio service status");
        try {
            return getServiceStatusRx().blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Service status request failed", e);
        }
    }

    @Override
    public CompletableFuture<ApiStatusResponse> getServiceStatusAsync() {
        log.info("Getting Portfolio service status (async)");
        return getServiceStatusRx().toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<ApiStatusResponse> getServiceStatusRx() {
        log.info("Getting Portfolio service status (reactive)");
        return portfolioApiClient.getServiceStatus()
                .map(ResponseEnvelope::getResult)
                .doOnError(throwable -> log.error("Service status request failed", throwable));
    }

    @Override
    public Object checkAddresses(PortfolioV5OverviewRequest request) throws OneInchException {
        log.info("Checking addresses compliance for {} addresses", request.getAddresses().size());
        try {
            return checkAddressesRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Address check request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> checkAddressesAsync(PortfolioV5OverviewRequest request) {
        log.info("Checking addresses compliance (async) for {} addresses", request.getAddresses().size());
        return checkAddressesRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> checkAddressesRx(PortfolioV5OverviewRequest request) {
        log.info("Checking addresses compliance (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.checkAddresses(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Address check request failed", throwable));
    }

    @Override
    public List<SupportedChainResponse> getSupportedChains() throws OneInchException {
        log.info("Getting supported chains");
        try {
            return getSupportedChainsRx().blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Get supported chains request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<SupportedChainResponse>> getSupportedChainsAsync() {
        log.info("Getting supported chains (async)");
        return getSupportedChainsRx().toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<SupportedChainResponse>> getSupportedChainsRx() {
        log.info("Getting supported chains (reactive)");
        return portfolioApiClient.getSupportedChains()
                .doOnError(throwable -> log.error("Get supported chains request failed", throwable));
    }

    @Override
    public List<SupportedProtocolGroupResponse> getSupportedProtocols() throws OneInchException {
        log.info("Getting supported protocols");
        try {
            return getSupportedProtocolsRx().blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Get supported protocols request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<SupportedProtocolGroupResponse>> getSupportedProtocolsAsync() {
        log.info("Getting supported protocols (async)");
        return getSupportedProtocolsRx().toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<SupportedProtocolGroupResponse>> getSupportedProtocolsRx() {
        log.info("Getting supported protocols (reactive)");
        return portfolioApiClient.getSupportedProtocols()
                .map(ResponseEnvelope::getResult)
                .doOnError(throwable -> log.error("Get supported protocols request failed", throwable));
    }

    @Override
    public CurrentValueResponse getCurrentValue(PortfolioV5OverviewRequest request) throws OneInchException {
        log.info("Getting current value for {} addresses", request.getAddresses().size());
        try {
            return getCurrentValueRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Current value request failed", e);
        }
    }

    @Override
    public CompletableFuture<CurrentValueResponse> getCurrentValueAsync(PortfolioV5OverviewRequest request) {
        log.info("Getting current value (async) for {} addresses", request.getAddresses().size());
        return getCurrentValueRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<CurrentValueResponse> getCurrentValueRx(PortfolioV5OverviewRequest request) {
        log.info("Getting current value (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getCurrentValue(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getUseCache())
                .map(ResponseEnvelope::getResult)
                .doOnError(throwable -> log.error("Current value request failed", throwable));
    }

    @Override
    public Object getValueChart(PortfolioV5ChartRequest request) throws OneInchException {
        log.info("Getting value chart for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        try {
            return getValueChartRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Value chart request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getValueChartAsync(PortfolioV5ChartRequest request) {
        log.info("Getting value chart (async) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return getValueChartRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getValueChartRx(PortfolioV5ChartRequest request) {
        log.info("Getting value chart (reactive) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return portfolioApiClient.getValueChart(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Value chart request failed", throwable));
    }

    @Override
    public Object getReport(PortfolioV5ChartRequest request) throws OneInchException {
        log.info("Getting report for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        try {
            return getReportRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Report request failed", e);
        }
    }

    @Override
    public CompletableFuture<Object> getReportAsync(PortfolioV5ChartRequest request) {
        log.info("Getting report (async) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return getReportRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Object> getReportRx(PortfolioV5ChartRequest request) {
        log.info("Getting report (reactive) for {} addresses with timerange: {}", 
                request.getAddresses().size(), request.getTimerange());
        return portfolioApiClient.getReport(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        true, // closed positions
                        1.0) // closed threshold
                .doOnError(throwable -> log.error("Report request failed", throwable));
    }

    // ==================== PROTOCOLS ENDPOINTS ====================

    @Override
    public List<AdapterResult> getProtocolsSnapshot(PortfolioV5SnapshotRequest request) throws OneInchException {
        log.info("Getting protocols snapshot for {} addresses", request.getAddresses().size());
        try {
            return getProtocolsSnapshotRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Protocols snapshot request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<AdapterResult>> getProtocolsSnapshotAsync(PortfolioV5SnapshotRequest request) {
        log.info("Getting protocols snapshot (async) for {} addresses", request.getAddresses().size());
        return getProtocolsSnapshotRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<AdapterResult>> getProtocolsSnapshotRx(PortfolioV5SnapshotRequest request) {
        log.info("Getting protocols snapshot (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getProtocolsSnapshot(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimestamp(),
                        request.getUseCache())
                .map(ResponseEnvelope::getResult)
                .doOnError(throwable -> log.error("Protocols snapshot request failed", throwable));
    }

    @Override
    public List<HistoryMetrics> getProtocolsMetrics(PortfolioV5MetricsRequest request) throws OneInchException {
        log.info("Getting protocols metrics for {} addresses", request.getAddresses().size());
        try {
            return getProtocolsMetricsRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Protocols metrics request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<HistoryMetrics>> getProtocolsMetricsAsync(PortfolioV5MetricsRequest request) {
        log.info("Getting protocols metrics (async) for {} addresses", request.getAddresses().size());
        return getProtocolsMetricsRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<HistoryMetrics>> getProtocolsMetricsRx(PortfolioV5MetricsRequest request) {
        log.info("Getting protocols metrics (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getProtocolsMetrics(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getProtocolGroupId(),
                        request.getContractAddress(),
                        request.getTokenId(),
                        request.getUseCache())
                .map(ResponseEnvelope::getResult)
                .doOnError(throwable -> log.error("Protocols metrics request failed", throwable));
    }

    // ==================== TOKENS ENDPOINTS ====================

    @Override
    public List<AdapterResult> getTokensSnapshot(PortfolioV5SnapshotRequest request) throws OneInchException {
        log.info("Getting tokens snapshot for {} addresses", request.getAddresses().size());
        try {
            return getTokensSnapshotRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Tokens snapshot request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<AdapterResult>> getTokensSnapshotAsync(PortfolioV5SnapshotRequest request) {
        log.info("Getting tokens snapshot (async) for {} addresses", request.getAddresses().size());
        return getTokensSnapshotRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<AdapterResult>> getTokensSnapshotRx(PortfolioV5SnapshotRequest request) {
        log.info("Getting tokens snapshot (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getTokensSnapshot(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimestamp(),
                        request.getUseCache())
                .doOnError(throwable -> log.error("Tokens snapshot request failed", throwable));
    }

    @Override
    public List<HistoryMetrics> getTokensMetrics(PortfolioV5MetricsRequest request) throws OneInchException {
        log.info("Getting tokens metrics for {} addresses", request.getAddresses().size());
        try {
            return getTokensMetricsRx(request).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Tokens metrics request failed", e);
        }
    }

    @Override
    public CompletableFuture<List<HistoryMetrics>> getTokensMetricsAsync(PortfolioV5MetricsRequest request) {
        log.info("Getting tokens metrics (async) for {} addresses", request.getAddresses().size());
        return getTokensMetricsRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<HistoryMetrics>> getTokensMetricsRx(PortfolioV5MetricsRequest request) {
        log.info("Getting tokens metrics (reactive) for {} addresses", request.getAddresses().size());
        return portfolioApiClient.getTokensMetrics(
                        request.getAddresses(),
                        request.getChainId(),
                        request.getTimerange(),
                        request.getUseCache())
                .map(ResponseEnvelope::getResult)
                .doOnError(throwable -> log.error("Tokens metrics request failed", throwable));
    }
}