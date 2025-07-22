package io.oneinch.sdk.client;

import io.oneinch.sdk.model.TokenDetailsResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
}