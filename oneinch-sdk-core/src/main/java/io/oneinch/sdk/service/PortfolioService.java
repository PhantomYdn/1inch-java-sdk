package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for Portfolio API v5 operations.
 * 
 * The Portfolio API v5 provides comprehensive portfolio analysis including:
 * - Current value breakdown by address, category, protocol group, and chain
 * - Historical value charts with time-series data
 * - Protocol snapshots with underlying tokens and rewards
 * - Protocol metrics with P&amp;L, ROI, and APR calculations
 * - Token snapshots and metrics
 * - Service information and supported chains/protocols
 */
public interface PortfolioService {

    // ==================== GENERAL ENDPOINTS ====================

    /**
     * Check if portfolio service is available
     * @return Service availability status
     */
    ApiStatusResponse getServiceStatus() throws OneInchException;

    /**
     * Get service status (async)
     */
    CompletableFuture<ApiStatusResponse> getServiceStatusAsync();

    /**
     * Get service status (reactive)
     */
    Single<ApiStatusResponse> getServiceStatusRx();

    /**
     * Check addresses for compliance (blacklist, limits, etc.)
     * @param request Request parameters including addresses and optional filters
     * @return Address compliance check results
     */
    Object checkAddresses(PortfolioV5OverviewRequest request) throws OneInchException;

    /**
     * Check addresses (async)
     */
    CompletableFuture<Object> checkAddressesAsync(PortfolioV5OverviewRequest request);

    /**
     * Check addresses (reactive)
     */
    Single<Object> checkAddressesRx(PortfolioV5OverviewRequest request);

    /**
     * Get list of supported blockchain networks
     * @return List of supported chains with their information
     */
    List<SupportedChainResponse> getSupportedChains() throws OneInchException;

    /**
     * Get supported chains (async)
     */
    CompletableFuture<List<SupportedChainResponse>> getSupportedChainsAsync();

    /**
     * Get supported chains (reactive)
     */
    Single<List<SupportedChainResponse>> getSupportedChainsRx();

    /**
     * Get list of supported DeFi protocol groups
     * @return List of supported protocol groups
     */
    List<SupportedProtocolGroupResponse> getSupportedProtocols() throws OneInchException;

    /**
     * Get supported protocols (async)
     */
    CompletableFuture<List<SupportedProtocolGroupResponse>> getSupportedProtocolsAsync();

    /**
     * Get supported protocols (reactive)
     */
    Single<List<SupportedProtocolGroupResponse>> getSupportedProtocolsRx();

    /**
     * Get current portfolio value breakdown
     * @param request Request parameters including addresses and optional filters
     * @return Current value breakdown by address, category, protocol group, and chain
     */
    CurrentValueResponse getCurrentValue(PortfolioV5OverviewRequest request) throws OneInchException;

    /**
     * Get current value (async)
     */
    CompletableFuture<CurrentValueResponse> getCurrentValueAsync(PortfolioV5OverviewRequest request);

    /**
     * Get current value (reactive)
     */
    Single<CurrentValueResponse> getCurrentValueRx(PortfolioV5OverviewRequest request);

    /**
     * Get historical value chart for portfolio
     * @param request Request parameters including addresses, timerange, and optional filters
     * @return Historical value chart data
     */
    Object getValueChart(PortfolioV5ChartRequest request) throws OneInchException;

    /**
     * Get value chart (async)
     */
    CompletableFuture<Object> getValueChartAsync(PortfolioV5ChartRequest request);

    /**
     * Get value chart (reactive)
     */
    Single<Object> getValueChartRx(PortfolioV5ChartRequest request);

    /**
     * Get CSV report on tokens and protocols overview details
     * @param request Request parameters including addresses, timerange, and optional filters
     * @return CSV report data
     */
    Object getReport(PortfolioV5ChartRequest request) throws OneInchException;

    /**
     * Get report (async)
     */
    CompletableFuture<Object> getReportAsync(PortfolioV5ChartRequest request);

    /**
     * Get report (reactive)
     */
    Single<Object> getReportRx(PortfolioV5ChartRequest request);

    // ==================== PROTOCOLS ENDPOINTS ====================

    /**
     * Get protocols snapshot with underlying tokens, rewards, fees, etc.
     * @param request Request parameters including addresses and optional timestamp
     * @return Protocol snapshot data
     */
    List<AdapterResult> getProtocolsSnapshot(PortfolioV5SnapshotRequest request) throws OneInchException;

    /**
     * Get protocols snapshot (async)
     */
    CompletableFuture<List<AdapterResult>> getProtocolsSnapshotAsync(PortfolioV5SnapshotRequest request);

    /**
     * Get protocols snapshot (reactive)
     */
    Single<List<AdapterResult>> getProtocolsSnapshotRx(PortfolioV5SnapshotRequest request);

    /**
     * Get protocols metrics with P&amp;L, ROI, APR calculations
     * @param request Request parameters including addresses and optional filters
     * @return Protocol metrics data
     */
    List<HistoryMetrics> getProtocolsMetrics(PortfolioV5MetricsRequest request) throws OneInchException;

    /**
     * Get protocols metrics (async)
     */
    CompletableFuture<List<HistoryMetrics>> getProtocolsMetricsAsync(PortfolioV5MetricsRequest request);

    /**
     * Get protocols metrics (reactive)
     */
    Single<List<HistoryMetrics>> getProtocolsMetricsRx(PortfolioV5MetricsRequest request);

    // ==================== TOKENS ENDPOINTS ====================

    /**
     * Get tokens snapshot
     * @param request Request parameters including addresses and optional timestamp
     * @return Token snapshot data
     */
    List<AdapterResult> getTokensSnapshot(PortfolioV5SnapshotRequest request) throws OneInchException;

    /**
     * Get tokens snapshot (async)
     */
    CompletableFuture<List<AdapterResult>> getTokensSnapshotAsync(PortfolioV5SnapshotRequest request);

    /**
     * Get tokens snapshot (reactive)
     */
    Single<List<AdapterResult>> getTokensSnapshotRx(PortfolioV5SnapshotRequest request);

    /**
     * Get tokens metrics with P&amp;L, ROI calculations
     * @param request Request parameters including addresses, timerange, and optional filters
     * @return Token metrics data
     */
    List<HistoryMetrics> getTokensMetrics(PortfolioV5MetricsRequest request) throws OneInchException;

    /**
     * Get tokens metrics (async)
     */
    CompletableFuture<List<HistoryMetrics>> getTokensMetricsAsync(PortfolioV5MetricsRequest request);

    /**
     * Get tokens metrics (reactive)
     */
    Single<List<HistoryMetrics>> getTokensMetricsRx(PortfolioV5MetricsRequest request);
}