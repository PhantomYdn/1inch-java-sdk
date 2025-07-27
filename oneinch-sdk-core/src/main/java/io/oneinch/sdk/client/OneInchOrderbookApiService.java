package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Retrofit interface for 1inch Orderbook API v4.0
 * Provides limit order creation, management, and querying functionality
 */
public interface OneInchOrderbookApiService {

    /**
     * Create a new limit order on the 1inch orderbook.
     * Submits a limit order that can be filled by other users.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param request limit order request containing order details and signature
     * @return Single containing limit order response with order hash and status
     */
    @POST("orderbook/v4.0/{chain}")
    Single<LimitOrderV4Response> createLimitOrder(
            @Path("chain") Integer chainId,
            @Body LimitOrderV4Request request
    );

    /**
     * Get limit orders created by a specific wallet address.
     * Returns paginated list of orders with optional filtering.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param address wallet address that created the orders
     * @param page page number for pagination (optional)
     * @param limit number of orders per page (optional)
     * @param statuses comma-separated list of order statuses to filter by (optional)
     * @param sortBy field to sort results by (optional)
     * @param takerAsset token address that takers receive (optional filter)
     * @param makerAsset token address that makers provide (optional filter)
     * @return Single containing list of limit orders for the address
     */
    @GET("orderbook/v4.0/{chain}/address/{address}")
    Single<List<GetLimitOrdersV4Response>> getLimitOrdersByAddress(
            @Path("chain") Integer chainId,
            @Path("address") String address,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("statuses") String statuses,
            @Query("sortBy") String sortBy,
            @Query("takerAsset") String takerAsset,
            @Query("makerAsset") String makerAsset
    );

    /**
     * Get a specific limit order by its order hash.
     * Returns detailed information about a single order.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param orderHash unique hash identifier of the order
     * @return Single containing limit order details
     */
    @GET("orderbook/v4.0/{chain}/order/{orderHash}")
    Single<GetLimitOrdersV4Response> getOrderByOrderHash(
            @Path("chain") Integer chainId,
            @Path("orderHash") String orderHash
    );

    /**
     * Get all limit orders from the orderbook.
     * Returns paginated list of all orders with optional filtering.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param page page number for pagination (optional)
     * @param limit number of orders per page (optional)
     * @param statuses comma-separated list of order statuses to filter by (optional)
     * @param sortBy field to sort results by (optional)
     * @param takerAsset token address that takers receive (optional filter)
     * @param makerAsset token address that makers provide (optional filter)
     * @return Single containing list of all limit orders
     */
    @GET("orderbook/v4.0/{chain}/all")
    Single<List<GetLimitOrdersV4Response>> getAllLimitOrders(
            @Path("chain") Integer chainId,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("statuses") String statuses,
            @Query("sortBy") String sortBy,
            @Query("takerAsset") String takerAsset,
            @Query("makerAsset") String makerAsset
    );

    /**
     * Get the total count of limit orders matching the filter criteria.
     * Returns order count statistics for analytics and pagination.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param statuses comma-separated list of order statuses to filter by (optional)
     * @param takerAsset token address that takers receive (optional filter)
     * @param makerAsset token address that makers provide (optional filter)
     * @return Single containing order count information
     */
    @GET("orderbook/v4.0/{chain}/count")
    Single<GetLimitOrdersCountV4Response> getOrdersCount(
            @Path("chain") Integer chainId,
            @Query("statuses") String statuses,
            @Query("takerAsset") String takerAsset,
            @Query("makerAsset") String makerAsset
    );

    /**
     * Get events related to a specific limit order.
     * Returns fill events, cancellations, and other order lifecycle events.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param orderHash unique hash identifier of the order
     * @return Single containing map of event types to event lists
     */
    @GET("orderbook/v4.0/{chain}/events/{orderHash}")
    Single<Map<String, List<GetEventsV4Response>>> getEventsByOrderHash(
            @Path("chain") Integer chainId,
            @Path("orderHash") String orderHash
    );

    /**
     * Get all recent events from the orderbook.
     * Returns the latest order events across all orders.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param limit maximum number of events to return (optional)
     * @return Single containing list of recent orderbook events
     */
    @GET("orderbook/v4.0/{chain}/events")
    Single<List<GetEventsV4Response>> getAllEvents(
            @Path("chain") Integer chainId,
            @Query("limit") Integer limit
    );

    /**
     * Check if a wallet has active orders with permit for a specific token.
     * Used to determine if gasless order cancellation is possible.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param walletAddress wallet address to check for active orders
     * @param token token contract address to check for permit orders
     * @return Single containing information about active permit orders
     */
    @GET("orderbook/v4.0/{chain}/has-active-orders-with-permit/{walletAddress}/{token}")
    Single<GetHasActiveOrdersWithPermitV4Response> hasActiveOrdersWithPermit(
            @Path("chain") Integer chainId,
            @Path("walletAddress") String walletAddress,
            @Path("token") String token
    );

    /**
     * Get unique trading pairs that have active orders.
     * Returns list of token pairs with available liquidity in the orderbook.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param page page number for pagination (optional)
     * @param limit number of pairs per page (optional)
     * @return Single containing list of active trading pairs
     */
    @GET("orderbook/v4.0/{chain}/unique-active-pairs")
    Single<GetActiveUniquePairsResponse> getUniqueActivePairs(
            @Path("chain") Integer chainId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );
}