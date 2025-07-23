package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SwapExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private static final String ONEINCH_TOKEN_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302";
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    
    public static void main(String[] args) {
        SwapExample example = new SwapExample();
        
        try {
            log.info("=== Running Reactive Swap Example ===");
            example.runReactiveSwapFlow();
            
            log.info("=== Running Synchronous Swap Example ===");
            example.runSynchronousSwapFlow();
            
            log.info("=== Running Parallel Reactive Example ===");
            example.runParallelReactiveExample();
            
        } catch (Exception e) {
            log.error("Example failed", e);
        }
    }
    
    /**
     * Demonstrates the new reactive (RxJava) approach - RECOMMENDED
     */
    public void runReactiveSwapFlow() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Reactive Swap Flow ===");
            
            BigInteger swapAmount = new BigInteger("10000000000000000"); // 0.01 ETH in wei
            
            // Reactive chaining with proper error handling
            client.swap().getSpenderRx(1)
                .doOnSuccess(spender -> log.info("Spender address: {}", spender.getAddress()))
                .flatMap(spender -> {
                    // Step 2: Check allowance
                    AllowanceRequest allowanceRequest = AllowanceRequest.builder()
                            .chainId(1)  // Ethereum
                            .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                            .walletAddress(WALLET_ADDRESS)
                            .build();
                    
                    return client.swap().getAllowanceRx(allowanceRequest);
                })
                .doOnSuccess(allowance -> log.info("Current allowance: {}", allowance.getAllowance()))
                .flatMap(allowance -> {
                    // Step 3: Get quote
                    QuoteRequest quoteRequest = QuoteRequest.builder()
                            .chainId(1)  // Ethereum
                            .src(ETH_ADDRESS)
                            .dst(ONEINCH_TOKEN_ADDRESS)
                            .amount(swapAmount)
                            .includeTokensInfo(true)
                            .includeProtocols(true)
                            .includeGas(true)
                            .build();
                    
                    return client.swap().getQuoteRx(quoteRequest);
                })
                .doOnSuccess(quote -> {
                    log.info("Quote received:");
                    log.info("  Expected output: {} {}", quote.getDstAmount(), 
                            quote.getDstToken() != null ? quote.getDstToken().getSymbol() : "tokens");
                    log.info("  Estimated gas: {}", quote.getGas());
                })
                .flatMap(quote -> {
                    // Step 4: Get swap transaction data
                    SwapRequest swapRequest = SwapRequest.builder()
                            .chainId(1)  // Ethereum
                            .src(ETH_ADDRESS)
                            .dst(ONEINCH_TOKEN_ADDRESS)
                            .amount(swapAmount)
                            .from(WALLET_ADDRESS)
                            .origin(WALLET_ADDRESS)
                            .slippage(1.0) // 1% slippage
                            .includeTokensInfo(true)
                            .build();
                    
                    return client.swap().getSwapRx(swapRequest);
                })
                .doOnSuccess(swap -> {
                    log.info("Swap transaction ready:");
                    log.info("  To: {}", swap.getTx().getTo());
                    log.info("  Value: {} ETH", swap.getTx().getValue());
                    log.info("  Gas limit: {}", swap.getTx().getGas());
                    log.info("  Expected output: {}", swap.getDstAmount());
                })
                .doOnError(error -> {
                    if (error instanceof OneInchApiException) {
                        OneInchApiException apiError = (OneInchApiException) error;
                        log.error("API Error: {} - {}", apiError.getError(), apiError.getDescription());
                    } else {
                        log.error("Unexpected error", error);
                    }
                })
                .subscribe(
                    swap -> log.info("Reactive swap flow completed successfully!"),
                    error -> log.error("Reactive swap flow failed", error)
                );
            
            // Wait a bit for the async operations to complete
            Thread.sleep(2000);
        }
    }
    
    /**
     * Demonstrates parallel reactive operations
     */
    public void runParallelReactiveExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Parallel Reactive Example ===");

            BigInteger swapAmount = new BigInteger("10000000000000000"); // 0.01 ETH in wei

            // Run multiple operations in parallel
            Single<SpenderResponse> spenderSingle = client.swap().getSpenderRx(1)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(spender -> log.info("Parallel: Got spender {}", spender.getAddress()));
            
            Single<QuoteResponse> quoteSingle = client.swap().getQuoteRx(
                    QuoteRequest.builder()
                            .chainId(1)  // Ethereum
                            .src(ETH_ADDRESS)
                            .dst(ONEINCH_TOKEN_ADDRESS)
                            .amount(swapAmount)
                            .includeGas(true)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(quote -> log.info("Parallel: Got quote {}", quote.getDstAmount()));
            
            Single<AllowanceResponse> allowanceSingle = client.swap().getAllowanceRx(
                    AllowanceRequest.builder()
                            .chainId(1)  // Ethereum
                            .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                            .walletAddress(WALLET_ADDRESS)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(allowance -> log.info("Parallel: Got allowance {}", allowance.getAllowance()));
            
            // Combine all results
            Single.zip(spenderSingle, quoteSingle, allowanceSingle,
                    (spender, quote, allowance) -> {
                        log.info("All parallel operations completed:");
                        log.info("  Spender: {}", spender.getAddress());
                        log.info("  Quote: {}", quote.getDstAmount());
                        log.info("  Allowance: {}", allowance.getAllowance());
                        return "Success";
                    })
                    .timeout(10, TimeUnit.SECONDS)
                    .blockingGet();
        }
    }
    
    /**
     * Demonstrates the synchronous approach
     */
    public void runSynchronousSwapFlow() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Synchronous Swap Flow ===");

            BigInteger swapAmount = new BigInteger("10000000000000000"); // 0.01 ETH in wei

            // Step 1: Get spender address (synchronous)
            log.info("Step 1: Getting spender address...");
            SpenderResponse spender = client.swap().getSpender(1);
            log.info("Spender address: {}", spender.getAddress());
            
            // Step 2: Check allowance (synchronous)
            log.info("Step 2: Checking allowance...");
            AllowanceRequest allowanceRequest = AllowanceRequest.builder()
                    .chainId(1)  // Ethereum
                    .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                    .walletAddress(WALLET_ADDRESS)
                    .build();
            
            AllowanceResponse allowance = client.swap().getAllowance(allowanceRequest);
            log.info("Current allowance: {}", allowance.getAllowance());
            
            // Step 3: Get quote (synchronous)
            log.info("Step 3: Getting quote...");
            QuoteRequest quoteRequest = QuoteRequest.builder()
                    .chainId(1)  // Ethereum
                    .src(ETH_ADDRESS)
                    .dst(ONEINCH_TOKEN_ADDRESS)
                    .amount(swapAmount)
                    .includeTokensInfo(true)
                    .includeProtocols(true)
                    .includeGas(true)
                    .build();
            
            QuoteResponse quote = client.swap().getQuote(quoteRequest);
            log.info("Expected output amount: {}", quote.getDstAmount());
            log.info("Estimated gas: {}", quote.getGas());
            
            // Step 4: Get swap data (asynchronous with CompletableFuture)
            log.info("Step 4: Getting swap transaction data (async)...");
            SwapRequest swapRequest = SwapRequest.builder()
                    .chainId(1)  // Ethereum
                    .src(ETH_ADDRESS)
                    .dst(ONEINCH_TOKEN_ADDRESS)
                    .amount(swapAmount)
                    .from(WALLET_ADDRESS)
                    .origin(WALLET_ADDRESS)
                    .slippage(1.0)
                    .includeTokensInfo(true)
                    .build();
            
            CompletableFuture<SwapResponse> swapFuture = client.swap().getSwapAsync(swapRequest);
            SwapResponse swap = swapFuture.get(10, TimeUnit.SECONDS);
            
            log.info("Swap transaction data:");
            log.info("  From: {}", swap.getTx().getFrom());
            log.info("  To: {}", swap.getTx().getTo());
            log.info("  Value: {}", swap.getTx().getValue());
            log.info("  Gas: {}", swap.getTx().getGas());
            log.info("Expected output amount: {}", swap.getDstAmount());
            
            log.info("=== Synchronous Swap Flow Complete ===");
        }
    }
    
    /**
     * Demonstrates comprehensive error handling
     */
    public void runErrorHandlingExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Error Handling Example ===");
            
            // Invalid quote request to trigger an error (reactive approach)
            QuoteRequest invalidRequest = QuoteRequest.builder()
                    .chainId(1)  // Ethereum
                    .src("invalid_address")
                    .dst("invalid_address")
                    .amount(BigInteger.ZERO)
                    .build();
            
            client.swap().getQuoteRx(invalidRequest)
                    .doOnSuccess(quote -> log.info("This shouldn't happen"))
                    .doOnError(error -> {
                        log.info("Reactive error handling:");
                        if (error instanceof OneInchApiException) {
                            OneInchApiException apiError = (OneInchApiException) error;
                            log.error("  API Error: {}", apiError.getError());
                            log.error("  Description: {}", apiError.getDescription());
                            log.error("  Status Code: {}", apiError.getStatusCode());
                            log.error("  Request ID: {}", apiError.getRequestId());
                        } else if (error instanceof OneInchException) {
                            log.error("  SDK Error: {}", error.getMessage());
                        } else {
                            log.error("  Unexpected error: {}", error.getMessage());
                        }
                    })
                    .onErrorReturn(error -> {
                        // Fallback value
                        QuoteResponse fallback = new QuoteResponse();
                        fallback.setDstAmount(BigInteger.ZERO);
                        return fallback;
                    })
                    .subscribe(
                        quote -> log.info("Final result (with fallback): {}", quote.getDstAmount()),
                        error -> log.error("This shouldn't happen with fallback", error)
                    );
            
            log.info("=== Error Handling Example Complete ===");
        } catch (Exception e) {
            log.error("Unexpected error in error handling example", e);
        }
    }
}