package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;

public interface OneInchTokenDetailsApiService {
    
    // Token details operations
    @GET("token-details/v1.0/details/{chain}")
    Single<TokenDetailsResponse> getNativeTokenDetails(
            @Path("chain") Integer chainId,
            @Query("provider") String provider
    );
    
    @GET("token-details/v1.0/details/{chain}/{contractAddress}")
    Single<TokenDetailsResponse> getTokenDetails(
            @Path("chain") Integer chainId,
            @Path("contractAddress") String contractAddress,
            @Query("provider") String provider
    );
    
    // Chart operations - Range based
    @GET("token-details/v1.0/charts/range/{chain}")
    Single<ChartDataResponse> getNativeTokenChartByRange(
            @Path("chain") Integer chainId,
            @Query("from") Long from,
            @Query("to") Long to,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    @GET("token-details/v1.0/charts/range/{chain}/{tokenAddress}")
    Single<ChartDataResponse> getTokenChartByRange(
            @Path("chain") Integer chainId,
            @Path("tokenAddress") String tokenAddress,
            @Query("from") Long from,
            @Query("to") Long to,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    // Chart operations - Interval based
    @GET("token-details/v1.0/charts/interval/{chain}")
    Single<ChartDataResponse> getNativeTokenChartByInterval(
            @Path("chain") Integer chainId,
            @Query("interval") String interval,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    @GET("token-details/v1.0/charts/interval/{chain}/{tokenAddress}")
    Single<ChartDataResponse> getTokenChartByInterval(
            @Path("chain") Integer chainId,
            @Path("tokenAddress") String tokenAddress,
            @Query("interval") String interval,
            @Query("provider") String provider,
            @Query("from_time") Long fromTime
    );
    
    // Price change operations
    @GET("token-details/v1.0/prices/change/{chain}")
    Single<TokenPriceChangeResponse> getNativeTokenPriceChange(
            @Path("chain") Integer chainId,
            @Query("interval") String interval
    );
    
    @GET("token-details/v1.0/prices/change/{chain}/{tokenAddress}")
    Single<TokenPriceChangeResponse> getTokenPriceChange(
            @Path("chain") Integer chainId,
            @Path("tokenAddress") String tokenAddress,
            @Query("interval") String interval
    );
    
    @POST("token-details/v1.0/prices/change/{chain}")
    Single<List<TokenListPriceChangeResponse>> getTokenListPriceChange(
            @Path("chain") Integer chainId,
            @Body TokenListPriceChangeRequest request
    );
}