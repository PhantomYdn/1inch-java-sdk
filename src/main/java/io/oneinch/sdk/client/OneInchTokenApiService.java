package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface OneInchTokenApiService {
    
    // Multi-chain token operations
    @GET("token/v1.2/multi-chain")
    Single<List<ProviderTokenDto>> getMultiChainTokens(
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    @GET("token/v1.2/multi-chain/token-list")
    Single<TokenListResponse> getMultiChainTokenList(
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    // Chain-specific token operations
    @GET("token/v1.2/{chainId}")
    Single<Map<String, ProviderTokenDto>> getTokens(
            @Path("chainId") Integer chainId,
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    @GET("token/v1.2/{chainId}/token-list")
    Single<TokenListResponse> getTokenList(
            @Path("chainId") Integer chainId,
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    // Token search operations
    @GET("token/v1.2/search")
    Single<List<Token>> searchMultiChainTokens(
            @Query("query") String query,
            @Query("ignore_listed") Boolean ignoreListed,
            @Query("only_positive_rating") Boolean onlyPositiveRating,
            @Query("limit") Integer limit
    );
    
    @GET("token/v1.2/{chainId}/search")
    Single<List<Token>> searchTokens(
            @Path("chainId") Integer chainId,
            @Query("query") String query,
            @Query("ignore_listed") Boolean ignoreListed,
            @Query("only_positive_rating") Boolean onlyPositiveRating,
            @Query("limit") Integer limit
    );
    
    // Custom token operations
    @GET("token/v1.2/{chainId}/custom")
    Single<Map<String, Token>> getCustomTokens(
            @Path("chainId") Integer chainId,
            @Query("addresses") List<String> addresses
    );
    
    @GET("token/v1.2/{chainId}/custom/{address}")
    Single<Token> getCustomToken(
            @Path("chainId") Integer chainId,
            @Path("address") String address
    );
}