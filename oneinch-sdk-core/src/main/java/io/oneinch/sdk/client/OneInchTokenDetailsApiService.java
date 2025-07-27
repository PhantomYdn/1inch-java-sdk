package io.oneinch.sdk.client;

import io.oneinch.sdk.model.tokendetails.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;

/**
 * Retrofit interface for 1inch Token Details API v1.0
 * Provides token pricing, chart data, and market information
 */
public interface OneInchTokenDetailsApiService {
    
    /**
     * Get detailed information for the native token of a blockchain.
     * Returns pricing, market cap, and metadata for the chain's native token (e.g., ETH for Ethereum).
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param provider price data provider (e.g., "coingecko", "coinmarketcap")
     * @return Single containing native token details with pricing and market data
     */
    @GET("token-details/v1.0/details/{chain}")
    Single<TokenDetailsResponse> getNativeTokenDetails(
            @Path("chain") Integer chainId,
            @Query("provider") String provider
    );
    
    /**
     * Get detailed information for a specific token contract.
     * Returns pricing, market cap, and metadata for the specified token.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param contractAddress token contract address to get details for
     * @param provider price data provider (e.g., "coingecko", "coinmarketcap")
     * @return Single containing token details with pricing and market data
     */
    @GET("token-details/v1.0/details/{chain}/{contractAddress}")
    Single<TokenDetailsResponse> getTokenDetails(
            @Path("chain") Integer chainId,
            @Path("contractAddress") String contractAddress,
            @Query("provider") String provider
    );
    
    /**
     * Get price chart data for the native token within a specific time range.
     * Returns historical price points for charting and analysis.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param from start timestamp for chart data (Unix timestamp)
     * @param to end timestamp for chart data (Unix timestamp)
     * @param provider price data provider (e.g., "coingecko", "coinmarketcap")
     * @param fromTime alternative start time parameter (optional)
     * @return Single containing chart data with price points over time
     */
    @GET("token-details/v1.0/charts/range/{chain}")
    Single<ChartDataResponse> getNativeTokenChartByRange(
            @Path("chain") Integer chainId,
            @Query("from") Long from,
            @Query("to") Long to,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    /**
     * Get price chart data for a specific token within a time range.
     * Returns historical price points for charting and analysis.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param tokenAddress token contract address to get chart data for
     * @param from start timestamp for chart data (Unix timestamp)
     * @param to end timestamp for chart data (Unix timestamp)
     * @param provider price data provider (e.g., "coingecko", "coinmarketcap")
     * @param fromTime alternative start time parameter (optional)
     * @return Single containing chart data with price points over time
     */
    @GET("token-details/v1.0/charts/range/{chain}/{tokenAddress}")
    Single<ChartDataResponse> getTokenChartByRange(
            @Path("chain") Integer chainId,
            @Path("tokenAddress") String tokenAddress,
            @Query("from") Long from,
            @Query("to") Long to,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    /**
     * Get price chart data for the native token using a predefined interval.
     * Returns historical price points at regular intervals (e.g., hourly, daily).
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param interval time interval for data points (e.g., "1h", "1d", "1w")
     * @param provider price data provider (e.g., "coingecko", "coinmarketcap")
     * @param fromTime start time for chart data (Unix timestamp, optional)
     * @return Single containing chart data with price points at specified intervals
     */
    @GET("token-details/v1.0/charts/interval/{chain}")
    Single<ChartDataResponse> getNativeTokenChartByInterval(
            @Path("chain") Integer chainId,
            @Query("interval") String interval,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    /**
     * Get price chart data for a specific token using a predefined interval.
     * Returns historical price points at regular intervals (e.g., hourly, daily).
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param tokenAddress token contract address to get chart data for
     * @param interval time interval for data points (e.g., "1h", "1d", "1w")
     * @param provider price data provider (e.g., "coingecko", "coinmarketcap")
     * @param fromTime start time for chart data (Unix timestamp, optional)
     * @return Single containing chart data with price points at specified intervals
     */
    @GET("token-details/v1.0/charts/interval/{chain}/{tokenAddress}")
    Single<ChartDataResponse> getTokenChartByInterval(
            @Path("chain") Integer chainId,
            @Path("tokenAddress") String tokenAddress,
            @Query("interval") String interval,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    /**
     * Get price change information for the native token over a specified period.
     * Returns percentage change and absolute change values.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param interval time period for price change calculation (e.g., "1h", "24h", "7d")
     * @return Single containing price change data for the native token
     */
    @GET("token-details/v1.0/prices/change/{chain}")
    Single<TokenPriceChangeResponse> getNativeTokenPriceChange(
            @Path("chain") Integer chainId,
            @Query("interval") String interval
    );
    
    /**
     * Get price change information for a specific token over a specified period.
     * Returns percentage change and absolute change values.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param tokenAddress token contract address to get price change for
     * @param interval time period for price change calculation (e.g., "1h", "24h", "7d")
     * @return Single containing price change data for the token
     */
    @GET("token-details/v1.0/prices/change/{chain}/{tokenAddress}")
    Single<TokenPriceChangeResponse> getTokenPriceChange(
            @Path("chain") Integer chainId,
            @Path("tokenAddress") String tokenAddress,
            @Query("interval") String interval
    );
    
    /**
     * Get price change information for multiple tokens in a single request.
     * Returns price change data for a list of specified token addresses.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param request request body containing list of token addresses and time interval
     * @return Single containing list of price change responses for each token
     */
    @POST("token-details/v1.0/prices/change/{chain}")
    Single<List<TokenListPriceChangeResponse>> getTokenListPriceChange(
            @Path("chain") Integer chainId,
            @Body TokenListPriceChangeRequest request
    );
}