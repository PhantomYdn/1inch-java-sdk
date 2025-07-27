package io.oneinch.sdk.client;

import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.token.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

/**
 * Retrofit interface for 1inch Token API v1.3
 * Provides token information, search, and metadata functionality
 */
public interface OneInchTokenApiService {
    
    /**
     * Get token information across all supported blockchain networks.
     * Returns a list of tokens available from the specified provider.
     *
     * @param provider token list provider (e.g., "1inch", "trust", "coingecko")
     * @param country country code for regulatory filtering (optional)
     * @return Single containing list of provider token DTOs from all chains
     */
    @GET("token/v1.3/multi-chain")
    Single<List<ProviderTokenDto>> getMultiChainTokens(
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    /**
     * Get token list in standard format across all supported blockchain networks.
     * Returns tokens in the standard token list schema format.
     *
     * @param provider token list provider (e.g., "1inch", "trust", "coingecko")
     * @param country country code for regulatory filtering (optional)
     * @return Single containing multi-chain token list response
     */
    @GET("token/v1.3/multi-chain/token-list")
    Single<TokenListResponse> getMultiChainTokenList(
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    /**
     * Get token information for a specific blockchain network.
     * Returns a map of token addresses to token information.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param provider token list provider (e.g., "1inch", "trust", "coingecko")
     * @param country country code for regulatory filtering (optional)
     * @return Single containing map of token address to provider token DTO
     */
    @GET("token/v1.3/{chainId}")
    Single<Map<String, ProviderTokenDto>> getTokens(
            @Path("chainId") Integer chainId,
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    /**
     * Get token list in standard format for a specific blockchain network.
     * Returns tokens in the standard token list schema format.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param provider token list provider (e.g., "1inch", "trust", "coingecko")
     * @param country country code for regulatory filtering (optional)
     * @return Single containing chain-specific token list response
     */
    @GET("token/v1.3/{chainId}/token-list")
    Single<TokenListResponse> getTokenList(
            @Path("chainId") Integer chainId,
            @Query("provider") String provider,
            @Query("country") String country
    );
    
    /**
     * Search for tokens by name or symbol across all supported blockchain networks.
     * Returns matching tokens from multiple chains.
     *
     * @param query search term (token name or symbol)
     * @param ignoreListed whether to ignore tokens in standard lists (optional)
     * @param onlyPositiveRating whether to return only positively rated tokens (optional)
     * @param limit maximum number of results to return (optional)
     * @return Single containing list of matching tokens from all chains
     */
    @GET("token/v1.3/search")
    Single<List<Token>> searchMultiChainTokens(
            @Query("query") String query,
            @Query("ignore_listed") Boolean ignoreListed,
            @Query("only_positive_rating") Boolean onlyPositiveRating,
            @Query("limit") Integer limit
    );
    
    /**
     * Search for tokens by name or symbol on a specific blockchain network.
     * Returns matching tokens from the specified chain only.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param query search term (token name or symbol)
     * @param ignoreListed whether to ignore tokens in standard lists (optional)
     * @param onlyPositiveRating whether to return only positively rated tokens (optional)
     * @param limit maximum number of results to return (optional)
     * @return Single containing list of matching tokens from the specified chain
     */
    @GET("token/v1.3/{chainId}/search")
    Single<List<Token>> searchTokens(
            @Path("chainId") Integer chainId,
            @Query("query") String query,
            @Query("ignore_listed") Boolean ignoreListed,
            @Query("only_positive_rating") Boolean onlyPositiveRating,
            @Query("limit") Integer limit
    );
    
    /**
     * Get detailed information for multiple custom token addresses.
     * Returns token metadata for the specified contract addresses.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param addresses list of token contract addresses to look up
     * @return Single containing map of token address to token information
     */
    @GET("token/v1.3/{chainId}/custom")
    Single<Map<String, Token>> getCustomTokens(
            @Path("chainId") Integer chainId,
            @Query("addresses") List<String> addresses
    );
    
    /**
     * Get detailed information for a single custom token address.
     * Returns token metadata for the specified contract address.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param address token contract address to look up
     * @return Single containing detailed token information
     */
    @GET("token/v1.3/{chainId}/custom/{address}")
    Single<Token> getCustomToken(
            @Path("chainId") Integer chainId,
            @Path("address") String address
    );
}