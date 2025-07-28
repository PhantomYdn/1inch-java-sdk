package io.oneinch.sdk.client;

import io.oneinch.sdk.model.fusion.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

/**
 * Retrofit API service interface for 1inch Fusion Orders API.
 * Handles order lifecycle management including active orders, order status, and order history.
 */
public interface OneInchFusionOrdersApiService {
    
    /**
     * Get gasless swap active orders.
     *
     * @param chainId Chain ID for the network
     * @param page Pagination step, default: 1 (page = offset / limit)
     * @param limit Number of active orders to receive (default: 100, max: 500)
     * @param version Settlement extension version: 2.0 or 2.1. By default: all
     * @return Single containing paginated active orders
     */
    @GET("fusion/v2.0/{chainId}/orders/order/active")
    Single<GetActiveOrdersOutput> getActiveOrders(
            @Path("chainId") Integer chainId,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("version") String version
    );
    
    /**
     * Get actual settlement contract address.
     *
     * @param chainId Chain ID for the network
     * @return Single containing settlement contract address
     */
    @GET("fusion/v2.0/{chainId}/orders/order/settlement")
    Single<SettlementAddressOutput> getSettlementContract(@Path("chainId") Integer chainId);
    
    /**
     * Get order status by order hash.
     *
     * @param chainId Chain ID for the network
     * @param orderHash Order hash to query
     * @return Single containing order status and fill details
     */
    @GET("fusion/v2.0/{chainId}/orders/order/status/{orderHash}")
    Single<GetOrderFillsByHashOutput> getOrderByOrderHash(
            @Path("chainId") Integer chainId,
            @Path("orderHash") String orderHash
    );
    
    /**
     * Get orders by multiple hashes (batch request).
     *
     * @param chainId Chain ID for the network
     * @param request Request containing list of order hashes
     * @return Single containing order status details for all requested hashes
     */
    @POST("fusion/v2.0/{chainId}/orders/order/status")
    Single<GetOrderFillsByHashOutput> getOrdersByOrderHashes(
            @Path("chainId") Integer chainId,
            @Body OrdersByHashesInput request
    );
    
    /**
     * Get orders history by maker address.
     *
     * @param chainId Chain ID for the network
     * @param address Maker's address
     * @param page Pagination step, default: 1 (page = offset / limit)
     * @param limit Number of orders to receive (default: 100, max: 500)
     * @param timestampFrom Timestamp from in milliseconds for interval [timestampFrom, timestampTo)
     * @param timestampTo Timestamp to in milliseconds for interval [timestampFrom, timestampTo)
     * @param makerToken Find history by the given maker token
     * @param takerToken Find history by the given taker token
     * @param withToken Find history items by source or destination token
     * @param version Settlement extension version: 2.0 or 2.1. By default: all
     * @return Single containing order history for the maker
     */
    @GET("fusion/v2.0/{chainId}/orders/order/maker/{address}")
    Single<OrderFillsByMakerOutput> getOrdersByMaker(
            @Path("chainId") Integer chainId,
            @Path("address") String address,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("timestampFrom") Long timestampFrom,
            @Query("timestampTo") Long timestampTo,
            @Query("makerToken") String makerToken,
            @Query("takerToken") String takerToken,
            @Query("withToken") String withToken,
            @Query("version") String version
    );
}