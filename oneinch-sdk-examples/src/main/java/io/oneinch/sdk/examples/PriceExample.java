package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.price.Currency;
import io.oneinch.sdk.model.price.PriceRequest;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Example demonstrating 1inch Price API usage
 * 
 * The Price API provides real-time token pricing across multiple blockchains
 * and currencies, supporting 60+ fiat currencies and 13+ blockchain networks.
 */
@Slf4j
public class PriceExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    
    // Common token addresses (Ethereum mainnet)
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"; // Native ETH
    private static final String USDC_ADDRESS = "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7"; // USDC
    private static final String USDT_ADDRESS = "0xdAC17F958D2ee523a2206206994597C13D831ec7"; // USDT
    private static final String ONEINCH_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302"; // 1INCH
    private static final String WBTC_ADDRESS = "0x2260FAC5E5542a773Aa44fBCfeDf7C193bc2C599"; // WBTC

    public static void main(String[] args) {
        log.info("=== 1inch Price API Examples ===");
        
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY) // Or set ONEINCH_API_KEY environment variable
                .build()) {
            
            // Run examples
            log.info("=== Basic Price Queries ===");
            basicPriceExamples(client);
            
            log.info("=== Multi-Currency Examples ===");
            multiCurrencyExamples(client);
            
            log.info("=== Reactive Programming Examples ===");
            reactivePriceExamples(client);
            
            log.info("=== Parallel Processing Examples ===");
            parallelPriceExamples(client);
            
            log.info("=== Portfolio Valuation Example ===");
            portfolioValuationExample(client);
            
            log.info("=== Multi-Chain Examples ===");
            multiChainExamples(client);
            
            // Small delay for async operations to complete
            Thread.sleep(2000);
            
        } catch (Exception e) {
            log.error("Price API examples failed", e);
        }
    }

    /**
     * Basic price query examples
     */
    public static void basicPriceExamples(OneInchClient client) {
        try {
            log.info("--- Basic Price Queries ---");
            
            // Get single token price in USD
            BigInteger ethPriceUsd = client.price().getPrice(1, ETH_ADDRESS, Currency.USD);
            log.info("ETH price: ${}", formatPrice(ethPriceUsd, 18, 2));
            
            // Get single token price in Wei (native format)
            BigInteger oneinchPriceWei = client.price().getPrice(1, ONEINCH_ADDRESS, null);
            log.info("1INCH price in Wei: {}", oneinchPriceWei);
            
            // Get multiple token prices at once
            PriceRequest multiTokenRequest = PriceRequest.builder()
                    .chainId(1) // Ethereum
                    .addresses(Arrays.asList(ETH_ADDRESS, USDC_ADDRESS, USDT_ADDRESS))
                    .currency(Currency.USD)
                    .build();
            
            Map<String, BigInteger> multiPrices = client.price().getPrices(multiTokenRequest);
            log.info("Multiple token prices:");
            multiPrices.forEach((address, price) -> {
                String symbol = getTokenSymbol(address);
                log.info("  {}: ${}", symbol, formatPrice(price, getTokenDecimals(address), 4));
            });
            
            // Get all whitelisted token prices
            Map<String, BigInteger> allPrices = client.price().getWhitelistPrices(1, Currency.USD);
            log.info("Found {} whitelisted token prices", allPrices.size());
            
        } catch (OneInchException e) {
            log.error("Basic price queries failed: {}", e.getMessage());
        }
    }

    /**
     * Multi-currency price examples
     */
    public static void multiCurrencyExamples(OneInchClient client) {
        try {
            log.info("--- Multi-Currency Price Examples ---");
            
            // Get ETH price in different fiat currencies
            Currency[] currencies = {Currency.USD, Currency.EUR, Currency.JPY, Currency.GBP, Currency.CNY};
            
            log.info("ETH prices in different currencies:");
            for (Currency currency : currencies) {
                BigInteger price = client.price().getPrice(1, ETH_ADDRESS, currency);
                log.info("  ETH/{}: {}", currency, formatPrice(price, 18, 2));
            }
            
            // Get supported currencies for Ethereum
            List<String> supportedCurrencies = client.price().getSupportedCurrencies(1);
            log.info("Supported currencies: {} total", supportedCurrencies.size());
            log.info("First 10: {}", supportedCurrencies.subList(0, Math.min(10, supportedCurrencies.size())));
            
        } catch (OneInchException e) {
            log.error("Multi-currency examples failed: {}", e.getMessage());
        }
    }

    /**
     * Reactive programming examples with RxJava
     */
    public static void reactivePriceExamples(OneInchClient client) {
        log.info("--- Reactive Programming Examples ---");
        
        // Reactive price fetching with error handling
        client.price().getPriceRx(1, ETH_ADDRESS, Currency.USD)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(price -> 
                    log.info("Reactive: ETH price: ${}", formatPrice(price, 18, 2)))
                .doOnError(error -> 
                    log.error("Reactive: ETH price fetch failed", error))
                .subscribe();
        
        // Reactive chain: Get prices -> Calculate portfolio value
        PriceRequest request = PriceRequest.builder()
                .chainId(1)
                .addresses(Arrays.asList(ETH_ADDRESS, ONEINCH_ADDRESS, USDC_ADDRESS))
                .currency(Currency.USD)
                .build();
        
        client.price().getPricesRx(request)
                .subscribeOn(Schedulers.io())
                .map(prices -> {
                    // Simulate portfolio holdings
                    BigDecimal totalValue = BigDecimal.ZERO;
                    
                    // 1 ETH
                    BigInteger ethPrice = prices.get(ETH_ADDRESS);
                    if (ethPrice != null) {
                        totalValue = totalValue.add(new BigDecimal(ethPrice).divide(BigDecimal.valueOf(1e18), 4, RoundingMode.HALF_UP));
                    }
                    
                    // 1000 1INCH tokens
                    BigInteger oneinchPrice = prices.get(ONEINCH_ADDRESS);
                    if (oneinchPrice != null) {
                        BigDecimal oneinchValue = new BigDecimal(oneinchPrice)
                                .multiply(BigDecimal.valueOf(1000))
                                .divide(BigDecimal.valueOf(1e18), 4, RoundingMode.HALF_UP);
                        totalValue = totalValue.add(oneinchValue);
                    }
                    
                    return totalValue;
                })
                .subscribe(
                    portfolioValue -> log.info("Reactive: Portfolio value: ${}", portfolioValue),
                    error -> log.error("Reactive: Portfolio calculation failed", error)
                );
    }

    /**
     * Parallel price fetching examples
     */
    public static void parallelPriceExamples(OneInchClient client) {
        log.info("--- Parallel Processing Examples ---");
        
        try {
            // Fetch prices for different tokens in parallel
            CompletableFuture<BigInteger> ethFuture = 
                    client.price().getPriceAsync(1, ETH_ADDRESS, Currency.USD);
            CompletableFuture<BigInteger> oneinchFuture = 
                    client.price().getPriceAsync(1, ONEINCH_ADDRESS, Currency.USD);
            CompletableFuture<BigInteger> usdcFuture = 
                    client.price().getPriceAsync(1, USDC_ADDRESS, Currency.USD);
            
            // Wait for all requests to complete
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    ethFuture, oneinchFuture, usdcFuture);
            
            allFutures.get(10, TimeUnit.SECONDS);
            
            log.info("Parallel results:");
            log.info("  ETH: ${}", formatPrice(ethFuture.get(), 18, 2));
            log.info("  1INCH: ${}", formatPrice(oneinchFuture.get(), 18, 6));
            log.info("  USDC: ${}", formatPrice(usdcFuture.get(), 6, 6));
            
        } catch (Exception e) {
            log.error("Parallel price fetching failed", e);
        }
    }

    /**
     * Portfolio valuation example
     */
    public static void portfolioValuationExample(OneInchClient client) {
        try {
            log.info("--- Portfolio Valuation Example ---");
            
            // Define a sample portfolio
            Map<String, BigDecimal> portfolio = Map.of(
                    ETH_ADDRESS, new BigDecimal("2.5"),      // 2.5 ETH
                    ONEINCH_ADDRESS, new BigDecimal("5000"), // 5000 1INCH
                    USDC_ADDRESS, new BigDecimal("1000"),    // 1000 USDC
                    WBTC_ADDRESS, new BigDecimal("0.1")      // 0.1 WBTC
            );
            
            // Get prices for all portfolio tokens
            PriceRequest request = PriceRequest.builder()
                    .chainId(1)
                    .addresses(List.copyOf(portfolio.keySet()))
                    .currency(Currency.USD)
                    .build();
            
            Map<String, BigInteger> prices = client.price().getPrices(request);
            
            // Calculate total portfolio value
            BigDecimal totalValue = BigDecimal.ZERO;
            
            log.info("Portfolio breakdown:");
            for (Map.Entry<String, BigDecimal> holding : portfolio.entrySet()) {
                String address = holding.getKey();
                BigDecimal amount = holding.getValue();
                BigInteger price = prices.get(address);
                
                if (price != null) {
                    int decimals = getTokenDecimals(address);
                    BigDecimal tokenValue = new BigDecimal(price)
                            .multiply(amount)
                            .divide(BigDecimal.valueOf(Math.pow(10, decimals)), 2, RoundingMode.HALF_UP);
                    
                    totalValue = totalValue.add(tokenValue);
                    
                    log.info("  {} {}: ${} (${} each)", 
                            amount, getTokenSymbol(address), tokenValue, 
                            formatPrice(price, decimals, 4));
                }
            }
            
            log.info("Total portfolio value: ${}", totalValue);
            
        } catch (OneInchException e) {
            log.error("Portfolio valuation failed: {}", e.getMessage());
        }
    }

    /**
     * Multi-chain price examples
     */
    public static void multiChainExamples(OneInchClient client) {
        try {
            log.info("--- Multi-Chain Price Examples ---");
            
            // Get ETH price on different chains
            int[] chainIds = {1, 137, 56, 42161}; // Ethereum, Polygon, BSC, Arbitrum
            String[] chainNames = {"Ethereum", "Polygon", "BSC", "Arbitrum"};
            
            log.info("ETH prices across different chains:");
            for (int i = 0; i < chainIds.length; i++) {
                try {
                    BigInteger price = client.price().getPrice(chainIds[i], ETH_ADDRESS, Currency.USD);
                    log.info("  {}: ${}", chainNames[i], formatPrice(price, 18, 2));
                } catch (OneInchException e) {
                    log.warn("  {}: Price not available - {}", chainNames[i], e.getMessage());
                }
            }
            
            // Get supported currencies for different chains
            log.info("Supported currencies by chain:");
            for (int i = 0; i < Math.min(2, chainIds.length); i++) {
                try {
                    List<String> currencies = client.price().getSupportedCurrencies(chainIds[i]);
                    log.info("  {} ({} currencies): {}", 
                            chainNames[i], currencies.size(), 
                            currencies.subList(0, Math.min(5, currencies.size())));
                } catch (OneInchException e) {
                    log.warn("  {}: Could not fetch currencies - {}", chainNames[i], e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("Multi-chain examples failed", e);
        }
    }

    // Helper methods
    private static String formatPrice(BigInteger price, int decimals, int displayDecimals) {
        BigDecimal decimalPrice = new BigDecimal(price)
                .divide(BigDecimal.valueOf(Math.pow(10, decimals)), displayDecimals, RoundingMode.HALF_UP);
        return decimalPrice.toPlainString();
    }

    private static String getTokenSymbol(String address) {
        switch (address.toLowerCase()) {
            case "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee": return "ETH";
            case "0xa0b86a33e6ab6b6ce4e5a5b7db2e8df6b1d2b9c7": return "USDC";
            case "0xdac17f958d2ee523a2206206994597c13d831ec7": return "USDT";
            case "0x111111111117dc0aa78b770fa6a738034120c302": return "1INCH";
            case "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599": return "WBTC";
            default: return "TOKEN";
        }
    }

    private static int getTokenDecimals(String address) {
        switch (address.toLowerCase()) {
            case "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee": return 18; // ETH
            case "0xa0b86a33e6ab6b6ce4e5a5b7db2e8df6b1d2b9c7": return 6;  // USDC
            case "0xdac17f958d2ee523a2206206994597c13d831ec7": return 6;  // USDT
            case "0x111111111117dc0aa78b770fa6a738034120c302": return 18; // 1INCH
            case "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599": return 8;  // WBTC
            default: return 18; // Default to 18 decimals
        }
    }
}