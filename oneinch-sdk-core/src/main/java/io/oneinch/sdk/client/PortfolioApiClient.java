package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

/**
 * Retrofit interface for Portfolio API endpoints
 */
public interface PortfolioApiClient {

    // ==================== GENERAL OVERVIEW ====================

    @GET("portfolio/v4/general/current_value")
    Single<Object> getGeneralCurrentValue(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v4/general/profit_and_loss")
    Single<Object> getGeneralProfitAndLoss(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v4/general/value_chart")
    Single<PortfolioValueChartResponse> getGeneralValueChart(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );

    // ==================== PROTOCOLS OVERVIEW ====================

    @GET("portfolio/v4/overview/protocols/current_value")
    Single<Object> getProtocolsCurrentValue(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v4/overview/protocols/profit_and_loss")
    Single<Object> getProtocolsProfitAndLoss(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v4/overview/protocols/details")
    Single<PortfolioProtocolsResponse> getProtocolsDetails(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("closed") Boolean closed,
            @Query("closed_threshold") Double closedThreshold,
            @Query("use_cache") Boolean useCache
    );

    // ==================== ERC20 TOKENS OVERVIEW ====================

    @GET("portfolio/v4/overview/erc20/current_value")
    Single<Object> getTokensCurrentValue(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v4/overview/erc20/profit_and_loss")
    Single<Object> getTokensProfitAndLoss(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("use_cache") Boolean useCache
    );

    @GET("portfolio/v4/overview/erc20/details")
    Single<PortfolioTokensResponse> getTokensDetails(
            @Query("addresses") List<String> addresses,
            @Query("chain_id") Integer chainId,
            @Query("timerange") String timerange,
            @Query("closed") Boolean closed,
            @Query("closed_threshold") Double closedThreshold,
            @Query("use_cache") Boolean useCache
    );

    // ==================== GENERAL INFORMATION ====================

    @GET("portfolio/v4/general/is_available")
    Single<Object> getServiceAvailability();

    @GET("portfolio/v4/general/supported_chains")
    Single<List<Object>> getSupportedChains();

    @GET("portfolio/v4/general/supported_protocols")
    Single<List<Object>> getSupportedProtocols();
}