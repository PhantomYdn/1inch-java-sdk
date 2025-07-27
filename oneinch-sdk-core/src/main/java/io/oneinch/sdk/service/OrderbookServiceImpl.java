package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchOrderbookApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.orderbook.*;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class OrderbookServiceImpl implements OrderbookService {

    private final OneInchOrderbookApiService apiService;

    @Override
    public LimitOrderV4Response createLimitOrder(LimitOrderV4Request request) throws OneInchException {
        log.info("Creating limit order for maker asset {} to taker asset {} on chain {}", 
                request.getData().getMakerAsset(), request.getData().getTakerAsset(), request.getChainId());
        try {
            return createLimitOrderRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Failed to create limit order", e);
            throw new OneInchException("Failed to create limit order", e);
        }
    }

    @Override
    public CompletableFuture<LimitOrderV4Response> createLimitOrderAsync(LimitOrderV4Request request) {
        return createLimitOrderRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<LimitOrderV4Response> createLimitOrderRx(LimitOrderV4Request request) {
        log.info("Creating limit order (reactive) for maker asset {} to taker asset {} on chain {}", 
                request.getData().getMakerAsset(), request.getData().getTakerAsset(), request.getChainId());
        return apiService.createLimitOrder(request.getChainId(), request)
                .doOnSuccess(response -> log.debug("Limit order created successfully: {}", response.isSuccess()))
                .doOnError(error -> log.error("Failed to create limit order", error));
    }

    @Override
    public List<GetLimitOrdersV4Response> getLimitOrdersByAddress(GetLimitOrdersRequest request) throws OneInchException {
        log.info("Getting limit orders by address {} on chain {}", request.getAddress(), request.getChainId());
        try {
            return getLimitOrdersByAddressRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Failed to get limit orders by address", e);
            throw new OneInchException("Failed to get limit orders by address", e);
        }
    }

    @Override
    public CompletableFuture<List<GetLimitOrdersV4Response>> getLimitOrdersByAddressAsync(GetLimitOrdersRequest request) {
        return getLimitOrdersByAddressRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<GetLimitOrdersV4Response>> getLimitOrdersByAddressRx(GetLimitOrdersRequest request) {
        log.info("Getting limit orders (reactive) by address {} on chain {}", request.getAddress(), request.getChainId());
        return apiService.getLimitOrdersByAddress(
                        request.getChainId(),
                        request.getAddress(),
                        request.getPage(),
                        request.getLimit(),
                        request.getStatuses(),
                        request.getSortBy(),
                        request.getTakerAsset(),
                        request.getMakerAsset()
                )
                .doOnSuccess(orders -> log.debug("Retrieved {} limit orders for address", orders.size()))
                .doOnError(error -> log.error("Failed to get limit orders by address", error));
    }

    @Override
    public GetLimitOrdersV4Response getOrderByOrderHash(Integer chainId, String orderHash) throws OneInchException {
        log.info("Getting order by hash {} on chain {}", orderHash, chainId);
        try {
            return getOrderByOrderHashRx(chainId, orderHash).blockingGet();
        } catch (Exception e) {
            log.error("Failed to get order by hash", e);
            throw new OneInchException("Failed to get order by hash", e);
        }
    }

    @Override
    public CompletableFuture<GetLimitOrdersV4Response> getOrderByOrderHashAsync(Integer chainId, String orderHash) {
        return getOrderByOrderHashRx(chainId, orderHash).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<GetLimitOrdersV4Response> getOrderByOrderHashRx(Integer chainId, String orderHash) {
        log.info("Getting order (reactive) by hash {} on chain {}", orderHash, chainId);
        return apiService.getOrderByOrderHash(chainId, orderHash)
                .doOnSuccess(order -> log.debug("Retrieved order: {}", order.getOrderHash()))
                .doOnError(error -> log.error("Failed to get order by hash", error));
    }

    @Override
    public List<GetLimitOrdersV4Response> getAllLimitOrders(GetAllLimitOrdersRequest request) throws OneInchException {
        log.info("Getting all limit orders on chain {} with page {} and limit {}", 
                request.getChainId(), request.getPage(), request.getLimit());
        try {
            return getAllLimitOrdersRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Failed to get all limit orders", e);
            throw new OneInchException("Failed to get all limit orders", e);
        }
    }

    @Override
    public CompletableFuture<List<GetLimitOrdersV4Response>> getAllLimitOrdersAsync(GetAllLimitOrdersRequest request) {
        return getAllLimitOrdersRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<GetLimitOrdersV4Response>> getAllLimitOrdersRx(GetAllLimitOrdersRequest request) {
        log.info("Getting all limit orders (reactive) on chain {} with page {} and limit {}", 
                request.getChainId(), request.getPage(), request.getLimit());
        return apiService.getAllLimitOrders(
                        request.getChainId(),
                        request.getPage(),
                        request.getLimit(),
                        request.getStatuses(),
                        request.getSortBy(),
                        request.getTakerAsset(),
                        request.getMakerAsset()
                )
                .doOnSuccess(orders -> log.debug("Retrieved {} limit orders", orders.size()))
                .doOnError(error -> log.error("Failed to get all limit orders", error));
    }

    @Override
    public GetLimitOrdersCountV4Response getOrdersCount(GetLimitOrdersCountRequest request) throws OneInchException {
        log.info("Getting orders count on chain {} with statuses {}", request.getChainId(), request.getStatuses());
        try {
            return getOrdersCountRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Failed to get orders count", e);
            throw new OneInchException("Failed to get orders count", e);
        }
    }

    @Override
    public CompletableFuture<GetLimitOrdersCountV4Response> getOrdersCountAsync(GetLimitOrdersCountRequest request) {
        return getOrdersCountRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<GetLimitOrdersCountV4Response> getOrdersCountRx(GetLimitOrdersCountRequest request) {
        log.info("Getting orders count (reactive) on chain {} with statuses {}", request.getChainId(), request.getStatuses());
        return apiService.getOrdersCount(
                        request.getChainId(),
                        request.getStatuses(),
                        request.getTakerAsset(),
                        request.getMakerAsset()
                )
                .doOnSuccess(response -> log.debug("Orders count: {}", response.getCount()))
                .doOnError(error -> log.error("Failed to get orders count", error));
    }

    @Override
    public Map<String, List<GetEventsV4Response>> getEventsByOrderHash(Integer chainId, String orderHash) throws OneInchException {
        log.info("Getting events by order hash {} on chain {}", orderHash, chainId);
        try {
            return getEventsByOrderHashRx(chainId, orderHash).blockingGet();
        } catch (Exception e) {
            log.error("Failed to get events by order hash", e);
            throw new OneInchException("Failed to get events by order hash", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, List<GetEventsV4Response>>> getEventsByOrderHashAsync(Integer chainId, String orderHash) {
        return getEventsByOrderHashRx(chainId, orderHash).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<Map<String, List<GetEventsV4Response>>> getEventsByOrderHashRx(Integer chainId, String orderHash) {
        log.info("Getting events (reactive) by order hash {} on chain {}", orderHash, chainId);
        return apiService.getEventsByOrderHash(chainId, orderHash)
                .doOnSuccess(events -> log.debug("Retrieved events for {} orders", events.size()))
                .doOnError(error -> log.error("Failed to get events by order hash", error));
    }

    @Override
    public List<GetEventsV4Response> getAllEvents(GetEventsRequest request) throws OneInchException {
        log.info("Getting all events on chain {} with limit {}", request.getChainId(), request.getLimit());
        try {
            return getAllEventsRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Failed to get all events", e);
            throw new OneInchException("Failed to get all events", e);
        }
    }

    @Override
    public CompletableFuture<List<GetEventsV4Response>> getAllEventsAsync(GetEventsRequest request) {
        return getAllEventsRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<List<GetEventsV4Response>> getAllEventsRx(GetEventsRequest request) {
        log.info("Getting all events (reactive) on chain {} with limit {}", request.getChainId(), request.getLimit());
        return apiService.getAllEvents(request.getChainId(), request.getLimit())
                .doOnSuccess(events -> log.debug("Retrieved {} events", events.size()))
                .doOnError(error -> log.error("Failed to get all events", error));
    }

    @Override
    public GetHasActiveOrdersWithPermitV4Response hasActiveOrdersWithPermit(HasActiveOrdersWithPermitRequest request) throws OneInchException {
        log.info("Checking active orders with permit for wallet {} and token {} on chain {}", 
                request.getWalletAddress(), request.getToken(), request.getChainId());
        try {
            return hasActiveOrdersWithPermitRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Failed to check active orders with permit", e);
            throw new OneInchException("Failed to check active orders with permit", e);
        }
    }

    @Override
    public CompletableFuture<GetHasActiveOrdersWithPermitV4Response> hasActiveOrdersWithPermitAsync(HasActiveOrdersWithPermitRequest request) {
        return hasActiveOrdersWithPermitRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<GetHasActiveOrdersWithPermitV4Response> hasActiveOrdersWithPermitRx(HasActiveOrdersWithPermitRequest request) {
        log.info("Checking active orders with permit (reactive) for wallet {} and token {} on chain {}", 
                request.getWalletAddress(), request.getToken(), request.getChainId());
        return apiService.hasActiveOrdersWithPermit(request.getChainId(), request.getWalletAddress(), request.getToken())
                .doOnSuccess(response -> log.debug("Has active orders with permit: {}", response.isResult()))
                .doOnError(error -> log.error("Failed to check active orders with permit", error));
    }

    @Override
    public GetActiveUniquePairsResponse getUniqueActivePairs(GetUniqueActivePairsRequest request) throws OneInchException {
        log.info("Getting unique active pairs on chain {} with page {} and limit {}", 
                request.getChainId(), request.getPage(), request.getLimit());
        try {
            return getUniqueActivePairsRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Failed to get unique active pairs", e);
            throw new OneInchException("Failed to get unique active pairs", e);
        }
    }

    @Override
    public CompletableFuture<GetActiveUniquePairsResponse> getUniqueActivePairsAsync(GetUniqueActivePairsRequest request) {
        return getUniqueActivePairsRx(request).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Single<GetActiveUniquePairsResponse> getUniqueActivePairsRx(GetUniqueActivePairsRequest request) {
        log.info("Getting unique active pairs (reactive) on chain {} with page {} and limit {}", 
                request.getChainId(), request.getPage(), request.getLimit());
        return apiService.getUniqueActivePairs(request.getChainId(), request.getPage(), request.getLimit())
                .doOnSuccess(response -> log.debug("Retrieved {} unique active pairs", response.getItems().size()))
                .doOnError(error -> log.error("Failed to get unique active pairs", error));
    }
}