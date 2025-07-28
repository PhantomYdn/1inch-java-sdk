package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.*;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive example demonstrating the 1inch Fusion API functionality.
 * Fusion enables gasless swaps through professional market makers (resolvers).
 * 
 * This example covers:
 * - Getting quotes with different presets (fast/medium/slow/custom)
 * - Submitting orders to the Fusion network
 * - Tracking order status and history
 * - Using all three programming models (Reactive/Async/Sync)
 */
@Slf4j
public class FusionExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    
    // Ethereum mainnet
    private static final Integer CHAIN_ID = 1;
    
    // Token addresses
    private static final String USDC_ADDRESS = "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7";
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private static final String ONEINCH_TOKEN_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302";
    
    // User wallet address
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    
    // Sample amounts
    private static final String SWAP_AMOUNT_USDC = "1000000000"; // 1000 USDC (6 decimals)
    private static final String SWAP_AMOUNT_ETH = "500000000000000000"; // 0.5 ETH (18 decimals)
    
    public static void main(String[] args) {
        FusionExample example = new FusionExample();
        
        try {
            log.info("=== 1inch Fusion API Example Started ===");
            
            // Comprehensive Fusion workflow examples
            example.runReactiveFusionWorkflow();
            example.runSynchronousFusionWorkflow();
            example.runParallelFusionOperations();
            example.runCustomPresetExample();
            example.runOrderTrackingExample();
            
            log.info("=== 1inch Fusion API Example Completed ===");
            
        } catch (Exception e) {
            log.error("Fusion example failed", e);
        }
    }
    
    /**
     * Demonstrates complete Fusion workflow using reactive programming (RECOMMENDED)
     */
    public void runReactiveFusionWorkflow() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Reactive Fusion Workflow ===");
            
            // Build quote request for USDC -> ETH swap
            FusionQuoteRequest quoteRequest = FusionQuoteRequest.builder()
                    .chainId(CHAIN_ID)
                    .fromTokenAddress(USDC_ADDRESS)
                    .toTokenAddress(ETH_ADDRESS)
                    .amount(SWAP_AMOUNT_USDC)
                    .walletAddress(WALLET_ADDRESS)
                    .enableEstimate(true)
                    .fee(100) // 1% fee in basis points
                    .showDestAmountMinusFee(true)
                    .surplus(true)
                    .build();
            
            // Reactive chain: Quote -> Order Creation -> Order Submission -> Status Tracking
            client.fusionQuoter().getQuoteRx(quoteRequest)
                .doOnSuccess(quote -> {
                    log.info("Quote received:");
                    log.info("  Quote ID: {}", quote.getQuoteId());
                    log.info("  From Amount: {} USDC", quote.getFromTokenAmount());
                    log.info("  To Amount: {} ETH", quote.getToTokenAmount());
                    log.info("  Recommended Preset: {}", quote.getRecommendedPreset());
                    log.info("  Settlement Address: {}", quote.getSettlementAddress());
                    log.info("  Suggested: {}", quote.getSuggested());
                    
                    if (quote.getPresets() != null) {
                        log.info("  Available Presets:");
                        if (quote.getPresets().getFast() != null) {
                            log.info("    Fast: {} seconds auction", quote.getPresets().getFast().getAuctionDuration());
                        }
                        if (quote.getPresets().getMedium() != null) {
                            log.info("    Medium: {} seconds auction", quote.getPresets().getMedium().getAuctionDuration());
                        }
                        if (quote.getPresets().getSlow() != null) {
                            log.info("    Slow: {} seconds auction", quote.getPresets().getSlow().getAuctionDuration());
                        }
                    }
                })
                .flatMap(quote -> {
                    // Create order based on quote
                    OrderInput order = OrderInput.builder()
                            .salt("0x" + System.currentTimeMillis()) // Unique salt
                            .maker(WALLET_ADDRESS)
                            .receiver(WALLET_ADDRESS)
                            .makerAsset(USDC_ADDRESS)
                            .takerAsset(ETH_ADDRESS)
                            .makingAmount(SWAP_AMOUNT_USDC)
                            .takingAmount(SWAP_AMOUNT_ETH)
                            .makerTraits("0x0") // Default traits
                            .build();
                    
                    // Sign order (in real implementation, you'd use a proper signing mechanism)
                    SignedOrderInput signedOrder = SignedOrderInput.builder()
                            .order(order)
                            .signature("0x" + "0".repeat(130)) // Mock signature - use proper signing in production
                            .extension("0x") // Optional extension data
                            .quoteId(quote.getQuoteId())
                            .build();
                    
                    log.info("Submitting order to Fusion network...");
                    return client.fusionRelayer().submitOrderRx(CHAIN_ID, signedOrder);
                })
                .doOnSuccess(result -> log.info("Order submitted successfully to Fusion network!"))
                .flatMap(result -> {
                    // Get active orders to verify submission
                    FusionActiveOrdersRequest activeOrdersRequest = FusionActiveOrdersRequest.builder()
                            .chainId(CHAIN_ID)
                            .page(1)
                            .limit(10)
                            .version("2.0")
                            .build();
                    
                    return client.fusionOrders().getActiveOrdersRx(activeOrdersRequest);
                })
                .doOnSuccess(activeOrders -> {
                    log.info("Active orders retrieved:");
                    log.info("  Total items: {}", activeOrders.getMeta().getTotalItems());
                    log.info("  Current page: {}", activeOrders.getMeta().getCurrentPage());
                    
                    if (activeOrders.getItems() != null && !activeOrders.getItems().isEmpty()) {
                        activeOrders.getItems().forEach(order -> {
                            log.info("  Order Hash: {}", order.getOrderHash());
                            log.info("  Remaining Amount: {}", order.getRemainingMakerAmount());
                            log.info("  Auction End: {}", order.getAuctionEndDate());
                        });
                    }
                })
                .doOnError(error -> {
                    if (error instanceof OneInchApiException) {
                        OneInchApiException apiError = (OneInchApiException) error;
                        log.error("Fusion API Error: {} - {}", apiError.getError(), apiError.getDescription());
                    } else {
                        log.error("Unexpected error in Fusion workflow", error);
                    }
                })
                .subscribe(
                    result -> log.info("Reactive Fusion workflow completed successfully!"),
                    error -> log.error("Reactive Fusion workflow failed", error)
                );
            
            // Wait for async operations
            Thread.sleep(3000);
        }
    }
    
    /**
     * Demonstrates synchronous Fusion operations
     */
    public void runSynchronousFusionWorkflow() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Synchronous Fusion Workflow ===");
            
            // Step 1: Get settlement contract address
            log.info("Step 1: Getting settlement contract address...");
            SettlementAddressOutput settlement = client.fusionOrders().getSettlementContract(CHAIN_ID);
            log.info("Settlement contract: {}", settlement.getAddress());
            
            // Step 2: Get quote (synchronous)
            log.info("Step 2: Getting Fusion quote...");
            FusionQuoteRequest quoteRequest = FusionQuoteRequest.builder()
                    .chainId(CHAIN_ID)
                    .fromTokenAddress(ETH_ADDRESS)
                    .toTokenAddress(ONEINCH_TOKEN_ADDRESS)
                    .amount(SWAP_AMOUNT_ETH)
                    .walletAddress(WALLET_ADDRESS)
                    .enableEstimate(true)
                    .surplus(false)
                    .build();
            
            GetQuoteOutput quote = client.fusionQuoter().getQuote(quoteRequest);
            log.info("Quote Details:");
            log.info("  Quote ID: {}", quote.getQuoteId());
            log.info("  From Amount: {} ETH", quote.getFromTokenAmount());
            log.info("  To Amount: {} 1INCH", quote.getToTokenAmount());
            log.info("  Recommended Preset: {}", quote.getRecommendedPreset());
            
            // Step 3: Get order history (asynchronous with CompletableFuture)
            log.info("Step 3: Getting order history (async)...");
            FusionOrderHistoryRequest historyRequest = FusionOrderHistoryRequest.builder()
                    .chainId(CHAIN_ID)
                    .address(WALLET_ADDRESS)
                    .page(1)
                    .limit(5)
                    .build();
            
            CompletableFuture<OrderFillsByMakerOutput> historyFuture = 
                    client.fusionOrders().getOrdersByMakerAsync(historyRequest);
            
            OrderFillsByMakerOutput history = historyFuture.get(10, TimeUnit.SECONDS);
            log.info("Order History:");
            log.info("  Receiver: {}", history.getReceiver());
            log.info("  Status: {}", history.getStatus());
            log.info("  Order Hash: {}", history.getOrderHash());
            
            log.info("=== Synchronous Fusion Workflow Complete ===");
        }
    }
    
    /**
     * Demonstrates parallel Fusion operations using reactive programming
     */
    public void runParallelFusionOperations() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Parallel Fusion Operations ===");
            
            // Run multiple operations in parallel
            Single<SettlementAddressOutput> settlementSingle = 
                    client.fusionOrders().getSettlementContractRx(CHAIN_ID)
                            .subscribeOn(Schedulers.io())
                            .doOnSuccess(settlement -> 
                                    log.info("Parallel: Got settlement address {}", settlement.getAddress()));
            
            Single<GetQuoteOutput> quoteSingle = client.fusionQuoter().getQuoteRx(
                    FusionQuoteRequest.builder()
                            .chainId(CHAIN_ID)
                            .fromTokenAddress(USDC_ADDRESS)
                            .toTokenAddress(ETH_ADDRESS)
                            .amount(SWAP_AMOUNT_USDC)
                            .walletAddress(WALLET_ADDRESS)
                            .enableEstimate(true)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(quote -> log.info("Parallel: Got quote ID {}", quote.getQuoteId()));
            
            Single<GetActiveOrdersOutput> activeOrdersSingle = 
                    client.fusionOrders().getActiveOrdersRx(
                            FusionActiveOrdersRequest.builder()
                                    .chainId(CHAIN_ID)
                                    .page(1)
                                    .limit(5)
                                    .build()
                    ).subscribeOn(Schedulers.io())
                     .doOnSuccess(orders -> 
                             log.info("Parallel: Got {} active orders", orders.getMeta().getTotalItems()));
            
            // Combine all results
            Single.zip(settlementSingle, quoteSingle, activeOrdersSingle,
                    (settlement, quote, activeOrders) -> {
                        log.info("All parallel Fusion operations completed:");
                        log.info("  Settlement: {}", settlement.getAddress());
                        log.info("  Quote ID: {}", quote.getQuoteId());
                        log.info("  Active Orders: {}", activeOrders.getMeta().getTotalItems());
                        return "Parallel Success";
                    })
                    .timeout(15, TimeUnit.SECONDS)
                    .blockingGet();
        }
    }
    
    /**
     * Demonstrates custom preset configuration for advanced users
     */
    public void runCustomPresetExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Custom Preset Example ===");
            
            // Create custom preset with specific auction parameters
            CustomPresetInput customPreset = CustomPresetInput.builder()
                    .auctionDuration(1800) // 30 minutes auction
                    .auctionStartAmount(520000000000000000L) // 0.52 ETH start
                    .auctionEndAmount(480000000000000000L) // 0.48 ETH end (better rate)
                    .points(Arrays.asList(
                            "0:1000",   // Start: 100% rate bump
                            "900:500",  // 15 min: 50% rate bump  
                            "1800:0"    // End: 0% rate bump (best rate)
                    ))
                    .build();
            
            FusionQuoteRequest customQuoteRequest = FusionQuoteRequest.builder()
                    .chainId(CHAIN_ID)
                    .fromTokenAddress(USDC_ADDRESS)
                    .toTokenAddress(ETH_ADDRESS)
                    .amount(SWAP_AMOUNT_USDC)
                    .walletAddress(WALLET_ADDRESS)
                    .enableEstimate(true)
                    .surplus(true)
                    .build();
            
            // Get quote with custom preset
            client.fusionQuoter().getQuoteWithCustomPresetsRx(customQuoteRequest, customPreset)
                    .doOnSuccess(quote -> {
                        log.info("Custom Preset Quote:");
                        log.info("  Quote ID: {}", quote.getQuoteId());
                        log.info("  Recommended Preset: {}", quote.getRecommendedPreset());
                        
                        if (quote.getPresets() != null && quote.getPresets().getCustom() != null) {
                            PresetClass custom = quote.getPresets().getCustom();
                            log.info("  Custom Preset Details:");
                            log.info("    Auction Duration: {} seconds", custom.getAuctionDuration());
                            log.info("    Start Amount: {}", custom.getAuctionStartAmount());
                            log.info("    End Amount: {}", custom.getAuctionEndAmount());
                            log.info("    Estimated Probability: {}", custom.getEstP());
                            log.info("    Allow Partial Fills: {}", custom.getAllowPartialFills());
                            log.info("    Allow Multiple Fills: {}", custom.getAllowMultipleFills());
                        }
                    })
                    .doOnError(error -> log.error("Custom preset quote failed", error))
                    .blockingGet();
            
            log.info("=== Custom Preset Example Complete ===");
        }
    }
    
    /**
     * Demonstrates order tracking and status monitoring
     */
    public void runOrderTrackingExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Order Tracking Example ===");
            
            // Sample order hash for demonstration (in practice, you'd get this from order submission)
            String sampleOrderHash = "0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559";
            
            // Track specific order by hash
            client.fusionOrders().getOrderByOrderHashRx(CHAIN_ID, sampleOrderHash)
                    .doOnSuccess(orderStatus -> {
                        log.info("Order Status Tracking:");
                        log.info("  Order Hash: {}", orderStatus.getOrderHash());
                        log.info("  Status: {}", orderStatus.getStatus());
                        log.info("  Approximate Taking Amount: {}", orderStatus.getApproximateTakingAmount());
                        log.info("  Auction Start: {}", orderStatus.getAuctionStartDate());
                        log.info("  Auction Duration: {} ms", orderStatus.getAuctionDuration());
                        log.info("  Initial Rate Bump: {}", orderStatus.getInitialRateBump());
                        log.info("  Positive Surplus: {}", orderStatus.getPositiveSurplus());
                        log.info("  Native Currency: {}", orderStatus.getIsNativeCurrency());
                        log.info("  Version: {}", orderStatus.getVersion());
                        
                        if (orderStatus.getOrder() != null) {
                            log.info("  Order Details:");
                            log.info("    Maker: {}", orderStatus.getOrder().getMaker());
                            log.info("    Maker Asset: {}", orderStatus.getOrder().getMakerAsset());
                            log.info("    Taker Asset: {}", orderStatus.getOrder().getTakerAsset());
                            log.info("    Making Amount: {}", orderStatus.getOrder().getMakingAmount());
                            log.info("    Taking Amount: {}", orderStatus.getOrder().getTakingAmount());
                        }
                        
                        if (orderStatus.getFills() != null && !orderStatus.getFills().isEmpty()) {
                            log.info("  Order Fills: {}", orderStatus.getFills().size());
                        } else {
                            log.info("  Order Fills: None yet");
                        }
                    })
                    .doOnError(error -> {
                        log.info("Order not found or error occurred (expected for demo): {}", error.getMessage());
                    })
                    .onErrorComplete() // Continue execution even if order not found
                    .ignoreElement()
                    .andThen(
                        // Get order history for the wallet
                        client.fusionOrders().getOrdersByMakerRx(
                                FusionOrderHistoryRequest.builder()
                                        .chainId(CHAIN_ID)
                                        .address(WALLET_ADDRESS)
                                        .page(1)
                                        .limit(10)
                                        .build()
                        )
                    )
                    .doOnSuccess(history -> {
                        log.info("Order History for Wallet:");
                        log.info("  Maker Address: {}", history.getReceiver());
                        log.info("  Latest Order Hash: {}", history.getOrderHash());
                        log.info("  Status: {}", history.getStatus());
                        log.info("  Maker Asset: {}", history.getMakerAsset());
                        log.info("  Maker Amount: {}", history.getMakerAmount());
                        log.info("  Min Taker Amount: {}", history.getMinTakerAmount());
                        log.info("  Version: {}", history.getVersion());
                    })
                    .doOnError(error -> log.info("No order history found (expected for demo): {}", error.getMessage()))
                    .onErrorComplete()
                    .subscribe(
                        result -> log.info("Order tracking example completed"),
                        error -> log.error("Order tracking failed", error)
                    );
            
            // Wait for async operations
            Thread.sleep(2000);
            
            log.info("=== Order Tracking Example Complete ===");
        }
    }
    
    /**
     * Demonstrates comprehensive error handling for Fusion API
     */
    public void runErrorHandlingExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Fusion Error Handling Example ===");
            
            // Invalid quote request to trigger an error
            FusionQuoteRequest invalidRequest = FusionQuoteRequest.builder()
                    .chainId(CHAIN_ID)
                    .fromTokenAddress("invalid_address")
                    .toTokenAddress("invalid_address")
                    .amount("0")
                    .walletAddress("invalid_wallet")
                    .enableEstimate(false)
                    .build();
            
            client.fusionQuoter().getQuoteRx(invalidRequest)
                    .doOnSuccess(quote -> log.info("This shouldn't happen"))
                    .doOnError(error -> {
                        log.info("Fusion error handling:");
                        if (error instanceof OneInchApiException) {
                            OneInchApiException apiError = (OneInchApiException) error;
                            log.error("  API Error: {}", apiError.getError());
                            log.error("  Description: {}", apiError.getDescription());
                            log.error("  Status Code: {}", apiError.getStatusCode());
                        } else if (error instanceof OneInchException) {
                            log.error("  SDK Error: {}", error.getMessage());
                        } else {
                            log.error("  Unexpected error: {}", error.getMessage());
                        }
                    })
                    .onErrorReturn(error -> {
                        // Fallback quote
                        return GetQuoteOutput.builder()
                                .quoteId("fallback_quote")
                                .fromTokenAmount("0")
                                .toTokenAmount("0")
                                .recommendedPreset(PresetType.FAST)
                                .suggested(false)
                                .build();
                    })
                    .subscribe(
                        quote -> log.info("Final result (with fallback): Quote ID {}", quote.getQuoteId()),
                        error -> log.error("This shouldn't happen with fallback", error)
                    );
            
            log.info("=== Fusion Error Handling Example Complete ===");
        } catch (Exception e) {
            log.error("Unexpected error in Fusion error handling example", e);
        }
    }
}