package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive example demonstrating the 1inch FusionPlus API functionality.
 * FusionPlus enables cross-chain gasless swaps with enhanced security through escrow mechanisms.
 * 
 * This example covers:
 * - Getting cross-chain quotes with different presets
 * - Building cross-chain orders with EIP712 typed data
 * - Submitting cross-chain orders and secrets to the network
 * - Tracking cross-chain order status and escrow events
 * - Using all three programming models (Reactive/Async/Sync)
 */
@Slf4j
public class FusionPlusExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    
    // Cross-chain configuration
    private static final Integer ETHEREUM_CHAIN_ID = 1;
    private static final Integer POLYGON_CHAIN_ID = 137;
    private static final Integer ARBITRUM_CHAIN_ID = 42161;
    
    // Token addresses
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private static final String USDC_ETHEREUM = "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7";
    private static final String USDC_POLYGON = "0x2791bca1f2de4661ed88a30c99a7a9449aa84174";
    private static final String WMATIC_POLYGON = "0x0d500b1d8e8ef31e21c99d1db9a6444d3adf1270";
    
    // User wallet address
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    
    // Sample amounts
    private static final String SWAP_AMOUNT_ETH = "1000000000000000000"; // 1 ETH (18 decimals)
    private static final String SWAP_AMOUNT_USDC = "1000000000"; // 1000 USDC (6 decimals)
    
    public static void main(String[] args) {
        FusionPlusExample example = new FusionPlusExample();
        
        try {
            log.info("=== 1inch FusionPlus API Example Started ===");
            
            // Comprehensive FusionPlus workflow examples
            example.runReactiveFusionPlusWorkflow();
            example.runSynchronousFusionPlusWorkflow();
            example.runParallelFusionPlusOperations();
            example.runCustomPresetExample();
            example.runCrossChainOrderTrackingExample();
            example.runSecretManagementExample();
            
            log.info("=== 1inch FusionPlus API Example Completed ===");
            
        } catch (Exception e) {
            log.error("FusionPlus example failed", e);
        }
    }
    
    /**
     * Demonstrates complete FusionPlus cross-chain workflow using reactive programming (RECOMMENDED)
     */
    public void runReactiveFusionPlusWorkflow() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Reactive FusionPlus Cross-Chain Workflow ===");
            
            // Build cross-chain quote request for ETH (Ethereum) -> USDC (Polygon)
            FusionPlusQuoteRequest quoteRequest = FusionPlusQuoteRequest.builder()
                    .srcChain(ETHEREUM_CHAIN_ID)
                    .dstChain(POLYGON_CHAIN_ID)
                    .srcTokenAddress(ETH_ADDRESS)
                    .dstTokenAddress(USDC_POLYGON)
                    .amount(SWAP_AMOUNT_ETH)
                    .walletAddress(WALLET_ADDRESS)
                    .enableEstimate(true)
                    .fee(100) // 1% fee in basis points
                    .build();
            
            // Reactive chain: Quote -> Order Building -> Order Submission -> Secret Submission -> Status Tracking
            client.fusionPlusQuoter().getQuoteRx(quoteRequest)
                .doOnSuccess(quote -> {
                    log.info("FusionPlus cross-chain quote received:");
                    log.info("  Quote ID: {}", quote.getQuoteId());
                    log.info("  Source Amount: {} ETH", quote.getSrcTokenAmount());
                    log.info("  Destination Amount: {} USDC", quote.getDstTokenAmount());
                    log.info("  Recommended Preset: {}", quote.getRecommendedPreset());
                    log.info("  Source Escrow Factory: {}", quote.getSrcEscrowFactory());
                    log.info("  Destination Escrow Factory: {}", quote.getDstEscrowFactory());
                    log.info("  Source Safety Deposit: {}", quote.getSrcSafetyDeposit());
                    log.info("  Destination Safety Deposit: {}", quote.getDstSafetyDeposit());
                    
                    if (quote.getTimeLocks() != null) {
                        log.info("  Time Locks Configuration:");
                        log.info("    Source Withdrawal: {} seconds", quote.getTimeLocks().getSrcWithdrawal());
                        log.info("    Source Public Withdrawal: {} seconds", quote.getTimeLocks().getSrcPublicWithdrawal());
                        log.info("    Destination Withdrawal: {} seconds", quote.getTimeLocks().getDstWithdrawal());
                        log.info("    Destination Public Withdrawal: {} seconds", quote.getTimeLocks().getDstPublicWithdrawal());
                    }
                    
                    if (quote.getPresets() != null) {
                        log.info("  Available Cross-Chain Presets:");
                        if (quote.getPresets().getFast() != null) {
                            log.info("    Fast: {} seconds auction, {} secrets required", 
                                    quote.getPresets().getFast().getAuctionDuration(),
                                    quote.getPresets().getFast().getSecretsCount());
                        }
                        if (quote.getPresets().getMedium() != null) {
                            log.info("    Medium: {} seconds auction, {} secrets required", 
                                    quote.getPresets().getMedium().getAuctionDuration(),
                                    quote.getPresets().getMedium().getSecretsCount());
                        }
                        if (quote.getPresets().getSlow() != null) {
                            log.info("    Slow: {} seconds auction, {} secrets required", 
                                    quote.getPresets().getSlow().getAuctionDuration(),
                                    quote.getPresets().getSlow().getSecretsCount());
                        }
                    }
                })
                .flatMap(quote -> {
                    // Build cross-chain order with EIP712 typed data
                    FusionPlusBuildOrderRequest buildRequest = FusionPlusBuildOrderRequest.builder()
                            .srcChain(ETHEREUM_CHAIN_ID)
                            .dstChain(POLYGON_CHAIN_ID)
                            .srcTokenAddress(ETH_ADDRESS)
                            .dstTokenAddress(USDC_POLYGON)
                            .amount(SWAP_AMOUNT_ETH)
                            .walletAddress(WALLET_ADDRESS)
                            .fee(100)
                            .preset("fast")
                            .source("SDK-Example")
                            .build();
                    
                    // Create secret hashes for atomic cross-chain execution
                    BuildOrderBody buildOrderBody = BuildOrderBody.builder()
                            .quote(quote)
                            .secretsHashList("0x315b47a8c3780434b153667588db4ca628526e20000000000000000000000000")
                            .build();
                    
                    log.info("Building cross-chain order with EIP712 typed data...");
                    return client.fusionPlusQuoter().buildQuoteTypedDataRx(buildRequest, buildOrderBody);
                })
                .doOnSuccess(buildResult -> {
                    log.info("Cross-chain order built successfully:");
                    log.info("  Order Hash: {}", buildResult.getOrderHash());
                    log.info("  Extension Length: {}", buildResult.getExtension() != null ? buildResult.getExtension().length() : 0);
                    log.info("  Has EIP712 Typed Data: {}", buildResult.getTypedData() != null);
                })
                .flatMap(buildResult -> {
                    // Create signed cross-chain order (in real implementation, you'd use proper signing)
                    FusionPlusSignedOrderInput signedOrder = FusionPlusSignedOrderInput.builder()
                            .order(CrossChainOrderV4.builder()
                                    .salt("0x" + System.currentTimeMillis())
                                    .maker(WALLET_ADDRESS)
                                    .receiver(WALLET_ADDRESS)
                                    .makerAsset(ETH_ADDRESS)
                                    .takerAsset(USDC_POLYGON)
                                    .makingAmount(SWAP_AMOUNT_ETH)
                                    .takingAmount("1000000000")
                                    .makerTraits("0x0")
                                    .srcChainId(ETHEREUM_CHAIN_ID)
                                    .dstChainId(POLYGON_CHAIN_ID)
                                    .build())
                            .signature("0x" + "0".repeat(130)) // Mock signature
                            .extension(buildResult.getExtension())
                            .srcChainId(ETHEREUM_CHAIN_ID)
                            .secretHashes(Arrays.asList("0x315b47a8c3780434b153667588db4ca628526e20000000000000000000000000"))
                            .timestamp(System.currentTimeMillis())
                            .deadline(System.currentTimeMillis() + 3600000) // 1 hour deadline
                            .build();
                    
                    log.info("Submitting cross-chain order to FusionPlus network...");
                    return client.fusionPlusRelayer().submitOrderRx(ETHEREUM_CHAIN_ID, signedOrder);
                })
                .doOnSuccess(result -> log.info("Cross-chain order submitted successfully to FusionPlus network!"))
                .flatMap(result -> {
                    // Get active cross-chain orders to verify submission
                    FusionPlusActiveOrdersRequest activeOrdersRequest = FusionPlusActiveOrdersRequest.builder()
                            .srcChain(ETHEREUM_CHAIN_ID)
                            .dstChain(POLYGON_CHAIN_ID)
                            .page(1)
                            .limit(10)
                            .sortBy("createdAt")
                            .build();
                    
                    return client.fusionPlusOrders().getActiveOrdersRx(activeOrdersRequest);
                })
                .doOnSuccess(activeOrders -> {
                    log.info("Active cross-chain orders retrieved:");
                    log.info("  Total items: {}", activeOrders.getMeta() != null ? activeOrders.getMeta().getTotalItems() : 0);
                    log.info("  Cross-chain orders: {}", activeOrders.getTotalCrossChainOrders());
                    log.info("  Supported source chains: {}", activeOrders.getSupportedSrcChains());
                    log.info("  Supported destination chains: {}", activeOrders.getSupportedDstChains());
                    
                    if (activeOrders.getItems() != null && !activeOrders.getItems().isEmpty()) {
                        activeOrders.getItems().forEach(order -> {
                            log.info("  Cross-Chain Order:");
                            log.info("    Order Hash: {}", order.getOrderHash());
                            log.info("    Source Chain: {}", order.getSrcChainId());
                            log.info("    Destination Chain: {}", order.getDstChainId());
                            log.info("    Status: {}", order.getStatus());
                            log.info("    Secret Hashes: {}", order.getSecretHashes());
                            log.info("    Source Escrow Factory: {}", order.getSrcEscrowFactory());
                            log.info("    Destination Escrow Factory: {}", order.getDstEscrowFactory());
                        });
                    }
                })
                .doOnError(error -> {
                    if (error instanceof OneInchApiException) {
                        OneInchApiException apiError = (OneInchApiException) error;
                        log.error("FusionPlus API Error: {} - {}", apiError.getError(), apiError.getDescription());
                    } else {
                        log.error("Unexpected error in FusionPlus workflow", error);
                    }
                })
                .subscribe(
                    result -> log.info("Reactive FusionPlus cross-chain workflow completed successfully!"),
                    error -> log.error("Reactive FusionPlus workflow failed", error)
                );
            
            // Wait for async operations
            Thread.sleep(5000);
        }
    }
    
    /**
     * Demonstrates synchronous FusionPlus cross-chain operations
     */
    public void runSynchronousFusionPlusWorkflow() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Synchronous FusionPlus Cross-Chain Workflow ===");
            
            // Step 1: Get supported chains for FusionPlus
            log.info("Step 1: Getting supported chains for FusionPlus...");
            GetActiveOrdersOutput supportedChains = client.fusionPlusOrders().getSupportedChains();
            log.info("Supported chains count: {}", supportedChains.getItems() != null ? supportedChains.getItems().size() : 0);
            
            // Step 2: Get cross-chain quote (synchronous)
            log.info("Step 2: Getting FusionPlus cross-chain quote...");
            FusionPlusQuoteRequest quoteRequest = FusionPlusQuoteRequest.builder()
                    .srcChain(ETHEREUM_CHAIN_ID)
                    .dstChain(ARBITRUM_CHAIN_ID)
                    .srcTokenAddress(USDC_ETHEREUM)
                    .dstTokenAddress(ETH_ADDRESS) // ETH on Arbitrum
                    .amount(SWAP_AMOUNT_USDC)
                    .walletAddress(WALLET_ADDRESS)
                    .enableEstimate(true)
                    .build();
            
            GetQuoteOutput quote = client.fusionPlusQuoter().getQuote(quoteRequest);
            log.info("Cross-chain Quote Details:");
            log.info("  Quote ID: {}", quote.getQuoteId());
            log.info("  Source Amount: {} USDC", quote.getSrcTokenAmount());
            log.info("  Destination Amount: {} ETH", quote.getDstTokenAmount());
            log.info("  Recommended Preset: {}", quote.getRecommendedPreset());
            log.info("  Source Safety Deposit: {}", quote.getSrcSafetyDeposit());
            log.info("  Destination Safety Deposit: {}", quote.getDstSafetyDeposit());
            
            // Step 3: Get cross-chain order history (asynchronous with CompletableFuture)
            log.info("Step 3: Getting cross-chain order history (async)...");
            CompletableFuture<GetActiveOrdersOutput> historyFuture = 
                    client.fusionPlusOrders().getOrdersByMakerAsync(ETHEREUM_CHAIN_ID, ARBITRUM_CHAIN_ID, WALLET_ADDRESS, 1, 5);
            
            GetActiveOrdersOutput history = historyFuture.get(10, TimeUnit.SECONDS);
            log.info("Cross-chain Order History:");
            log.info("  Total items: {}", history.getMeta() != null ? history.getMeta().getTotalItems() : 0);
            log.info("  Cross-chain orders: {}", history.getTotalCrossChainOrders());
            
            log.info("=== Synchronous FusionPlus Cross-Chain Workflow Complete ===");
        }
    }
    
    /**
     * Demonstrates parallel FusionPlus operations using reactive programming
     */
    public void runParallelFusionPlusOperations() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Parallel FusionPlus Cross-Chain Operations ===");
            
            // Run multiple cross-chain operations in parallel
            Single<GetActiveOrdersOutput> supportedChainsSingle = 
                    client.fusionPlusOrders().getSupportedChainsRx()
                            .subscribeOn(Schedulers.io())
                            .doOnSuccess(chains -> 
                                    log.info("Parallel: Got {} supported chains", 
                                            chains.getItems() != null ? chains.getItems().size() : 0));
            
            Single<GetQuoteOutput> ethToPolygonQuoteSingle = client.fusionPlusQuoter().getQuoteRx(
                    FusionPlusQuoteRequest.builder()
                            .srcChain(ETHEREUM_CHAIN_ID)
                            .dstChain(POLYGON_CHAIN_ID)
                            .srcTokenAddress(ETH_ADDRESS)
                            .dstTokenAddress(WMATIC_POLYGON)
                            .amount(SWAP_AMOUNT_ETH)
                            .walletAddress(WALLET_ADDRESS)
                            .enableEstimate(true)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(quote -> log.info("Parallel: Got ETH->MATIC quote ID {}", quote.getQuoteId()));
            
            Single<GetQuoteOutput> polygonToEthQuoteSingle = client.fusionPlusQuoter().getQuoteRx(
                    FusionPlusQuoteRequest.builder()
                            .srcChain(POLYGON_CHAIN_ID)
                            .dstChain(ETHEREUM_CHAIN_ID)
                            .srcTokenAddress(USDC_POLYGON)
                            .dstTokenAddress(ETH_ADDRESS)
                            .amount(SWAP_AMOUNT_USDC)
                            .walletAddress(WALLET_ADDRESS)
                            .enableEstimate(true)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(quote -> log.info("Parallel: Got USDC->ETH quote ID {}", quote.getQuoteId()));
            
            Single<GetActiveOrdersOutput> activeOrdersSingle = 
                    client.fusionPlusOrders().getActiveOrdersRx(
                            FusionPlusActiveOrdersRequest.builder()
                                    .page(1)
                                    .limit(5)
                                    .build()
                    ).subscribeOn(Schedulers.io())
                     .doOnSuccess(orders -> 
                             log.info("Parallel: Got {} active cross-chain orders", 
                                     orders.getItems() != null ? orders.getItems().size() : 0));
            
            // Combine all results
            Single.zip(supportedChainsSingle, ethToPolygonQuoteSingle, polygonToEthQuoteSingle, activeOrdersSingle,
                    (supportedChains, ethToPolygonQuote, polygonToEthQuote, activeOrders) -> {
                        log.info("All parallel FusionPlus cross-chain operations completed:");
                        log.info("  Supported Chains: {}", supportedChains.getItems() != null ? supportedChains.getItems().size() : 0);
                        log.info("  ETH->MATIC Quote ID: {}", ethToPolygonQuote.getQuoteId());
                        log.info("  USDC->ETH Quote ID: {}", polygonToEthQuote.getQuoteId());
                        log.info("  Active Orders: {}", activeOrders.getItems() != null ? activeOrders.getItems().size() : 0);
                        return "Parallel Success";
                    })
                    .timeout(20, TimeUnit.SECONDS)
                    .blockingGet();
        }
    }
    
    /**
     * Demonstrates custom preset configuration for advanced cross-chain users
     */
    public void runCustomPresetExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting FusionPlus Custom Preset Example ===");
            
            // Create custom preset with specific cross-chain auction parameters
            CustomPresetParams customPreset = CustomPresetParams.builder()
                    .auctionDuration(3600) // 1 hour auction for cross-chain
                    .auctionStartAmount("520000000000000000") // 0.52 ETH start
                    .auctionEndAmount("480000000000000000") // 0.48 ETH end (better rate)
                    .secretsCount(2) // Require 2 secrets for enhanced security
                    .allowPartialFills(false)
                    .allowMultipleFills(false)
                    .points(Arrays.asList(
                            AuctionPoint.builder().delay(0).coefficient(1.0).build(),     // Start: 100% rate
                            AuctionPoint.builder().delay(1800).coefficient(0.75).build(), // 30 min: 75% rate  
                            AuctionPoint.builder().delay(3600).coefficient(0.5).build()   // End: 50% rate (best)
                    ))
                    .gasBumpEstimate(100)
                    .gasPriceEstimate("50000000000") // 50 gwei
                    .build();
            
            FusionPlusQuoteRequest customQuoteRequest = FusionPlusQuoteRequest.builder()
                    .srcChain(ETHEREUM_CHAIN_ID)
                    .dstChain(POLYGON_CHAIN_ID)
                    .srcTokenAddress(ETH_ADDRESS)
                    .dstTokenAddress(USDC_POLYGON)
                    .amount(SWAP_AMOUNT_ETH)
                    .walletAddress(WALLET_ADDRESS)
                    .enableEstimate(true)
                    .build();
            
            // Get quote with custom preset
            client.fusionPlusQuoter().getQuoteWithCustomPresetsRx(customQuoteRequest, customPreset)
                    .doOnSuccess(quote -> {
                        log.info("FusionPlus Custom Preset Quote:");
                        log.info("  Quote ID: {}", quote.getQuoteId());
                        log.info("  Recommended Preset: {}", quote.getRecommendedPreset());
                        log.info("  Source Safety Deposit: {}", quote.getSrcSafetyDeposit());
                        log.info("  Destination Safety Deposit: {}", quote.getDstSafetyDeposit());
                        
                        if (quote.getPresets() != null && quote.getPresets().getCustom() != null) {
                            Preset custom = quote.getPresets().getCustom();
                            log.info("  Custom Cross-Chain Preset Details:");
                            log.info("    Auction Duration: {} seconds", custom.getAuctionDuration());
                            log.info("    Start Amount: {}", custom.getAuctionStartAmount());
                            log.info("    End Amount: {}", custom.getAuctionEndAmount());
                            log.info("    Secrets Required: {}", custom.getSecretsCount());
                            log.info("    Allow Partial Fills: {}", custom.getAllowPartialFills());
                            log.info("    Allow Multiple Fills: {}", custom.getAllowMultipleFills());
                            log.info("    Auction Points: {}", custom.getPoints() != null ? custom.getPoints().size() : 0);
                        }
                        
                        if (quote.getTimeLocks() != null) {
                            log.info("  Cross-Chain Time Locks:");
                            log.info("    Source Withdrawal: {} seconds", quote.getTimeLocks().getSrcWithdrawal());
                            log.info("    Destination Withdrawal: {} seconds", quote.getTimeLocks().getDstWithdrawal());
                            log.info("    Source Cancellation: {} seconds", quote.getTimeLocks().getSrcCancellation());
                            log.info("    Destination Cancellation: {} seconds", quote.getTimeLocks().getDstCancellation());
                        }
                    })
                    .doOnError(error -> log.error("Custom preset quote failed", error))
                    .blockingGet();
            
            log.info("=== FusionPlus Custom Preset Example Complete ===");
        }
    }
    
    /**
     * Demonstrates cross-chain order tracking and status monitoring
     */
    public void runCrossChainOrderTrackingExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting FusionPlus Cross-Chain Order Tracking Example ===");
            
            // Sample cross-chain order hash for demonstration
            String sampleOrderHash = "0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559";
            
            // Track specific cross-chain order by hash
            client.fusionPlusOrders().getOrderByOrderHashRx(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, sampleOrderHash)
                    .doOnSuccess(orderStatus -> {
                        log.info("Cross-Chain Order Status Tracking:");
                        log.info("  Order Hash: {}", orderStatus.getOrderHash());
                        log.info("  Source Chain: {}", orderStatus.getSrcChainId());
                        log.info("  Destination Chain: {}", orderStatus.getDstChainId());
                        log.info("  Status: {}", orderStatus.getStatus());
                        log.info("  Secret Hashes: {}", orderStatus.getSecretHashes());
                        log.info("  Created At: {}", orderStatus.getCreatedAt());
                        log.info("  Deadline: {}", orderStatus.getDeadline());
                        log.info("  Source Escrow Factory: {}", orderStatus.getSrcEscrowFactory());
                        log.info("  Destination Escrow Factory: {}", orderStatus.getDstEscrowFactory());
                        log.info("  Source Safety Deposit: {}", orderStatus.getSrcSafetyDeposit());
                        log.info("  Destination Safety Deposit: {}", orderStatus.getDstSafetyDeposit());
                        
                        if (orderStatus.getOrder() != null) {
                            log.info("  Cross-Chain Order Details:");
                            log.info("    Maker: {}", orderStatus.getOrder().getMaker());
                            log.info("    Maker Asset: {}", orderStatus.getOrder().getMakerAsset());
                            log.info("    Taker Asset: {}", orderStatus.getOrder().getTakerAsset());
                            log.info("    Making Amount: {}", orderStatus.getOrder().getMakingAmount());
                            log.info("    Taking Amount: {}", orderStatus.getOrder().getTakingAmount());
                            log.info("    Source Chain ID: {}", orderStatus.getOrder().getSrcChainId());
                            log.info("    Destination Chain ID: {}", orderStatus.getOrder().getDstChainId());
                        }
                    })
                    .doOnError(error -> {
                        log.info("Cross-chain order not found or error occurred (expected for demo): {}", error.getMessage());
                    })
                    .onErrorComplete() // Continue execution even if order not found
                    .ignoreElement()
                    .andThen(
                        // Get escrow events for the cross-chain order
                        client.fusionPlusOrders().getEscrowEventsRx(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, sampleOrderHash, 1, 10)
                    )
                    .doOnSuccess(events -> {
                        log.info("Cross-Chain Escrow Events:");
                        log.info("  Total Events: {}", events.getItems() != null ? events.getItems().size() : 0);
                    })
                    .doOnError(error -> log.info("No escrow events found (expected for demo): {}", error.getMessage()))
                    .onErrorComplete()
                    .ignoreElement()
                    .andThen(
                        // Get public actions available for the order
                        client.fusionPlusOrders().getPublicActionsRx(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, sampleOrderHash)
                    )
                    .doOnSuccess(publicAction -> {
                        log.info("Public Actions Available:");
                        log.info("  Action Type: {}", publicAction.getActionType());
                        log.info("  Chain ID: {}", publicAction.getChainId());
                        log.info("  Available: {}", publicAction.getAvailable());
                        log.info("  Available At: {}", publicAction.getAvailableAt());
                        log.info("  Target Contract: {}", publicAction.getTarget());
                        log.info("  Gas Estimate: {}", publicAction.getGasEstimate());
                    })
                    .doOnError(error -> log.info("No public actions available (expected for demo): {}", error.getMessage()))
                    .onErrorComplete()
                    .subscribe(
                        result -> log.info("Cross-chain order tracking example completed"),
                        error -> log.error("Cross-chain order tracking failed", error)
                    );
            
            // Wait for async operations
            Thread.sleep(3000);
            
            log.info("=== FusionPlus Cross-Chain Order Tracking Example Complete ===");
        }
    }
    
    /**
     * Demonstrates secret management for cross-chain atomic swaps
     */
    public void runSecretManagementExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting FusionPlus Secret Management Example ===");
            
            // Sample secret submission (second phase of cross-chain swap)
            String sampleOrderHash = "0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559";
            String sampleSecret = "0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef";
            
            // Create secret input for submission
            SecretInput secretInput = SecretInput.builder()
                    .orderHash(sampleOrderHash)
                    .secret(sampleSecret)
                    .chainId(POLYGON_CHAIN_ID) // Submit secret to destination chain
                    .executor(WALLET_ADDRESS)
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            // Submit secret for cross-chain order execution
            client.fusionPlusRelayer().submitSecretRx(POLYGON_CHAIN_ID, secretInput)
                    .doOnSuccess(result -> {
                        log.info("Secret Management:");
                        log.info("  Secret submitted successfully to destination chain");
                        log.info("  Order Hash: {}", sampleOrderHash);
                        log.info("  Chain ID: {}", POLYGON_CHAIN_ID);
                        log.info("  Executor: {}", WALLET_ADDRESS);
                    })
                    .doOnError(error -> {
                        log.info("Secret submission failed (expected for demo): {}", error.getMessage());
                        log.info("In production, this would complete the cross-chain atomic swap");
                    })
                    .onErrorComplete()
                    .subscribe(
                        result -> log.info("Secret management example completed"),
                        error -> log.error("Secret management failed", error)
                    );
            
            // Wait for async operation
            Thread.sleep(1000);
            
            log.info("=== FusionPlus Secret Management Example Complete ===");
        }
    }
    
    /**
     * Demonstrates comprehensive error handling for FusionPlus API
     */
    public void runErrorHandlingExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting FusionPlus Error Handling Example ===");
            
            // Invalid cross-chain quote request to trigger an error
            FusionPlusQuoteRequest invalidRequest = FusionPlusQuoteRequest.builder()
                    .srcChain(999) // Invalid chain
                    .dstChain(888) // Invalid chain
                    .srcTokenAddress("invalid_address")
                    .dstTokenAddress("invalid_address")
                    .amount("0")
                    .walletAddress("invalid_wallet")
                    .enableEstimate(false)
                    .build();
            
            client.fusionPlusQuoter().getQuoteRx(invalidRequest)
                    .doOnSuccess(quote -> log.info("This shouldn't happen"))
                    .doOnError(error -> {
                        log.info("FusionPlus error handling:");
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
                        // Fallback quote for cross-chain
                        return GetQuoteOutput.builder()
                                .quoteId("fallback_cross_chain_quote")
                                .srcTokenAmount("0")
                                .dstTokenAmount("0")
                                .recommendedPreset("fast")
                                .srcEscrowFactory("0x0000000000000000000000000000000000000000")
                                .dstEscrowFactory("0x0000000000000000000000000000000000000000")
                                .srcSafetyDeposit("0")
                                .dstSafetyDeposit("0")
                                .build();
                    })
                    .subscribe(
                        quote -> log.info("Final result (with fallback): Quote ID {}", quote.getQuoteId()),
                        error -> log.error("This shouldn't happen with fallback", error)
                    );
            
            log.info("=== FusionPlus Error Handling Example Complete ===");
        } catch (Exception e) {
            log.error("Unexpected error in FusionPlus error handling example", e);
        }
    }
}