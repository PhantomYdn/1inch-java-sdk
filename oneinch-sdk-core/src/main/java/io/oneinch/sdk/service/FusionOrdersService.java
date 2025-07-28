package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.*;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for Fusion Orders API operations.
 * Provides order lifecycle management including active orders, order status, and order history.
 * 
 * Supports three programming approaches:
 * - Reactive (RxJava): Methods ending with 'Rx' returning Single&lt;T&gt;
 * - Synchronous: Standard methods that block until completion
 * - Asynchronous: Methods ending with 'Async' returning CompletableFuture&lt;T&gt;
 */
public interface FusionOrdersService {
    
    // ==================== ACTIVE ORDERS ====================
    
    /**
     * Get gasless swap active orders (reactive).
     *
     * @param request Request parameters for active orders
     * @return Single containing paginated active orders
     */
    Single<GetActiveOrdersOutput> getActiveOrdersRx(FusionActiveOrdersRequest request);
    
    /**
     * Get gasless swap active orders (asynchronous).
     *
     * @param request Request parameters for active orders
     * @return CompletableFuture containing paginated active orders
     */
    CompletableFuture<GetActiveOrdersOutput> getActiveOrdersAsync(FusionActiveOrdersRequest request);
    
    /**
     * Get gasless swap active orders (synchronous).
     *
     * @param request Request parameters for active orders
     * @return Paginated active orders
     * @throws OneInchException if the request fails
     */
    GetActiveOrdersOutput getActiveOrders(FusionActiveOrdersRequest request) throws OneInchException;
    
    // ==================== SETTLEMENT CONTRACT ====================
    
    /**
     * Get actual settlement contract address (reactive).
     *
     * @param chainId Chain ID for the network
     * @return Single containing settlement contract address
     */
    Single<SettlementAddressOutput> getSettlementContractRx(Integer chainId);
    
    /**
     * Get actual settlement contract address (asynchronous).
     *
     * @param chainId Chain ID for the network
     * @return CompletableFuture containing settlement contract address
     */
    CompletableFuture<SettlementAddressOutput> getSettlementContractAsync(Integer chainId);
    
    /**
     * Get actual settlement contract address (synchronous).
     *
     * @param chainId Chain ID for the network
     * @return Settlement contract address
     * @throws OneInchException if the request fails
     */
    SettlementAddressOutput getSettlementContract(Integer chainId) throws OneInchException;
    
    // ==================== ORDER STATUS ====================
    
    /**
     * Get order status by order hash (reactive).
     *
     * @param chainId Chain ID for the network
     * @param orderHash Order hash to query
     * @return Single containing order status and fill details
     */
    Single<GetOrderFillsByHashOutput> getOrderByOrderHashRx(Integer chainId, String orderHash);
    
    /**
     * Get order status by order hash (asynchronous).
     *
     * @param chainId Chain ID for the network
     * @param orderHash Order hash to query
     * @return CompletableFuture containing order status and fill details
     */
    CompletableFuture<GetOrderFillsByHashOutput> getOrderByOrderHashAsync(Integer chainId, String orderHash);
    
    /**
     * Get order status by order hash (synchronous).
     *
     * @param chainId Chain ID for the network
     * @param orderHash Order hash to query
     * @return Order status and fill details
     * @throws OneInchException if the request fails
     */
    GetOrderFillsByHashOutput getOrderByOrderHash(Integer chainId, String orderHash) throws OneInchException;
    
    // ==================== BATCH ORDER STATUS ====================
    
    /**
     * Get orders by multiple hashes (batch request) (reactive).
     *
     * @param chainId Chain ID for the network
     * @param request Request containing list of order hashes
     * @return Single containing order status details for all requested hashes
     */
    Single<GetOrderFillsByHashOutput> getOrdersByOrderHashesRx(Integer chainId, OrdersByHashesInput request);
    
    /**
     * Get orders by multiple hashes (batch request) (asynchronous).
     *
     * @param chainId Chain ID for the network
     * @param request Request containing list of order hashes
     * @return CompletableFuture containing order status details for all requested hashes
     */
    CompletableFuture<GetOrderFillsByHashOutput> getOrdersByOrderHashesAsync(Integer chainId, OrdersByHashesInput request);
    
    /**
     * Get orders by multiple hashes (batch request) (synchronous).
     *
     * @param chainId Chain ID for the network
     * @param request Request containing list of order hashes
     * @return Order status details for all requested hashes
     * @throws OneInchException if the request fails
     */
    GetOrderFillsByHashOutput getOrdersByOrderHashes(Integer chainId, OrdersByHashesInput request) throws OneInchException;
    
    // ==================== ORDER HISTORY ====================
    
    /**
     * Get orders history by maker address (reactive).
     *
     * @param request Request parameters for order history
     * @return Single containing order history for the maker
     */
    Single<OrderFillsByMakerOutput> getOrdersByMakerRx(FusionOrderHistoryRequest request);
    
    /**
     * Get orders history by maker address (asynchronous).
     *
     * @param request Request parameters for order history
     * @return CompletableFuture containing order history for the maker
     */
    CompletableFuture<OrderFillsByMakerOutput> getOrdersByMakerAsync(FusionOrderHistoryRequest request);
    
    /**
     * Get orders history by maker address (synchronous).
     *
     * @param request Request parameters for order history
     * @return Order history for the maker
     * @throws OneInchException if the request fails
     */
    OrderFillsByMakerOutput getOrdersByMaker(FusionOrderHistoryRequest request) throws OneInchException;
}