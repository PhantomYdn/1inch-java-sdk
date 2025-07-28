package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchFusionRelayerApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.SignedOrderInput;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of FusionRelayerService providing order submission functionality.
 * Supports reactive, synchronous, and asynchronous programming models.
 */
@Slf4j
@RequiredArgsConstructor
public class FusionRelayerServiceImpl implements FusionRelayerService {
    
    private final OneInchFusionRelayerApiService apiService;
    
    // ==================== SINGLE ORDER SUBMISSION ====================
    
    @Override
    public Single<Void> submitOrderRx(Integer chainId, SignedOrderInput signedOrder) {
        log.info("Submitting Fusion order (reactive) for chain {} with quoteId {}", 
                chainId, signedOrder.getQuoteId());
        
        return apiService.submitOrder(chainId, signedOrder)
                .doOnSuccess(response -> log.debug("Fusion order submitted successfully for chain {}", chainId))
                .doOnError(error -> log.error("Fusion order submission failed for chain {}", chainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<Void> submitOrderAsync(Integer chainId, SignedOrderInput signedOrder) {
        return submitOrderRx(chainId, signedOrder).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public void submitOrder(Integer chainId, SignedOrderInput signedOrder) throws OneInchException {
        try {
            submitOrderRx(chainId, signedOrder).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== BATCH ORDER SUBMISSION ====================
    
    @Override
    public Single<Void> submitManyOrdersRx(Integer chainId, List<SignedOrderInput> signedOrders) {
        log.info("Submitting {} Fusion orders (reactive) for chain {}", 
                signedOrders != null ? signedOrders.size() : 0, chainId);
        
        return apiService.submitManyOrders(chainId, signedOrders)
                .doOnSuccess(response -> log.debug("{} Fusion orders submitted successfully for chain {}", 
                        signedOrders != null ? signedOrders.size() : 0, chainId))
                .doOnError(error -> log.error("Batch Fusion order submission failed for chain {}", chainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<Void> submitManyOrdersAsync(Integer chainId, List<SignedOrderInput> signedOrders) {
        return submitManyOrdersRx(chainId, signedOrders).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public void submitManyOrders(Integer chainId, List<SignedOrderInput> signedOrders) throws OneInchException {
        try {
            submitManyOrdersRx(chainId, signedOrders).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
}