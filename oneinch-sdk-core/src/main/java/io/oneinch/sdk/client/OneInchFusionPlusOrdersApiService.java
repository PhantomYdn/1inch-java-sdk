package io.oneinch.sdk.client;

import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

/**
 * Retrofit API service interface for 1inch FusionPlus Orders API.
 * Handles cross-chain order lifecycle management including active orders, order status, and events.
 */
public interface OneInchFusionPlusOrdersApiService {
    
    /**
     * Get cross-chain gasless swap active orders.
     *
     * @param srcChain Source chain ID for filtering
     * @param dstChain Destination chain ID for filtering
     * @param page Pagination step, default: 1 (page = offset / limit)
     * @param limit Number of active orders to receive (default: 100, max: 500)
     * @param sortBy Sort field for ordering results
     * @return Single containing paginated active cross-chain orders
     */
    @GET("cross-chain/v1.0/order/active")
    Single<GetActiveOrdersOutput> getActiveOrders(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("sortBy") String sortBy
    );
    
    /**
     * Get cross-chain order status by order hash.
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to query
     * @return Single containing cross-chain order status and details
     */
    @GET("cross-chain/v1.0/order/{orderHash}")
    Single<CrossChainOrderDto> getOrderByOrderHash(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Path("orderHash") String orderHash
    );
    
    /**
     * Get cross-chain orders by multiple hashes (batch request).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHashes Comma-separated list of order hashes
     * @return Single containing order status details for all requested hashes
     */
    @GET("cross-chain/v1.0/order/batch")
    Single<GetActiveOrdersOutput> getOrdersByOrderHashes(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Query("orderHashes") String orderHashes
    );
    
    /**
     * Get cross-chain orders by maker address.
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param address Maker's address
     * @param page Pagination step, default: 1
     * @param limit Number of orders to receive (default: 100, max: 500)
     * @return Single containing cross-chain order history for the maker
     */
    @GET("cross-chain/v1.0/order/maker/{address}")
    Single<GetActiveOrdersOutput> getOrdersByMaker(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Path("address") String address,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );
    
    /**
     * Get escrow events for cross-chain orders.
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get events for
     * @param page Pagination step
     * @param limit Number of events to receive
     * @return Single containing list of escrow events
     */
    @GET("cross-chain/v1.0/order/{orderHash}/events")
    Single<GetActiveOrdersOutput> getEscrowEvents(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Path("orderHash") String orderHash,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );
    
    /**
     * Get public actions available for cross-chain orders.
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get public actions for
     * @return Single containing available public actions
     */
    @GET("cross-chain/v1.0/order/{orderHash}/public-actions")
    Single<PublicActionOutput> getPublicActions(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Path("orderHash") String orderHash
    );
    
    /**
     * Get supported chains for FusionPlus.
     *
     * @return Single containing list of supported chain IDs
     */
    @GET("cross-chain/v1.0/chains")
    Single<GetActiveOrdersOutput> getSupportedChains();
}