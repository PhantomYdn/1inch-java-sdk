package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.oneinch.sdk.service.TokenService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String ONEINCH_TOKEN_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302";
    
    public static void main(String[] args) {
        TokenExample example = new TokenExample();
        
        try {
            log.info("=== Running Token API Examples ===");
            example.runTokenListExample();
            example.runTokenSearchExample();
            example.runCustomTokenExample();
            example.runMultiChainTokensExample();
            example.runReactiveTokenExample();
            
        } catch (Exception e) {
            log.error("Token example failed", e);
        }
    }

    /**
     * Demonstrates getting token list for a specific chain
     */
    public void runTokenListExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Token List Example ===");
            
            TokenService tokenService = client.token();
            
            // Get tokens for Ethereum mainnet
            TokenListRequest request = TokenListRequest.builder()
                    .chainId(1) // Ethereum
                    .provider("1inch")
                    .build();
            
            TokenListResponse tokenList = tokenService.getTokenList(request);
            
            log.info("Token list retrieved:");
            log.info("  Name: {}", tokenList.getName());
            log.info("  Version: {}", tokenList.getVersion());
            log.info("  Total tokens: {}", tokenList.getTokens().size());
            
            // Show first few tokens
            tokenList.getTokens().stream()
                    .limit(5)
                    .forEach(token -> log.info("  Token: {} ({}) - {}", 
                            token.getName(), token.getSymbol(), token.getAddress()));
        }
    }

    /**
     * Demonstrates searching for tokens across multiple chains
     */
    public void runTokenSearchExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Token Search Example ===");
            
            TokenService tokenService = client.token();
            
            // Search for 1inch tokens across all chains
            TokenSearchRequest request = TokenSearchRequest.builder()
                    .query("1inch")
                    .onlyPositiveRating(true)
                    .limit(10)
                    .build();
            
            List<Token> searchResults = tokenService.searchMultiChainTokens(request);
            
            log.info("Search results for '1inch':");
            searchResults.forEach(token -> 
                    log.info("  Found: {} ({}) on chain {} - rating: {}", 
                            token.getName(), token.getSymbol(), token.getChainId(), token.getRating()));
        }
    }

    /**
     * Demonstrates getting custom token information
     */
    public void runCustomTokenExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Custom Token Example ===");
            
            TokenService tokenService = client.token();
            
            // Get 1INCH token details on Ethereum
            Token token = tokenService.getCustomToken(1, ONEINCH_TOKEN_ADDRESS);
            
            log.info("Custom token details:");
            log.info("  Name: {}", token.getName());
            log.info("  Symbol: {}", token.getSymbol());
            log.info("  Address: {}", token.getAddress());
            log.info("  Decimals: {}", token.getDecimals());
            log.info("  Chain ID: {}", token.getChainId());
            log.info("  Rating: {}", token.getRating());
            
            if (token.getTags() != null && !token.getTags().isEmpty()) {
                log.info("  Tags: {}", token.getTags());
            }
        }
    }

    /**
     * Demonstrates getting multi-chain tokens from a provider  
     */
    public void runMultiChainTokensExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Multi-Chain Tokens Example ===");
            
            TokenService tokenService = client.token();
            
            // Get tokens from 1inch provider across all supported chains
            TokenListRequest request = TokenListRequest.builder()
                    .provider("1inch")
                    .build();
            
            List<ProviderTokenDto> multiChainTokens = tokenService.getMultiChainTokens(request);
            
            log.info("Multi-chain tokens from 1inch provider:");
            log.info("  Total tokens: {}", multiChainTokens.size());
            
            // Group by symbol and show which chains each token is on
            multiChainTokens.stream()
                    .filter(token -> "1INCH".equals(token.getSymbol()))
                    .forEach(token -> log.info("  1INCH available on chain: {} at {}", 
                            token.getChainId(), token.getAddress()));
        }
    }

    /**
     * Demonstrates reactive token operations with parallel execution
     */
    public void runReactiveTokenExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Reactive Token Example ===");
            
            TokenService tokenService = client.token();

            // Run multiple token operations in parallel using reactive streams
            Single<Map<String, ProviderTokenDto>> ethereumTokens = tokenService.getTokensRx(
                    TokenListRequest.builder()
                            .chainId(1) // Ethereum
                            .provider("1inch")
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(tokens -> log.info("Ethereum tokens loaded: {} tokens", tokens.size()));

            Single<Map<String, ProviderTokenDto>> bscTokens = tokenService.getTokensRx(
                    TokenListRequest.builder()
                            .chainId(56) // BSC
                            .provider("1inch")
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(tokens -> log.info("BSC tokens loaded: {} tokens", tokens.size()));

            Single<List<Token>> searchResults = tokenService.searchMultiChainTokensRx(
                    TokenSearchRequest.builder()
                            .query("USDC")
                            .onlyPositiveRating(true)
                            .limit(5)
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(tokens -> log.info("USDC search results: {} tokens", tokens.size()));

            Single<Token> customToken = tokenService.getCustomTokenRx(1, ONEINCH_TOKEN_ADDRESS)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(token -> log.info("Custom token loaded: {} ({})", token.getName(), token.getSymbol()));

            // Combine all results
            Single.zip(ethereumTokens, bscTokens, searchResults, customToken,
                    (ethTokens, bscTokensList, usdcTokens, oneInchToken) -> {
                        log.info("All reactive token operations completed:");
                        log.info("  Ethereum tokens: {}", ethTokens.size());
                        log.info("  BSC tokens: {}", bscTokensList.size());
                        log.info("  USDC search results: {}", usdcTokens.size());
                        log.info("  1INCH token: {} with {} decimals", 
                                oneInchToken.getSymbol(), oneInchToken.getDecimals());
                        return "All token operations completed successfully";
                    })
                    .timeout(15, TimeUnit.SECONDS)
                    .blockingGet();
        }
    }

    /**
     * Demonstrates async token operations with CompletableFuture
     */
    public void runAsyncTokenExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Async Token Example ===");
            
            TokenService tokenService = client.token();

            // Start multiple async operations
            CompletableFuture<Map<String, ProviderTokenDto>> tokenListFuture = tokenService.getTokensAsync(
                    TokenListRequest.builder()
                            .chainId(1) // Ethereum
                            .provider("1inch")
                            .build()
            );

            CompletableFuture<List<Token>> searchFuture = tokenService.searchMultiChainTokensAsync(
                    TokenSearchRequest.builder()
                            .query("ETH")
                            .limit(3)
                            .build()
            );

            CompletableFuture<Token> customTokenFuture = tokenService.getCustomTokenAsync(1, ONEINCH_TOKEN_ADDRESS);

            // Wait for all to complete and combine results
            CompletableFuture.allOf(tokenListFuture, searchFuture, customTokenFuture)
                    .thenRun(() -> {
                        try {
                            Map<String, ProviderTokenDto> tokenList = tokenListFuture.get();
                            List<Token> searchResults = searchFuture.get();
                            Token customToken = customTokenFuture.get();

                            log.info("Async operations completed:");
                            log.info("  Token list: {} tokens", tokenList.size());
                            log.info("  Search results: {} tokens", searchResults.size());
                            log.info("  Custom token: {} ({})", customToken.getName(), customToken.getSymbol());
                        } catch (Exception e) {
                            log.error("Error in async completion", e);
                        }
                    })
                    .get(10, TimeUnit.SECONDS);
        }
    }

    /**
     * Demonstrates comprehensive error handling for token operations
     */
    public void runTokenErrorHandlingExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Token Error Handling Example ===");
            
            TokenService tokenService = client.token();
            
            // Try to get a non-existent token (reactive with error handling)
            tokenService.getCustomTokenRx(1, "0x0000000000000000000000000000000000000000")
                    .doOnSuccess(token -> log.info("This shouldn't happen: {}", token))
                    .doOnError(error -> {
                        log.info("Expected error for invalid token:");
                        if (error instanceof OneInchException) {
                            log.error("  SDK Error: {}", error.getMessage());
                        } else {
                            log.error("  Unexpected error: {}", error.getMessage());
                        }
                    })
                    .onErrorReturn(error -> {
                        // Fallback token
                        Token fallback = new Token();
                        fallback.setSymbol("UNKNOWN");
                        fallback.setName("Unknown Token");
                        return fallback;
                    })
                    .subscribe(
                        token -> log.info("Final result (with fallback): {} ({})", 
                                token.getName(), token.getSymbol()),
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