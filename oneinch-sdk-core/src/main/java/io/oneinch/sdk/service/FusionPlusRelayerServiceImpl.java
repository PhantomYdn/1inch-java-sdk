package io.oneinch.sdk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchFusionPlusRelayerApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusionplus.FusionPlusSignedOrderInput;
import io.oneinch.sdk.model.fusionplus.SecretInput;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of FusionPlusRelayerService providing cross-chain order submission and secret management.
 * Supports reactive, synchronous, and asynchronous programming models.
 */
@Slf4j
@RequiredArgsConstructor
public class FusionPlusRelayerServiceImpl implements FusionPlusRelayerService {
    
    private final OneInchFusionPlusRelayerApiService apiService;
    private final ObjectMapper objectMapper;
    
    // ==================== SINGLE ORDER SUBMISSION ====================
    
    @Override
    public Single<Void> submitOrderRx(Integer srcChainId, FusionPlusSignedOrderInput signedOrder) {
        log.info("Submitting FusionPlus order (reactive) to srcChain={}, orderHash={}", 
                srcChainId, signedOrder.getOrder() != null ? signedOrder.getOrder().getSalt() : "unknown");
        
        return apiService.submitOrder(srcChainId, signedOrder)
                .doOnSuccess(response -> log.debug("FusionPlus order submitted successfully to srcChain={}", srcChainId))
                .doOnError(error -> log.error("FusionPlus order submission failed for srcChain={}", srcChainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<Void> submitOrderAsync(Integer srcChainId, FusionPlusSignedOrderInput signedOrder) {
        return submitOrderRx(srcChainId, signedOrder).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public void submitOrder(Integer srcChainId, FusionPlusSignedOrderInput signedOrder) throws OneInchException {
        try {
            submitOrderRx(srcChainId, signedOrder).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== BATCH ORDER SUBMISSION ====================
    
    @Override
    public Single<Void> submitManyOrdersRx(Integer srcChainId, List<FusionPlusSignedOrderInput> signedOrders) {
        log.info("Submitting {} FusionPlus orders (reactive) to srcChain={}", 
                signedOrders != null ? signedOrders.size() : 0, srcChainId);
        
        try {
            // Convert list to JSON string as required by the API
            String ordersJson = objectMapper.writeValueAsString(signedOrders);
            
            return apiService.submitManyOrders(srcChainId, ordersJson)
                    .doOnSuccess(response -> log.debug("{} FusionPlus orders submitted successfully to srcChain={}", 
                            signedOrders.size(), srcChainId))
                    .doOnError(error -> log.error("FusionPlus batch order submission failed for srcChain={}", srcChainId, error))
                    .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize FusionPlus orders for batch submission", e);
            return Single.error(new OneInchException("Failed to serialize orders for batch submission", e));
        }
    }
    
    @Override
    public CompletableFuture<Void> submitManyOrdersAsync(Integer srcChainId, List<FusionPlusSignedOrderInput> signedOrders) {
        return submitManyOrdersRx(srcChainId, signedOrders).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public void submitManyOrders(Integer srcChainId, List<FusionPlusSignedOrderInput> signedOrders) throws OneInchException {
        try {
            submitManyOrdersRx(srcChainId, signedOrders).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== SECRET SUBMISSION ====================
    
    @Override
    public Single<Void> submitSecretRx(Integer chainId, SecretInput secretInput) {
        log.info("Submitting secret (reactive) to chainId={}, orderHash={}", 
                chainId, secretInput.getOrderHash());
        
        return apiService.submitSecret(chainId, secretInput)
                .doOnSuccess(response -> log.debug("Secret submitted successfully for orderHash={} on chainId={}", 
                        secretInput.getOrderHash(), chainId))
                .doOnError(error -> log.error("Secret submission failed for orderHash={} on chainId={}", 
                        secretInput.getOrderHash(), chainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<Void> submitSecretAsync(Integer chainId, SecretInput secretInput) {
        return submitSecretRx(chainId, secretInput).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public void submitSecret(Integer chainId, SecretInput secretInput) throws OneInchException {
        try {
            submitSecretRx(chainId, secretInput).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
}