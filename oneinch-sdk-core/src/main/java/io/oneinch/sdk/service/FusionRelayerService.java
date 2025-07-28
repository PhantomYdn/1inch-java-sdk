package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.SignedOrderInput;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for Fusion Relayer API operations.
 * Provides order submission functionality for signed gasless orders.
 * 
 * Supports three programming approaches:
 * - Reactive (RxJava): Methods ending with 'Rx' returning Single&lt;T&gt;
 * - Synchronous: Standard methods that block until completion
 * - Asynchronous: Methods ending with 'Async' returning CompletableFuture&lt;T&gt;
 */
public interface FusionRelayerService {
    
    // ==================== SINGLE ORDER SUBMISSION ====================
    
    /**
     * Submit a limit order that resolvers will be able to fill (reactive).
     *
     * @param chainId Chain ID for the network
     * @param signedOrder Signed order input containing order data, signature, extension, and quoteId
     * @return Single indicating successful submission
     */
    Single<Void> submitOrderRx(Integer chainId, SignedOrderInput signedOrder);
    
    /**
     * Submit a limit order that resolvers will be able to fill (asynchronous).
     *
     * @param chainId Chain ID for the network
     * @param signedOrder Signed order input containing order data, signature, extension, and quoteId
     * @return CompletableFuture indicating successful submission
     */
    CompletableFuture<Void> submitOrderAsync(Integer chainId, SignedOrderInput signedOrder);
    
    /**
     * Submit a limit order that resolvers will be able to fill (synchronous).
     *
     * @param chainId Chain ID for the network
     * @param signedOrder Signed order input containing order data, signature, extension, and quoteId
     * @throws OneInchException if the submission fails
     */
    void submitOrder(Integer chainId, SignedOrderInput signedOrder) throws OneInchException;
    
    // ==================== BATCH ORDER SUBMISSION ====================
    
    /**
     * Submit a list of limit orders which resolvers will be able to fill (reactive).
     *
     * @param chainId Chain ID for the network
     * @param signedOrders List of signed orders to submit
     * @return Single indicating successful submission
     */
    Single<Void> submitManyOrdersRx(Integer chainId, List<SignedOrderInput> signedOrders);
    
    /**
     * Submit a list of limit orders which resolvers will be able to fill (asynchronous).
     *
     * @param chainId Chain ID for the network
     * @param signedOrders List of signed orders to submit
     * @return CompletableFuture indicating successful submission
     */
    CompletableFuture<Void> submitManyOrdersAsync(Integer chainId, List<SignedOrderInput> signedOrders);
    
    /**
     * Submit a list of limit orders which resolvers will be able to fill (synchronous).
     *
     * @param chainId Chain ID for the network
     * @param signedOrders List of signed orders to submit
     * @throws OneInchException if the submission fails
     */
    void submitManyOrders(Integer chainId, List<SignedOrderInput> signedOrders) throws OneInchException;
}