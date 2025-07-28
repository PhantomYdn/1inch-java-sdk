package io.oneinch.sdk.client;

import io.oneinch.sdk.model.price.CurrenciesResponse;
import io.oneinch.sdk.model.price.PostPriceRequest;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.math.BigInteger;
import java.util.Map;

/**
 * Retrofit interface for 1inch Price API v1.1
 * Provides real-time token pricing across multiple currencies and chains
 */
public interface OneInchPriceApiService {

    /**
     * Get prices for all whitelisted tokens on a specific chain.
     * Returns prices in specified currency or native Wei if no currency provided.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param currency currency code for price conversion (optional, defaults to Wei)
     * @return map of token addresses to their prices
     */
    @GET("price/v1.1/{chainId}")
    Single<Map<String, BigInteger>> getWhitelistPrices(
            @Path("chainId") Integer chainId,
            @Query("currency") String currency
    );

    /**
     * Get prices for specific token addresses via GET request.
     * Multiple addresses can be passed separated by comma.
     *
     * @param chainId blockchain network identifier
     * @param addresses comma-separated token addresses
     * @param currency currency code for price conversion (optional)
     * @return map of token addresses to their prices
     */
    @GET("price/v1.1/{chainId}/{addresses}")
    Single<Map<String, BigInteger>> getPricesByAddresses(
            @Path("chainId") Integer chainId,
            @Path("addresses") String addresses,
            @Query("currency") String currency
    );

    /**
     * Get prices for requested tokens via POST request.
     * Allows sending token list in request body for larger lists.
     *
     * @param chainId blockchain network identifier
     * @param request request body containing token addresses and currency
     * @return map of token addresses to their prices
     */
    @POST("price/v1.1/{chainId}")
    Single<Map<String, BigInteger>> getPricesByPost(
            @Path("chainId") Integer chainId,
            @Body PostPriceRequest request
    );

    /**
     * Get list of supported currencies for price conversion.
     *
     * @param chainId blockchain network identifier
     * @return response containing list of supported currency codes
     */
    @GET("price/v1.1/{chainId}/currencies")
    Single<CurrenciesResponse> getSupportedCurrencies(
            @Path("chainId") Integer chainId
    );
}