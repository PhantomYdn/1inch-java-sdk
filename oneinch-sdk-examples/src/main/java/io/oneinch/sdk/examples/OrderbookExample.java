package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.Meta;
import io.oneinch.sdk.model.orderbook.*;
import io.oneinch.sdk.service.OrderbookService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OrderbookExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private static final String USDC_ADDRESS = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48";
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    
    public static void main(String[] args) {
        OrderbookExample example = new OrderbookExample();
        
        try {
            log.info("=== Running Orderbook API Examples ===");
            example.runGetAllOrdersExample();
            example.runGetOrdersCountExample();
            example.runGetOrdersByAddressExample();
            example.runGetAllEventsExample();
            example.runGetUniqueActivePairsExample();
            example.runReactiveOrderbookExample();
            
        } catch (Exception e) {
            log.error("Orderbook example failed", e);
        }
    }

    /**
     * Demonstrates getting all limit orders with filtering
     */
    public void runGetAllOrdersExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Get All Orders Example ===");
            
            OrderbookService orderbookService = client.orderbook();
            
            // Get first 10 valid orders
            GetAllLimitOrdersRequest request = GetAllLimitOrdersRequest.builder()
                    .chainId(1) // Ethereum
                    .page(1)
                    .limit(10)
                    .statuses("1") // Only valid orders
                    .sortBy("createDateTime")
                    .build();
            
            try {
                List<GetLimitOrdersV4Response> orders = orderbookService.getAllLimitOrders(request);
                
                log.info("Retrieved {} limit orders", orders.size());
                
                orders.forEach(order -> {
                    log.info("Order:");
                    log.info("  Hash: {}", order.getOrderHash());
                    log.info("  Maker: {} -> Taker: {}", 
                            order.getData().getMakerAsset(), 
                            order.getData().getTakerAsset());
                    log.info("  Making Amount: {}", order.getData().getMakingAmount());
                    log.info("  Taking Amount: {}", order.getData().getTakingAmount());
                    log.info("  Remaining: {}", order.getRemainingMakerAmount());
                    log.info("  Created: {}", order.getCreateDateTime());
                    log.info("  ---");
                });
                
            } catch (OneInchException e) {
                log.warn("Could not retrieve orders (this might be expected): {}", e.getMessage());
            }
        }
    }

    /**
     * Demonstrates getting the count of orders with filters
     */
    public void runGetOrdersCountExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Get Orders Count Example ===");
            
            OrderbookService orderbookService = client.orderbook();
            
            // Get count of all orders
            GetLimitOrdersCountRequest request = GetLimitOrdersCountRequest.builder()
                    .chainId(1) // Ethereum
                    .statuses("1,2,3") // All statuses
                    .build();
            
            try {
                GetLimitOrdersCountV4Response response = orderbookService.getOrdersCount(request);
                log.info("Total orders count: {}", response.getCount());
                
                // Get count of valid orders only
                GetLimitOrdersCountRequest validOrdersRequest = GetLimitOrdersCountRequest.builder()
                        .chainId(1)
                        .statuses("1") // Only valid orders
                        .build();
                
                GetLimitOrdersCountV4Response validResponse = orderbookService.getOrdersCount(validOrdersRequest);
                log.info("Valid orders count: {}", validResponse.getCount());
                
            } catch (OneInchException e) {
                log.warn("Could not get orders count (this might be expected): {}", e.getMessage());
            }
        }
    }

    /**
     * Demonstrates getting orders for a specific address
     */
    public void runGetOrdersByAddressExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Get Orders by Address Example ===");
            
            OrderbookService orderbookService = client.orderbook();
            
            // Get orders for a specific wallet address
            GetLimitOrdersRequest request = GetLimitOrdersRequest.builder()
                    .chainId(1) // Ethereum
                    .address(WALLET_ADDRESS)
                    .page(1)
                    .limit(5)
                    .statuses("1,2") // Valid and temporarily invalid orders
                    .build();
            
            try {
                List<GetLimitOrdersV4Response> orders = orderbookService.getLimitOrdersByAddress(request);
                
                log.info("Retrieved {} orders for address {}", orders.size(), WALLET_ADDRESS);
                
                if (!orders.isEmpty()) {
                    orders.forEach(order -> {
                        log.info("User order:");
                        log.info("  Hash: {}", order.getOrderHash());
                        log.info("  Maker Asset: {}", order.getData().getMakerAsset());
                        log.info("  Taker Asset: {}", order.getData().getTakerAsset());
                        log.info("  Status: {}", order.getOrderInvalidReason() == null ? "Valid" : "Invalid");
                    });
                } else {
                    log.info("No orders found for this address");
                }
                
            } catch (OneInchException e) {
                log.warn("Could not retrieve orders for address (this might be expected): {}", e.getMessage());
            }
        }
    }

    /**
     * Demonstrates getting all order events (fills, cancellations, etc.)
     */
    public void runGetAllEventsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Get All Events Example ===");
            
            OrderbookService orderbookService = client.orderbook();
            
            // Get recent events
            GetEventsRequest request = GetEventsRequest.builder()
                    .chainId(1) // Ethereum
                    .limit(10)
                    .build();
            
            try {
                List<GetEventsV4Response> events = orderbookService.getAllEvents(request);
                
                log.info("Retrieved {} recent events", events.size());
                
                events.forEach(event -> {
                    log.info("Event:");
                    log.info("  Action: {}", event.getAction());
                    log.info("  Order Hash: {}", event.getOrderHash());
                    log.info("  Taker: {}", event.getTaker());
                    log.info("  Block Number: {}", event.getBlockNumber());
                    log.info("  Transaction: {}", event.getTransactionHash());
                    log.info("  Created: {}", event.getCreateDateTime());
                    log.info("  ---");
                });
                
            } catch (OneInchException e) {
                log.warn("Could not retrieve events (this might be expected): {}", e.getMessage());
            }
        }
    }

    /**
     * Demonstrates getting unique active trading pairs
     */
    public void runGetUniqueActivePairsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Get Unique Active Pairs Example ===");
            
            OrderbookService orderbookService = client.orderbook();
            
            // Get active trading pairs
            GetUniqueActivePairsRequest request = GetUniqueActivePairsRequest.builder()
                    .chainId(1) // Ethereum
                    .page(1)
                    .limit(10)
                    .build();
            
            try {
                GetActiveUniquePairsResponse response = orderbookService.getUniqueActivePairs(request);
                
                log.info("Active trading pairs:");
                log.info("  Total pairs: {}", response.getMeta().getTotalItems());
                log.info("  Current page: {}", response.getMeta().getCurrentPage());
                log.info("  Items per page: {}", response.getMeta().getItemsPerPage());
                
                response.getItems().forEach(pair -> {
                    log.info("  Trading pair: {} <-> {}", pair.getMakerAsset(), pair.getTakerAsset());
                });
                
            } catch (OneInchException e) {
                log.warn("Could not retrieve active pairs (this might be expected): {}", e.getMessage());
            }
        }
    }

    /**
     * Demonstrates creating a limit order (requires proper signing)
     * Note: This is a demonstration of the API structure - actual order creation 
     * requires proper cryptographic signing which is beyond the scope of this example
     */
    public void runCreateLimitOrderExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Create Limit Order Example (Demo) ===");
            
            OrderbookService orderbookService = client.orderbook();
            
            // Create order data (this would need proper values and signing in real usage)
            LimitOrderV4Data orderData = LimitOrderV4Data.builder()
                    .makerAsset(ETH_ADDRESS)
                    .takerAsset(USDC_ADDRESS)
                    .maker(WALLET_ADDRESS)
                    .makingAmount("1000000000000000000") // 1 ETH
                    .takingAmount("3000000000") // 3000 USDC (6 decimals)
                    .salt("1234567890")
                    .extension("0x")
                    .makerTraits("0")
                    .build();
            
            // Create order request (this would need proper signature in real usage)
            LimitOrderV4Request request = LimitOrderV4Request.builder()
                    .chainId(1) // Ethereum
                    .orderHash("0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef")
                    .signature("0x1b1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1c")
                    .data(orderData)
                    .build();
            
            log.info("Demo order structure:");
            log.info("  Maker Asset: {} (ETH)", orderData.getMakerAsset());
            log.info("  Taker Asset: {} (USDC)", orderData.getTakerAsset());
            log.info("  Making Amount: {} wei (1 ETH)", orderData.getMakingAmount());
            log.info("  Taking Amount: {} (3000 USDC)", orderData.getTakingAmount());
            log.info("  NOTE: This is just a demo structure - real orders need proper signing!");
            
            // Note: We don't actually call createLimitOrder here as it would fail 
            // without proper cryptographic signing
        }
    }

    /**
     * Demonstrates reactive orderbook operations with parallel execution
     */
    public void runReactiveOrderbookExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Reactive Orderbook Example ===");
            
            OrderbookService orderbookService = client.orderbook();

            // Run multiple orderbook operations in parallel
            Single<GetLimitOrdersCountV4Response> ordersCount = orderbookService.getOrdersCountRx(
                    GetLimitOrdersCountRequest.builder()
                            .chainId(1)
                            .statuses("1")
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(count -> log.info("Orders count loaded: {}", count.getCount()))
             .doOnError(error -> log.warn("Orders count failed: {}", error.getMessage()))
             .onErrorReturn(error -> {
                 GetLimitOrdersCountV4Response fallback = new GetLimitOrdersCountV4Response();
                 fallback.setCount(0L);
                 return fallback;
             });

            Single<List<GetEventsV4Response>> events = orderbookService.getAllEventsRx(
                    GetEventsRequest.builder()
                            .chainId(1)
                            .limit(5)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(eventList -> log.info("Events loaded: {}", eventList.size()))
             .doOnError(error -> log.warn("Events loading failed: {}", error.getMessage()))
             .onErrorReturn(error -> List.of());

            Single<GetActiveUniquePairsResponse> pairs = orderbookService.getUniqueActivePairsRx(
                    GetUniqueActivePairsRequest.builder()
                            .chainId(1)
                            .page(1)
                            .limit(3)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(pairResponse -> log.info("Active pairs loaded: {}", pairResponse.getItems().size()))
             .doOnError(error -> log.warn("Active pairs loading failed: {}", error.getMessage()))
             .onErrorReturn(error -> {
                 GetActiveUniquePairsResponse fallback = new GetActiveUniquePairsResponse();
                 fallback.setItems(List.of());
                 Meta meta = new Meta();
                 meta.setTotalItems(0L);
                 fallback.setMeta(meta);
                 return fallback;
             });

            // Combine all results
            Single.zip(ordersCount, events, pairs,
                    (count, eventList, pairResponse) -> {
                        log.info("All reactive orderbook operations completed:");
                        log.info("  Total orders: {}", count.getCount());
                        log.info("  Recent events: {}", eventList.size());
                        log.info("  Active pairs: {}", pairResponse.getItems().size());
                        return "All operations completed";
                    })
                    .timeout(15, TimeUnit.SECONDS)
                    .blockingGet();
        }
    }

    /**
     * Demonstrates async orderbook operations with CompletableFuture
     */
    public void runAsyncOrderbookExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Async Orderbook Example ===");
            
            OrderbookService orderbookService = client.orderbook();

            // Start multiple async operations with error handling
            CompletableFuture<GetLimitOrdersCountV4Response> countFuture = 
                    orderbookService.getOrdersCountAsync(
                            GetLimitOrdersCountRequest.builder()
                                    .chainId(1)
                                    .statuses("1,2,3")
                                    .build()
                    ).exceptionally(throwable -> {
                        log.warn("Count async failed: {}", throwable.getMessage());
                        GetLimitOrdersCountV4Response fallback = new GetLimitOrdersCountV4Response();
                        fallback.setCount(0L);
                        return fallback;
                    });

            CompletableFuture<List<GetEventsV4Response>> eventsFuture = 
                    orderbookService.getAllEventsAsync(
                            GetEventsRequest.builder()
                                    .chainId(1)
                                    .limit(3)
                                    .build()
                    ).exceptionally(throwable -> {
                        log.warn("Events async failed: {}", throwable.getMessage());
                        return List.of();
                    });

            // Wait for all to complete and combine results
            CompletableFuture.allOf(countFuture, eventsFuture)
                    .thenRun(() -> {
                        try {
                            GetLimitOrdersCountV4Response count = countFuture.get();
                            List<GetEventsV4Response> events = eventsFuture.get();

                            log.info("Async orderbook operations completed:");
                            log.info("  Orders count: {}", count.getCount());
                            log.info("  Events retrieved: {}", events.size());
                        } catch (Exception e) {
                            log.error("Error in async completion", e);
                        }
                    })
                    .get(10, TimeUnit.SECONDS);
        }
    }

    /**
     * Demonstrates comprehensive error handling for orderbook operations
     */
    public void runOrderbookErrorHandlingExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Orderbook Error Handling Example ===");
            
            OrderbookService orderbookService = client.orderbook();
            
            // Try to get order by invalid hash (reactive with error handling)
            orderbookService.getOrderByOrderHashRx(1, "invalid_hash")
                    .doOnSuccess(order -> log.info("This shouldn't happen: {}", order))
                    .doOnError(error -> {
                        log.info("Expected error for invalid order hash:");
                        if (error instanceof OneInchException) {
                            log.error("  SDK Error: {}", error.getMessage());
                        } else {
                            log.error("  Unexpected error: {}", error.getMessage());
                        }
                    })
                    .onErrorReturn(error -> {
                        // Fallback order
                        GetLimitOrdersV4Response fallback = new GetLimitOrdersV4Response();
                        fallback.setOrderHash("not_found");
                        return fallback;
                    })
                    .subscribe(
                        order -> log.info("Final result (with fallback): {}", order.getOrderHash()),
                        error -> log.error("This shouldn't happen with fallback", error)
                    );
            
            // Wait a bit for async operation
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            log.error("Error handling example failed", e);
        }
    }
}