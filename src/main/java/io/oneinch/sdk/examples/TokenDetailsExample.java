package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.oneinch.sdk.service.TokenDetailsService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenDetailsExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String ONEINCH_TOKEN_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302";
    private static final String USDC_TOKEN_ADDRESS = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48";
    
    public static void main(String[] args) {
        TokenDetailsExample example = new TokenDetailsExample();
        
        try {
            log.info("=== Running Token Details API Examples ===");
            example.runTokenDetailsExample();
            example.runTokenChartExample();
            example.runTokenPriceChangeExample();
            example.runNativeTokenExample();
            example.runReactiveTokenDetailsExample();
            
        } catch (Exception e) {
            log.error("Token details example failed", e);
        }
    }

    /**
     * Demonstrates getting detailed token information
     */
    public void runTokenDetailsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Token Details Example ===");
            
            TokenDetailsService tokenDetailsService = client.tokenDetails();
            
            // Get detailed information for 1INCH token
            TokenDetailsRequest request = TokenDetailsRequest.builder()
                    .chainId(1) // Ethereum
                    .contractAddress(ONEINCH_TOKEN_ADDRESS)
                    .provider("coingecko")
                    .build();
            
            TokenDetailsResponse details = tokenDetailsService.getTokenDetails(request);
            
            log.info("Token details for 1INCH:");
            if (details.getAssets() != null) {
                log.info("  Name: {}", details.getAssets().getName());
                log.info("  Website: {}", details.getAssets().getWebsite());
                log.info("  Description: {}", details.getAssets().getShortDescription());
            }
            if (details.getDetails() != null) {
                log.info("  Provider: {}", details.getDetails().getProvider());
                log.info("  Market Cap: ${}", details.getDetails().getMarketCap());
                log.info("  24h Volume: ${}", details.getDetails().getVol24());
                log.info("  Circulating Supply: {}", details.getDetails().getCirculatingSupply());
                log.info("  Total Supply: {}", details.getDetails().getTotalSupply());
            }
        }
    }

    /**
     * Demonstrates getting token price charts
     */
    public void runTokenChartExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Token Chart Example ===");
            
            TokenDetailsService tokenDetailsService = client.tokenDetails();
            
            // Get 7-day price chart for 1INCH token using range request
            long currentTime = System.currentTimeMillis() / 1000;
            long sevenDaysAgo = currentTime - (7 * 24 * 60 * 60);
            
            ChartRangeRequest request = ChartRangeRequest.builder()
                    .chainId(1) // Ethereum
                    .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                    .from(sevenDaysAgo)
                    .to(currentTime)
                    .provider("coingecko")
                    .build();
            
            ChartDataResponse chartData = tokenDetailsService.getTokenChartByRange(request);
            
            log.info("Chart data for 1INCH (7 days):");
            if (chartData.getData() != null && !chartData.getData().isEmpty()) {
                log.info("  Total data points: {}", chartData.getData().size());
                log.info("  Provider: {}", chartData.getProvider());
                
                // Show first and last few data points
                var dataPoints = chartData.getData();
                if (!dataPoints.isEmpty()) {
                    var firstPoint = dataPoints.get(0);
                    var lastPoint = dataPoints.get(dataPoints.size() - 1);
                    log.info("  First price point: timestamp={}, price=${}", 
                            firstPoint.getTime(), firstPoint.getPrice());
                    log.info("  Last price point: timestamp={}, price=${}", 
                            lastPoint.getTime(), lastPoint.getPrice());
                }
            } else {
                log.info("  No chart data available");
            }
        }
    }

    /**
     * Demonstrates getting token price changes over different periods
     */
    public void runTokenPriceChangeExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Token Price Change Example ===");
            
            TokenDetailsService tokenDetailsService = client.tokenDetails();
            
            // Get price changes for 1INCH token
            PriceChangeRequest request = PriceChangeRequest.builder()
                    .chainId(1) // Ethereum
                    .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                    .interval("24h")
                    .build();
            
            TokenPriceChangeResponse priceChange = tokenDetailsService.getTokenPriceChange(request);
            
            log.info("Price changes for 1INCH (24h):");
            if (priceChange.getInUSD() != null) {
                log.info("  Price change in USD: ${}", priceChange.getInUSD());
            }
            if (priceChange.getInPercent() != null) {
                log.info("  Price change in percent: {}%", priceChange.getInPercent());
            }
        }
    }

    /**
     * Demonstrates getting native token (ETH) information
     */
    public void runNativeTokenExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Native Token Example ===");
            
            TokenDetailsService tokenDetailsService = client.tokenDetails();
            
            // Get ETH (native token) details for Ethereum
            TokenDetailsRequest request = TokenDetailsRequest.builder()
                    .chainId(1) // Ethereum
                    .provider("coingecko")
                    .build();
            
            TokenDetailsResponse ethDetails = tokenDetailsService.getNativeTokenDetails(request);
            
            log.info("Native token (ETH) details:");
            if (ethDetails.getAssets() != null) {
                log.info("  Name: {}", ethDetails.getAssets().getName());
                log.info("  Website: {}", ethDetails.getAssets().getWebsite());
            }
            if (ethDetails.getDetails() != null) {
                log.info("  Provider: {}", ethDetails.getDetails().getProvider());
                log.info("  Market Cap: ${}", ethDetails.getDetails().getMarketCap());
                log.info("  24h Volume: ${}", ethDetails.getDetails().getVol24());
                log.info("  Total Supply: {}", ethDetails.getDetails().getTotalSupply());
            }
            
            // Get ETH chart data for the last 30 days
            long currentTime = System.currentTimeMillis() / 1000;
            long thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60);
            
            ChartRangeRequest chartRequest = ChartRangeRequest.builder()
                    .chainId(1) // Ethereum
                    .from(thirtyDaysAgo)
                    .to(currentTime)
                    .provider("coingecko")
                    .build();
            
            ChartDataResponse ethChart = tokenDetailsService.getNativeTokenChartByRange(chartRequest);
            
            if (ethChart.getData() != null && !ethChart.getData().isEmpty()) {
                log.info("ETH chart data (30 days): {} data points", ethChart.getData().size());
            } else {
                log.info("No ETH chart data available");
            }
        }
    }

    /**
     * Demonstrates reactive token details operations with parallel execution
     */
    public void runReactiveTokenDetailsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Reactive Token Details Example ===");
            
            TokenDetailsService tokenDetailsService = client.tokenDetails();

            // Run multiple token details operations in parallel
            Single<TokenDetailsResponse> oneInchDetails = tokenDetailsService.getTokenDetailsRx(
                    TokenDetailsRequest.builder()
                            .chainId(1)
                            .contractAddress(ONEINCH_TOKEN_ADDRESS)
                            .provider("coingecko")
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(details -> log.info("1INCH details loaded: {}", 
                     details.getAssets() != null ? details.getAssets().getName() : "N/A"));

            Single<TokenDetailsResponse> usdcDetails = tokenDetailsService.getTokenDetailsRx(
                    TokenDetailsRequest.builder()
                            .chainId(1)
                            .contractAddress(USDC_TOKEN_ADDRESS)
                            .provider("coingecko")
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(details -> log.info("USDC details loaded: {}", 
                     details.getAssets() != null ? details.getAssets().getName() : "N/A"));

            Single<TokenPriceChangeResponse> priceChanges = tokenDetailsService.getTokenPriceChangeRx(
                    PriceChangeRequest.builder()
                            .chainId(1)
                            .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                            .interval("24h")
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(changes -> log.info("1INCH price changes loaded: {}%", changes.getInPercent()));

            long currentTime = System.currentTimeMillis() / 1000;
            long oneDayAgo = currentTime - (24 * 60 * 60);

            Single<ChartDataResponse> chartData = tokenDetailsService.getTokenChartByRangeRx(
                    ChartRangeRequest.builder()
                            .chainId(1)
                            .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                            .from(oneDayAgo)
                            .to(currentTime)
                            .provider("coingecko")
                            .build()
            ).subscribeOn(Schedulers.io())
             .doOnSuccess(chart -> log.info("1INCH chart loaded: {} data points", 
                     chart.getData() != null ? chart.getData().size() : 0));

            // Combine all results
            Single.zip(oneInchDetails, usdcDetails, priceChanges, chartData,
                    (oneInch, usdc, changes, chart) -> {
                        log.info("All reactive token details operations completed:");
                        log.info("  1INCH name: {}", 
                                oneInch.getAssets() != null ? oneInch.getAssets().getName() : "N/A");
                        log.info("  USDC name: {}", 
                                usdc.getAssets() != null ? usdc.getAssets().getName() : "N/A");
                        log.info("  1INCH 24h change: {}%", changes.getInPercent());
                        log.info("  Chart data points: {}", 
                                chart.getData() != null ? chart.getData().size() : 0);
                        return "All operations completed";
                    })
                    .timeout(15, TimeUnit.SECONDS)
                    .blockingGet();
        }
    }

    /**
     * Demonstrates async token details operations with CompletableFuture
     */
    public void runAsyncTokenDetailsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Async Token Details Example ===");
            
            TokenDetailsService tokenDetailsService = client.tokenDetails();

            // Start multiple async operations
            CompletableFuture<TokenDetailsResponse> detailsFuture = tokenDetailsService.getTokenDetailsAsync(
                    TokenDetailsRequest.builder()
                            .chainId(1)
                            .contractAddress(ONEINCH_TOKEN_ADDRESS)
                            .provider("coingecko")
                            .build()
            );

            CompletableFuture<TokenPriceChangeResponse> priceChangeFuture = tokenDetailsService.getTokenPriceChangeAsync(
                    PriceChangeRequest.builder()
                            .chainId(1)
                            .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                            .interval("24h")
                            .build()
            );

            long currentTime = System.currentTimeMillis() / 1000;
            long sevenDaysAgo = currentTime - (7 * 24 * 60 * 60);

            CompletableFuture<ChartDataResponse> chartFuture = tokenDetailsService.getTokenChartByRangeAsync(
                    ChartRangeRequest.builder()
                            .chainId(1)
                            .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                            .from(sevenDaysAgo)
                            .to(currentTime)
                            .provider("coingecko")
                            .build()
            );

            // Wait for all to complete and combine results
            CompletableFuture.allOf(detailsFuture, priceChangeFuture, chartFuture)
                    .thenRun(() -> {
                        try {
                            TokenDetailsResponse details = detailsFuture.get();
                            TokenPriceChangeResponse priceChange = priceChangeFuture.get();
                            ChartDataResponse chart = chartFuture.get();

                            log.info("Async token details operations completed:");
                            log.info("  Token name: {}", 
                                    details.getAssets() != null ? details.getAssets().getName() : "N/A");
                            log.info("  24h change: {}%", priceChange.getInPercent());
                            log.info("  Chart points: {}", 
                                    chart.getData() != null ? chart.getData().size() : 0);
                        } catch (Exception e) {
                            log.error("Error in async completion", e);
                        }
                    })
                    .get(10, TimeUnit.SECONDS);
        }
    }

    /**
     * Demonstrates comprehensive error handling for token details operations
     */
    public void runTokenDetailsErrorHandlingExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Token Details Error Handling Example ===");
            
            TokenDetailsService tokenDetailsService = client.tokenDetails();
            
            // Try to get details for a non-existent token (reactive with error handling)
            tokenDetailsService.getTokenDetailsRx(
                    TokenDetailsRequest.builder()
                            .chainId(1)
                            .contractAddress("0x0000000000000000000000000000000000000000")
                            .provider("coingecko")
                            .build()
            )
            .doOnSuccess(details -> log.info("This shouldn't happen: {}", details))
            .doOnError(error -> {
                log.info("Expected error for invalid token:");
                if (error instanceof OneInchException) {
                    log.error("  SDK Error: {}", error.getMessage());
                } else {
                    log.error("  Unexpected error: {}", error.getMessage());
                }
            })
            .onErrorReturn(error -> {
                // Fallback response
                TokenDetailsResponse fallback = new TokenDetailsResponse();
                // Note: TokenDetailsResponse fields are not directly settable 
                // This is just for demonstration
                return fallback;
            })
            .subscribe(
                details -> log.info("Final result (with fallback): {}", 
                        details.getAssets() != null ? details.getAssets().getName() : "fallback"),
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