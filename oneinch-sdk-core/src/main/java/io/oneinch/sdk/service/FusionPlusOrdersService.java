package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for FusionPlus Orders API operations.
 * Provides cross-chain order lifecycle management including active orders, order status, and events.
 * 
 * Supports three programming approaches:
 * - Reactive (RxJava): Methods ending with 'Rx' returning Single&lt;T&gt;
 * - Synchronous: Standard methods that block until completion
 * - Asynchronous: Methods ending with 'Async' returning CompletableFuture&lt;T&gt;
 */
public interface FusionPlusOrdersService {
    
    // ==================== ACTIVE CROSS-CHAIN ORDERS ====================
    
    /**
     * Get cross-chain gasless swap active orders (reactive).
     *
     * @param request Request parameters for active cross-chain orders
     * @return Single containing paginated active cross-chain orders
     */
    Single<GetActiveOrdersOutput> getActiveOrdersRx(FusionPlusActiveOrdersRequest request);
    
    /**
     * Get cross-chain gasless swap active orders (asynchronous).
     *
     * @param request Request parameters for active cross-chain orders
     * @return CompletableFuture containing paginated active cross-chain orders
     */
    CompletableFuture<GetActiveOrdersOutput> getActiveOrdersAsync(FusionPlusActiveOrdersRequest request);
    
    /**
     * Get cross-chain gasless swap active orders (synchronous).
     *
     * @param request Request parameters for active cross-chain orders
     * @return Paginated active cross-chain orders
     * @throws OneInchException if the request fails
     */
    GetActiveOrdersOutput getActiveOrders(FusionPlusActiveOrdersRequest request) throws OneInchException;
    
    // ==================== CROSS-CHAIN ORDER STATUS ====================
    
    /**
     * Get cross-chain order status by order hash (reactive).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to query
     * @return Single containing cross-chain order status and details
     */
    Single<CrossChainOrderDto> getOrderByOrderHashRx(Integer srcChain, Integer dstChain, String orderHash);
    
    /**
     * Get cross-chain order status by order hash (asynchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to query
     * @return CompletableFuture containing cross-chain order status and details
     */
    CompletableFuture<CrossChainOrderDto> getOrderByOrderHashAsync(Integer srcChain, Integer dstChain, String orderHash);
    
    /**
     * Get cross-chain order status by order hash (synchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to query
     * @return Cross-chain order status and details
     * @throws OneInchException if the request fails
     */
    CrossChainOrderDto getOrderByOrderHash(Integer srcChain, Integer dstChain, String orderHash) throws OneInchException;
    
    // ==================== BATCH CROSS-CHAIN ORDER STATUS ====================
    
    /**
     * Get cross-chain orders by multiple hashes (batch request) (reactive).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHashes Comma-separated list of order hashes
     * @return Single containing order status details for all requested hashes
     */
    Single<GetActiveOrdersOutput> getOrdersByOrderHashesRx(Integer srcChain, Integer dstChain, String orderHashes);
    
    /**
     * Get cross-chain orders by multiple hashes (batch request) (asynchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHashes Comma-separated list of order hashes
     * @return CompletableFuture containing order status details for all requested hashes
     */
    CompletableFuture<GetActiveOrdersOutput> getOrdersByOrderHashesAsync(Integer srcChain, Integer dstChain, String orderHashes);
    
    /**
     * Get cross-chain orders by multiple hashes (batch request) (synchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHashes Comma-separated list of order hashes
     * @return Order status details for all requested hashes
     * @throws OneInchException if the request fails
     */
    GetActiveOrdersOutput getOrdersByOrderHashes(Integer srcChain, Integer dstChain, String orderHashes) throws OneInchException;
    
    // ==================== CROSS-CHAIN ORDER HISTORY ====================
    
    /**
     * Get cross-chain orders history by maker address (reactive).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param address Maker's address
     * @param page Pagination step
     * @param limit Number of orders to receive
     * @return Single containing cross-chain order history for the maker
     */
    Single<GetActiveOrdersOutput> getOrdersByMakerRx(Integer srcChain, Integer dstChain, String address, Integer page, Integer limit);
    
    /**
     * Get cross-chain orders history by maker address (asynchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param address Maker's address
     * @param page Pagination step
     * @param limit Number of orders to receive
     * @return CompletableFuture containing cross-chain order history for the maker
     */
    CompletableFuture<GetActiveOrdersOutput> getOrdersByMakerAsync(Integer srcChain, Integer dstChain, String address, Integer page, Integer limit);
    
    /**
     * Get cross-chain orders history by maker address (synchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param address Maker's address
     * @param page Pagination step
     * @param limit Number of orders to receive
     * @return Cross-chain order history for the maker
     * @throws OneInchException if the request fails
     */
    GetActiveOrdersOutput getOrdersByMaker(Integer srcChain, Integer dstChain, String address, Integer page, Integer limit) throws OneInchException;
    
    // ==================== ESCROW EVENTS ====================
    
    /**
     * Get escrow events for cross-chain orders (reactive).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get events for
     * @param page Pagination step
     * @param limit Number of events to receive
     * @return Single containing list of escrow events
     */
    Single<GetActiveOrdersOutput> getEscrowEventsRx(Integer srcChain, Integer dstChain, String orderHash, Integer page, Integer limit);
    
    /**
     * Get escrow events for cross-chain orders (asynchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get events for
     * @param page Pagination step
     * @param limit Number of events to receive
     * @return CompletableFuture containing list of escrow events
     */
    CompletableFuture<GetActiveOrdersOutput> getEscrowEventsAsync(Integer srcChain, Integer dstChain, String orderHash, Integer page, Integer limit);
    
    /**
     * Get escrow events for cross-chain orders (synchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get events for
     * @param page Pagination step
     * @param limit Number of events to receive
     * @return List of escrow events
     * @throws OneInchException if the request fails
     */
    GetActiveOrdersOutput getEscrowEvents(Integer srcChain, Integer dstChain, String orderHash, Integer page, Integer limit) throws OneInchException;
    
    // ==================== PUBLIC ACTIONS ====================
    
    /**
     * Get public actions available for cross-chain orders (reactive).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get public actions for
     * @return Single containing available public actions
     */
    Single<PublicActionOutput> getPublicActionsRx(Integer srcChain, Integer dstChain, String orderHash);
    
    /**
     * Get public actions available for cross-chain orders (asynchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get public actions for
     * @return CompletableFuture containing available public actions
     */
    CompletableFuture<PublicActionOutput> getPublicActionsAsync(Integer srcChain, Integer dstChain, String orderHash);
    
    /**
     * Get public actions available for cross-chain orders (synchronous).
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param orderHash Order hash to get public actions for
     * @return Available public actions
     * @throws OneInchException if the request fails
     */
    PublicActionOutput getPublicActions(Integer srcChain, Integer dstChain, String orderHash) throws OneInchException;
    
    // ==================== SUPPORTED CHAINS ====================
    
    /**
     * Get supported chains for FusionPlus (reactive).
     *
     * @return Single containing list of supported chain IDs
     */
    Single<GetActiveOrdersOutput> getSupportedChainsRx();
    
    /**
     * Get supported chains for FusionPlus (asynchronous).
     *
     * @return CompletableFuture containing list of supported chain IDs
     */
    CompletableFuture<GetActiveOrdersOutput> getSupportedChainsAsync();
    
    /**
     * Get supported chains for FusionPlus (synchronous).
     *
     * @return List of supported chain IDs
     * @throws OneInchException if the request fails
     */
    GetActiveOrdersOutput getSupportedChains() throws OneInchException;
}