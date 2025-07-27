package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

/**
 * Retrofit interface for 1inch Portfolio API v5.0
 * Provides portfolio management, analytics, and tracking functionality
 */
public interface PortfolioApiClient {

    // ==================== GENERAL ENDPOINTS ====================

    /**
     * Get the current status of the Portfolio API service.
     * Returns health and availability information for monitoring.
     *
     * @return Single containing API status response with service health information
     */
    @GET("portfolio/v5.0/general/status")
    Single<ResponseEnvelope<ApiStatusResponse>> getServiceStatus();

    /**
     * Validate and check multiple wallet addresses for correctness.
     * Returns validation results for each provided address.
     *
     * @param addresses list of wallet addresses to validate
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param useCache whether to use cached results for performance (optional)
     * @return Single containing address validation results
     */
    @GET("portfolio/v5.0/general/address_check")
    Single<Object> checkAddresses(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("use_cache") Boolean useCache
    );

    /**
     * Get list of blockchain networks supported by the Portfolio API.
     * Returns information about available chains for portfolio tracking.
     *
     * @return Single containing list of supported blockchain networks
     */
    @GET("portfolio/v5.0/general/supported_chains")
    Single<List<SupportedChainResponse>> getSupportedChains();

    /**
     * Get list of DeFi protocols supported for portfolio tracking.
     * Returns information about available protocols and their adapters.
     *
     * @return Single containing list of supported protocol groups
     */
    @GET("portfolio/v5.0/general/supported_protocols")
    Single<ResponseEnvelope<List<SupportedProtocolGroupResponse>>> getSupportedProtocols();

    /**
     * Get current total value of portfolio for specified addresses.
     * Returns the current USD value of all assets across supported protocols.
     *
     * @param addresses list of wallet addresses to analyze
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param useCache whether to use cached results for performance (optional)
     * @return Single containing current portfolio value information
     */
    @GET("portfolio/v5.0/general/current_value")
    Single<ResponseEnvelope<CurrentValueResponse>> getCurrentValue(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("use_cache") Boolean useCache
    );

    /**
     * Get historical portfolio value chart data over time.
     * Returns time series data for portfolio value visualization.
     *
     * @param addresses list of wallet addresses to analyze
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param timerange time period for chart data (e.g., "1d", "7d", "30d")
     * @param useCache whether to use cached results for performance (optional)
     * @return Single containing chart data for portfolio value over time
     */
    @GET("portfolio/v5.0/general/chart")
    Single<Object> getValueChart(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );

    /**
     * Generate comprehensive portfolio report with detailed analytics.
     * Returns detailed breakdown of portfolio performance and holdings.
     *
     * @param addresses list of wallet addresses to analyze
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param timerange time period for report analysis (e.g., "1d", "7d", "30d")
     * @param closed whether to include closed positions in the report (optional)
     * @param closedThreshold threshold value for considering positions as closed (optional)
     * @return Single containing comprehensive portfolio report
     */
    @GET("portfolio/v5.0/general/report")
    Single<Object> getReport(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("closed") Boolean closed,
            @Query("closed_threshold") Double closedThreshold
    );

    // ==================== PROTOCOLS ENDPOINTS ====================

    /**
     * Get current snapshot of protocol positions and balances.
     * Returns detailed breakdown of holdings across DeFi protocols.
     *
     * @param addresses list of wallet addresses to analyze
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param timestamp specific timestamp for historical snapshot (optional)
     * @param useCache whether to use cached results for performance (optional)
     * @return Single containing protocol positions snapshot
     */
    @GET("portfolio/v5.0/protocols/snapshot")
    Single<ResponseEnvelope<List<AdapterResult>>> getProtocolsSnapshot(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timestamp") Long timestamp,
            @Query("use_cache") Boolean useCache
    );

    /**
     * Get historical metrics and performance data for protocol positions.
     * Returns time series data for protocol-specific portfolio analysis.
     *
     * @param addresses list of wallet addresses to analyze
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param protocolGroupId specific protocol group to filter by (optional)
     * @param contractAddress specific contract address to filter by (optional)
     * @param tokenId specific token ID for NFT protocols (optional)
     * @param useCache whether to use cached results for performance (optional)
     * @return Single containing historical protocol metrics
     */
    @GET("portfolio/v5.0/protocols/metrics")
    Single<ResponseEnvelope<List<HistoryMetrics>>> getProtocolsMetrics(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("protocol_group_id") String protocolGroupId,
            @Query("contract_address") String contractAddress,
            @Query("token_id") Integer tokenId,
            @Query("use_cache") Boolean useCache
    );

    // ==================== TOKENS ENDPOINTS ====================

    /**
     * Get current snapshot of token holdings and balances.
     * Returns detailed breakdown of all token positions.
     *
     * @param addresses list of wallet addresses to analyze
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param timestamp specific timestamp for historical snapshot (optional)
     * @param useCache whether to use cached results for performance (optional)
     * @return Single containing token holdings snapshot
     */
    @GET("portfolio/v5.0/tokens/snapshot")
    Single<List<AdapterResult>> getTokensSnapshot(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timestamp") Long timestamp,
            @Query("use_cache") Boolean useCache
    );

    /**
     * Get historical metrics and performance data for token holdings.
     * Returns time series data for token-specific portfolio analysis.
     *
     * @param addresses list of wallet addresses to analyze
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param timerange time period for metrics calculation (e.g., "1d", "7d", "30d")
     * @param useCache whether to use cached results for performance (optional)
     * @return Single containing historical token metrics
     */
    @GET("portfolio/v5.0/tokens/metrics")
    Single<ResponseEnvelope<List<HistoryMetrics>>> getTokensMetrics(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );
}