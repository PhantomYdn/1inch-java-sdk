package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for Portfolio API operations.
 * 
 * The Portfolio API provides comprehensive portfolio analysis including:
 * - Current value analysis for protocols and tokens
 * - Profit and Loss (P&amp;L) and ROI calculations
 * - Protocol-specific metrics and details
 * - ERC20 token breakdowns
 * - Historical value charts
 * - Multi-chain and multi-address support
 */
public interface PortfolioService {

    // ==================== GENERAL OVERVIEW ====================

    /**
     * Get general current value for all supported protocols and tokens
     * @param request Request parameters including addresses and optional filters
     * @return Current value data grouped by chains and addresses
     */
    Object getGeneralCurrentValue(PortfolioOverviewRequest request) throws OneInchException;

    /**
     * Get general current value (async)
     */
    CompletableFuture<Object> getGeneralCurrentValueAsync(PortfolioOverviewRequest request);

    /**
     * Get general current value (reactive)
     */
    Single<Object> getGeneralCurrentValueRx(PortfolioOverviewRequest request);

    /**
     * Get general profit and loss across all supported protocols and tokens
     * @param request Request parameters including addresses, timerange, and optional filters
     * @return P&amp;L and ROI data organized by chains
     */
    Object getGeneralProfitAndLoss(PortfolioOverviewRequest request) throws OneInchException;

    /**
     * Get general profit and loss (async)
     */
    CompletableFuture<Object> getGeneralProfitAndLossAsync(PortfolioOverviewRequest request);

    /**
     * Get general profit and loss (reactive)
     */
    Single<Object> getGeneralProfitAndLossRx(PortfolioOverviewRequest request);

    /**
     * Get general value chart for all supported protocols and tokens
     * @param request Request parameters including addresses, timerange, and optional filters
     * @return Historical value chart data
     */
    PortfolioValueChartResponse getGeneralValueChart(PortfolioValueChartRequest request) throws OneInchException;

    /**
     * Get general value chart (async)
     */
    CompletableFuture<PortfolioValueChartResponse> getGeneralValueChartAsync(PortfolioValueChartRequest request);

    /**
     * Get general value chart (reactive)
     */
    Single<PortfolioValueChartResponse> getGeneralValueChartRx(PortfolioValueChartRequest request);

    // ==================== PROTOCOLS OVERVIEW ====================

    /**
     * Get current value for supported protocols
     * @param request Request parameters including addresses and optional filters
     * @return Current value data grouped by chains and addresses
     */
    Object getProtocolsCurrentValue(PortfolioOverviewRequest request) throws OneInchException;

    /**
     * Get protocols current value (async)
     */
    CompletableFuture<Object> getProtocolsCurrentValueAsync(PortfolioOverviewRequest request);

    /**
     * Get protocols current value (reactive)
     */
    Single<Object> getProtocolsCurrentValueRx(PortfolioOverviewRequest request);

    /**
     * Get protocols profit and loss
     * @param request Request parameters including addresses, timerange, and optional filters
     * @return P&amp;L and ROI data organized by chains
     */
    Object getProtocolsProfitAndLoss(PortfolioOverviewRequest request) throws OneInchException;

    /**
     * Get protocols profit and loss (async)
     */
    CompletableFuture<Object> getProtocolsProfitAndLossAsync(PortfolioOverviewRequest request);

    /**
     * Get protocols profit and loss (reactive)
     */
    Single<Object> getProtocolsProfitAndLossRx(PortfolioOverviewRequest request);

    /**
     * Get detailed protocol information including statistics and metrics
     * @param request Request parameters including addresses and optional filters
     * @return Detailed protocol statistics and metrics
     */
    PortfolioProtocolsResponse getProtocolsDetails(PortfolioDetailsRequest request) throws OneInchException;

    /**
     * Get protocols details (async)
     */
    CompletableFuture<PortfolioProtocolsResponse> getProtocolsDetailsAsync(PortfolioDetailsRequest request);

    /**
     * Get protocols details (reactive)
     */
    Single<PortfolioProtocolsResponse> getProtocolsDetailsRx(PortfolioDetailsRequest request);

    // ==================== ERC20 TOKENS OVERVIEW ====================

    /**
     * Get current value for supported ERC20 tokens
     * @param request Request parameters including addresses and optional filters
     * @return Current value data grouped by chains and addresses
     */
    Object getTokensCurrentValue(PortfolioOverviewRequest request) throws OneInchException;

    /**
     * Get tokens current value (async)
     */
    CompletableFuture<Object> getTokensCurrentValueAsync(PortfolioOverviewRequest request);

    /**
     * Get tokens current value (reactive)
     */
    Single<Object> getTokensCurrentValueRx(PortfolioOverviewRequest request);

    /**
     * Get ERC20 tokens profit and loss
     * @param request Request parameters including addresses, timerange, and optional filters
     * @return P&amp;L and ROI data organized by chains
     */
    Object getTokensProfitAndLoss(PortfolioOverviewRequest request) throws OneInchException;

    /**
     * Get tokens profit and loss (async)
     */
    CompletableFuture<Object> getTokensProfitAndLossAsync(PortfolioOverviewRequest request);

    /**
     * Get tokens profit and loss (reactive)
     */
    Single<Object> getTokensProfitAndLossRx(PortfolioOverviewRequest request);

    /**
     * Get detailed ERC20 token information including ROI and P&amp;L
     * @param request Request parameters including addresses and optional filters
     * @return Detailed token statistics including ROI and P&amp;L
     */
    PortfolioTokensResponse getTokensDetails(PortfolioDetailsRequest request) throws OneInchException;

    /**
     * Get tokens details (async)
     */
    CompletableFuture<PortfolioTokensResponse> getTokensDetailsAsync(PortfolioDetailsRequest request);

    /**
     * Get tokens details (reactive)
     */
    Single<PortfolioTokensResponse> getTokensDetailsRx(PortfolioDetailsRequest request);

    // ==================== GENERAL INFORMATION ====================

    /**
     * Check if the Portfolio service is available
     * @return Service availability status
     */
    Object getServiceAvailability() throws OneInchException;

    /**
     * Get service availability (async)
     */
    CompletableFuture<Object> getServiceAvailabilityAsync();

    /**
     * Get service availability (reactive)
     */
    Single<Object> getServiceAvailabilityRx();

    /**
     * Get list of supported blockchain networks
     * @return List of supported chain IDs and names
     */
    List<Object> getSupportedChains() throws OneInchException;

    /**
     * Get supported chains (async)
     */
    CompletableFuture<List<Object>> getSupportedChainsAsync();

    /**
     * Get supported chains (reactive)
     */
    Single<List<Object>> getSupportedChainsRx();

    /**
     * Get list of supported DeFi protocols
     * @return List of supported protocol names and information
     */
    List<Object> getSupportedProtocols() throws OneInchException;

    /**
     * Get supported protocols (async)
     */
    CompletableFuture<List<Object>> getSupportedProtocolsAsync();

    /**
     * Get supported protocols (reactive)
     */
    Single<List<Object>> getSupportedProtocolsRx();
}