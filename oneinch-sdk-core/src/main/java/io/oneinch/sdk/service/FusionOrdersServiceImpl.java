package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchFusionOrdersApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.*;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of FusionOrdersService providing order lifecycle management.
 * Supports reactive, synchronous, and asynchronous programming models.
 */
@Slf4j
@RequiredArgsConstructor
public class FusionOrdersServiceImpl implements FusionOrdersService {
    
    private final OneInchFusionOrdersApiService apiService;
    
    // ==================== ACTIVE ORDERS ====================
    
    @Override
    public Single<GetActiveOrdersOutput> getActiveOrdersRx(FusionActiveOrdersRequest request) {
        log.info("Getting active Fusion orders (reactive) for chain {} with page={}, limit={}", 
                request.getChainId(), request.getPage(), request.getLimit());
        
        return apiService.getActiveOrders(
                        request.getChainId(),
                        request.getPage(),
                        request.getLimit(),
                        request.getVersion())
                .doOnSuccess(response -> log.debug("Retrieved {} active orders", 
                        response.getItems() != null ? response.getItems().size() : 0))
                .doOnError(error -> log.error("Active orders request failed for chain {}", 
                        request.getChainId(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetActiveOrdersOutput> getActiveOrdersAsync(FusionActiveOrdersRequest request) {
        return getActiveOrdersRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetActiveOrdersOutput getActiveOrders(FusionActiveOrdersRequest request) throws OneInchException {
        try {
            return getActiveOrdersRx(request).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== SETTLEMENT CONTRACT ====================
    
    @Override
    public Single<SettlementAddressOutput> getSettlementContractRx(Integer chainId) {
        log.info("Getting settlement contract address (reactive) for chain {}", chainId);
        
        return apiService.getSettlementContract(chainId)
                .doOnSuccess(response -> log.debug("Settlement contract address: {}", response.getAddress()))
                .doOnError(error -> log.error("Settlement contract request failed for chain {}", chainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<SettlementAddressOutput> getSettlementContractAsync(Integer chainId) {
        return getSettlementContractRx(chainId).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public SettlementAddressOutput getSettlementContract(Integer chainId) throws OneInchException {
        try {
            return getSettlementContractRx(chainId).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== ORDER STATUS ====================
    
    @Override
    public Single<GetOrderFillsByHashOutput> getOrderByOrderHashRx(Integer chainId, String orderHash) {
        log.info("Getting order status (reactive) for chain {} and orderHash {}", chainId, orderHash);
        
        return apiService.getOrderByOrderHash(chainId, orderHash)
                .doOnSuccess(response -> log.debug("Order status: {} for hash {}", 
                        response.getStatus(), orderHash))
                .doOnError(error -> log.error("Order status request failed for chain {} and hash {}", 
                        chainId, orderHash, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetOrderFillsByHashOutput> getOrderByOrderHashAsync(Integer chainId, String orderHash) {
        return getOrderByOrderHashRx(chainId, orderHash).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetOrderFillsByHashOutput getOrderByOrderHash(Integer chainId, String orderHash) throws OneInchException {
        try {
            return getOrderByOrderHashRx(chainId, orderHash).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== BATCH ORDER STATUS ====================
    
    @Override
    public Single<GetOrderFillsByHashOutput> getOrdersByOrderHashesRx(Integer chainId, OrdersByHashesInput request) {
        log.info("Getting batch order status (reactive) for chain {} with {} hashes", 
                chainId, request.getOrderHashes() != null ? request.getOrderHashes().size() : 0);
        
        return apiService.getOrdersByOrderHashes(chainId, request)
                .doOnSuccess(response -> log.debug("Batch order status retrieved"))
                .doOnError(error -> log.error("Batch order status request failed for chain {}", chainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetOrderFillsByHashOutput> getOrdersByOrderHashesAsync(Integer chainId, OrdersByHashesInput request) {
        return getOrdersByOrderHashesRx(chainId, request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetOrderFillsByHashOutput getOrdersByOrderHashes(Integer chainId, OrdersByHashesInput request) throws OneInchException {
        try {
            return getOrdersByOrderHashesRx(chainId, request).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== ORDER HISTORY ====================
    
    @Override
    public Single<OrderFillsByMakerOutput> getOrdersByMakerRx(FusionOrderHistoryRequest request) {
        log.info("Getting order history (reactive) for chain {} and maker {}", 
                request.getChainId(), request.getAddress());
        
        return apiService.getOrdersByMaker(
                        request.getChainId(),
                        request.getAddress(),
                        request.getPage(),
                        request.getLimit(),
                        request.getTimestampFrom(),
                        request.getTimestampTo(),
                        request.getMakerToken(),
                        request.getTakerToken(),
                        request.getWithToken(),
                        request.getVersion())
                .doOnSuccess(response -> log.debug("Order history retrieved for maker {}", request.getAddress()))
                .doOnError(error -> log.error("Order history request failed for chain {} and maker {}", 
                        request.getChainId(), request.getAddress(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<OrderFillsByMakerOutput> getOrdersByMakerAsync(FusionOrderHistoryRequest request) {
        return getOrdersByMakerRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public OrderFillsByMakerOutput getOrdersByMaker(FusionOrderHistoryRequest request) throws OneInchException {
        try {
            return getOrdersByMakerRx(request).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
}