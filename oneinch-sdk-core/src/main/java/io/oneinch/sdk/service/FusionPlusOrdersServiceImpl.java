package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchFusionPlusOrdersApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of FusionPlusOrdersService providing cross-chain order lifecycle management.
 * Supports reactive, synchronous, and asynchronous programming models.
 */
@Slf4j
@RequiredArgsConstructor
public class FusionPlusOrdersServiceImpl implements FusionPlusOrdersService {
    
    private final OneInchFusionPlusOrdersApiService apiService;
    
    // ==================== ACTIVE CROSS-CHAIN ORDERS ====================
    
    @Override
    public Single<GetActiveOrdersOutput> getActiveOrdersRx(FusionPlusActiveOrdersRequest request) {
        log.info("Getting active FusionPlus orders (reactive) for srcChain={}, dstChain={}, page={}, limit={}", 
                request.getSrcChain(), request.getDstChain(), request.getPage(), request.getLimit());
        
        return apiService.getActiveOrders(
                        request.getSrcChain(),
                        request.getDstChain(),
                        request.getPage(),
                        request.getLimit(),
                        request.getSortBy())
                .doOnSuccess(response -> log.debug("Retrieved {} active cross-chain orders", 
                        response.getItems() != null ? response.getItems().size() : 0))
                .doOnError(error -> log.error("Active cross-chain orders request failed for srcChain={}, dstChain={}", 
                        request.getSrcChain(), request.getDstChain(), error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetActiveOrdersOutput> getActiveOrdersAsync(FusionPlusActiveOrdersRequest request) {
        return getActiveOrdersRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetActiveOrdersOutput getActiveOrders(FusionPlusActiveOrdersRequest request) throws OneInchException {
        try {
            return getActiveOrdersRx(request).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== CROSS-CHAIN ORDER STATUS ====================
    
    @Override
    public Single<CrossChainOrderDto> getOrderByOrderHashRx(Integer srcChain, Integer dstChain, String orderHash) {
        log.info("Getting cross-chain order status (reactive) for srcChain={}, dstChain={}, orderHash={}", 
                srcChain, dstChain, orderHash);
        
        return apiService.getOrderByOrderHash(srcChain, dstChain, orderHash)
                .doOnSuccess(response -> log.debug("Cross-chain order status: {} for hash {}", 
                        response.getStatus(), orderHash))
                .doOnError(error -> log.error("Cross-chain order status request failed for srcChain={}, dstChain={}, hash={}", 
                        srcChain, dstChain, orderHash, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<CrossChainOrderDto> getOrderByOrderHashAsync(Integer srcChain, Integer dstChain, String orderHash) {
        return getOrderByOrderHashRx(srcChain, dstChain, orderHash).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public CrossChainOrderDto getOrderByOrderHash(Integer srcChain, Integer dstChain, String orderHash) throws OneInchException {
        try {
            return getOrderByOrderHashRx(srcChain, dstChain, orderHash).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== BATCH CROSS-CHAIN ORDER STATUS ====================
    
    @Override
    public Single<GetActiveOrdersOutput> getOrdersByOrderHashesRx(Integer srcChain, Integer dstChain, String orderHashes) {
        log.info("Getting batch cross-chain order status (reactive) for srcChain={}, dstChain={}", 
                srcChain, dstChain);
        
        return apiService.getOrdersByOrderHashes(srcChain, dstChain, orderHashes)
                .doOnSuccess(response -> log.debug("Retrieved {} cross-chain orders by hashes", 
                        response.getItems() != null ? response.getItems().size() : 0))
                .doOnError(error -> log.error("Batch cross-chain order request failed for srcChain={}, dstChain={}", 
                        srcChain, dstChain, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetActiveOrdersOutput> getOrdersByOrderHashesAsync(Integer srcChain, Integer dstChain, String orderHashes) {
        return getOrdersByOrderHashesRx(srcChain, dstChain, orderHashes).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetActiveOrdersOutput getOrdersByOrderHashes(Integer srcChain, Integer dstChain, String orderHashes) throws OneInchException {
        try {
            return getOrdersByOrderHashesRx(srcChain, dstChain, orderHashes).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== CROSS-CHAIN ORDER HISTORY ====================
    
    @Override
    public Single<GetActiveOrdersOutput> getOrdersByMakerRx(Integer srcChain, Integer dstChain, String address, Integer page, Integer limit) {
        log.info("Getting cross-chain order history (reactive) for srcChain={}, dstChain={}, address={}", 
                srcChain, dstChain, address);
        
        return apiService.getOrdersByMaker(srcChain, dstChain, address, page, limit)
                .doOnSuccess(response -> log.debug("Retrieved {} cross-chain orders for maker {}", 
                        response.getItems() != null ? response.getItems().size() : 0, address))
                .doOnError(error -> log.error("Cross-chain order history request failed for address={}", 
                        address, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetActiveOrdersOutput> getOrdersByMakerAsync(Integer srcChain, Integer dstChain, String address, Integer page, Integer limit) {
        return getOrdersByMakerRx(srcChain, dstChain, address, page, limit).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetActiveOrdersOutput getOrdersByMaker(Integer srcChain, Integer dstChain, String address, Integer page, Integer limit) throws OneInchException {
        try {
            return getOrdersByMakerRx(srcChain, dstChain, address, page, limit).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== ESCROW EVENTS ====================
    
    @Override
    public Single<GetActiveOrdersOutput> getEscrowEventsRx(Integer srcChain, Integer dstChain, String orderHash, Integer page, Integer limit) {
        log.info("Getting escrow events (reactive) for srcChain={}, dstChain={}, orderHash={}", 
                srcChain, dstChain, orderHash);
        
        return apiService.getEscrowEvents(srcChain, dstChain, orderHash, page, limit)
                .doOnSuccess(response -> log.debug("Retrieved {} escrow events for order {}", 
                        response.getItems() != null ? response.getItems().size() : 0, orderHash))
                .doOnError(error -> log.error("Escrow events request failed for orderHash={}", 
                        orderHash, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetActiveOrdersOutput> getEscrowEventsAsync(Integer srcChain, Integer dstChain, String orderHash, Integer page, Integer limit) {
        return getEscrowEventsRx(srcChain, dstChain, orderHash, page, limit).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetActiveOrdersOutput getEscrowEvents(Integer srcChain, Integer dstChain, String orderHash, Integer page, Integer limit) throws OneInchException {
        try {
            return getEscrowEventsRx(srcChain, dstChain, orderHash, page, limit).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== PUBLIC ACTIONS ====================
    
    @Override
    public Single<PublicActionOutput> getPublicActionsRx(Integer srcChain, Integer dstChain, String orderHash) {
        log.info("Getting public actions (reactive) for srcChain={}, dstChain={}, orderHash={}", 
                srcChain, dstChain, orderHash);
        
        return apiService.getPublicActions(srcChain, dstChain, orderHash)
                .doOnSuccess(response -> log.debug("Retrieved public action {} for order {}", 
                        response.getActionType(), orderHash))
                .doOnError(error -> log.error("Public actions request failed for orderHash={}", 
                        orderHash, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<PublicActionOutput> getPublicActionsAsync(Integer srcChain, Integer dstChain, String orderHash) {
        return getPublicActionsRx(srcChain, dstChain, orderHash).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public PublicActionOutput getPublicActions(Integer srcChain, Integer dstChain, String orderHash) throws OneInchException {
        try {
            return getPublicActionsRx(srcChain, dstChain, orderHash).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== SUPPORTED CHAINS ====================
    
    @Override
    public Single<GetActiveOrdersOutput> getSupportedChainsRx() {
        log.info("Getting supported chains (reactive) for FusionPlus");
        
        return apiService.getSupportedChains()
                .doOnSuccess(response -> log.debug("Retrieved {} supported chains", 
                        response.getItems() != null ? response.getItems().size() : 0))
                .doOnError(error -> log.error("Supported chains request failed", error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<GetActiveOrdersOutput> getSupportedChainsAsync() {
        return getSupportedChainsRx().toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public GetActiveOrdersOutput getSupportedChains() throws OneInchException {
        try {
            return getSupportedChainsRx().blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
}