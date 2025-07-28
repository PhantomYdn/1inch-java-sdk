package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusionplus.FusionPlusSignedOrderInput;
import io.oneinch.sdk.model.fusionplus.SecretInput;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for FusionPlus Relayer API operations.
 * Handles cross-chain order submission and secret management for atomic swaps.
 * 
 * Supports three programming approaches:
 * - Reactive (RxJava): Methods ending with 'Rx' returning Single&lt;T&gt;
 * - Synchronous: Standard methods that block until completion
 * - Asynchronous: Methods ending with 'Async' returning CompletableFuture&lt;T&gt;
 */
public interface FusionPlusRelayerService {
    
    // ==================== SINGLE ORDER SUBMISSION ====================
    
    /**
     * Submit a single cross-chain order to the FusionPlus network (reactive).
     *
     * @param srcChainId Source chain ID where the order originates
     * @param signedOrder Signed cross-chain order with all required parameters
     * @return Single that completes when order is submitted successfully
     */
    Single<Void> submitOrderRx(Integer srcChainId, FusionPlusSignedOrderInput signedOrder);
    
    /**
     * Submit a single cross-chain order to the FusionPlus network (asynchronous).
     *
     * @param srcChainId Source chain ID where the order originates
     * @param signedOrder Signed cross-chain order with all required parameters
     * @return CompletableFuture that completes when order is submitted successfully
     */
    CompletableFuture<Void> submitOrderAsync(Integer srcChainId, FusionPlusSignedOrderInput signedOrder);
    
    /**
     * Submit a single cross-chain order to the FusionPlus network (synchronous).
     *
     * @param srcChainId Source chain ID where the order originates
     * @param signedOrder Signed cross-chain order with all required parameters
     * @throws OneInchException if the submission fails
     */
    void submitOrder(Integer srcChainId, FusionPlusSignedOrderInput signedOrder) throws OneInchException;
    
    // ==================== BATCH ORDER SUBMISSION ====================
    
    /**
     * Submit multiple cross-chain orders to the FusionPlus network in batch (reactive).
     *
     * @param srcChainId Source chain ID where the orders originate
     * @param signedOrders List of signed cross-chain orders
     * @return Single that completes when all orders are submitted successfully
     */
    Single<Void> submitManyOrdersRx(Integer srcChainId, List<FusionPlusSignedOrderInput> signedOrders);
    
    /**
     * Submit multiple cross-chain orders to the FusionPlus network in batch (asynchronous).
     *
     * @param srcChainId Source chain ID where the orders originate
     * @param signedOrders List of signed cross-chain orders
     * @return CompletableFuture that completes when all orders are submitted successfully
     */
    CompletableFuture<Void> submitManyOrdersAsync(Integer srcChainId, List<FusionPlusSignedOrderInput> signedOrders);
    
    /**
     * Submit multiple cross-chain orders to the FusionPlus network in batch (synchronous).
     *
     * @param srcChainId Source chain ID where the orders originate
     * @param signedOrders List of signed cross-chain orders
     * @throws OneInchException if the submission fails
     */
    void submitManyOrders(Integer srcChainId, List<FusionPlusSignedOrderInput> signedOrders) throws OneInchException;
    
    // ==================== SECRET SUBMISSION ====================
    
    /**
     * Submit secret for cross-chain order execution (reactive).
     * This is the second phase of the cross-chain atomic swap process.
     *
     * @param chainId Chain ID where the secret should be submitted
     * @param secretInput Secret information for order execution
     * @return Single that completes when secret is submitted successfully
     */
    Single<Void> submitSecretRx(Integer chainId, SecretInput secretInput);
    
    /**
     * Submit secret for cross-chain order execution (asynchronous).
     * This is the second phase of the cross-chain atomic swap process.
     *
     * @param chainId Chain ID where the secret should be submitted
     * @param secretInput Secret information for order execution
     * @return CompletableFuture that completes when secret is submitted successfully
     */
    CompletableFuture<Void> submitSecretAsync(Integer chainId, SecretInput secretInput);
    
    /**
     * Submit secret for cross-chain order execution (synchronous).
     * This is the second phase of the cross-chain atomic swap process.
     *
     * @param chainId Chain ID where the secret should be submitted
     * @param secretInput Secret information for order execution
     * @throws OneInchException if the submission fails
     */
    void submitSecret(Integer chainId, SecretInput secretInput) throws OneInchException;
}