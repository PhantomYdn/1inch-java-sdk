package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

/**
 * Retrofit interface for Portfolio API v5 endpoints
 */
public interface PortfolioApiClient {

    // ==================== GENERAL ENDPOINTS ====================

    @GET("portfolio/v5.0/general/status")
    Single<ResponseEnvelope<ApiStatusResponse>> getServiceStatus();

    @GET("portfolio/v5.0/general/address_check")
    Single<Object> checkAddresses(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v5.0/general/supported_chains")
    Single<List<SupportedChainResponse>> getSupportedChains();

    @GET("portfolio/v5.0/general/supported_protocols")
    Single<ResponseEnvelope<List<SupportedProtocolGroupResponse>>> getSupportedProtocols();

    @GET("portfolio/v5.0/general/current_value")
    Single<ResponseEnvelope<CurrentValueResponse>> getCurrentValue(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v5.0/general/chart")
    Single<Object> getValueChart(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v5.0/general/report")
    Single<Object> getReport(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("closed") Boolean closed,
            @Query("closed_threshold") Double closedThreshold
    );

    // ==================== PROTOCOLS ENDPOINTS ====================

    @GET("portfolio/v5.0/protocols/snapshot")
    Single<ResponseEnvelope<List<AdapterResult>>> getProtocolsSnapshot(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timestamp") Long timestamp,
            @Query("use_cache") Boolean useCache
    );

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

    @GET("portfolio/v5.0/tokens/snapshot")
    Single<List<AdapterResult>> getTokensSnapshot(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timestamp") Long timestamp,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v5.0/tokens/metrics")
    Single<ResponseEnvelope<List<HistoryMetrics>>> getTokensMetrics(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );
}