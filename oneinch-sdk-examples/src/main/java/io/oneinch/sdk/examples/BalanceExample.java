package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.balance.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating Balance API usage
 */
@Slf4j
public class BalanceExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String SAMPLE_WALLET = "0x111111111117dc0aa78b770fa6a738034120c302"; // 1inch token contract
    private static final String SAMPLE_SPENDER = "0x1111111254eeb25477b68fb85ed929f73a960582"; // 1inch v5 router
    private static final String USDC_TOKEN = "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7"; // USDC
    private static final String USDT_TOKEN = "0xdAC17F958D2ee523a2206206994597C13D831ec7"; // USDT
    
    public static void main(String[] args) {
        BalanceExample example = new BalanceExample();
        
        try {
            log.info("=== Running Balance API Examples ===");
            example.runBasicBalanceExample();
            example.runCustomBalanceExample();
            example.runAllowanceExample();
            example.runCustomAllowanceExample();
            example.runCombinedBalanceAndAllowanceExample();
            example.runAggregatedBalanceExample();
            example.runMultiWalletBalanceExample();
            example.runReactiveBalanceExample();
            
        } catch (Exception e) {
            log.error("Balance example failed", e);
        }
    }

    /**
     * Demonstrates basic wallet balance checking
     */
    public void runBasicBalanceExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Basic Balance Example ===");
            
            BalanceRequest request = BalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .walletAddress(SAMPLE_WALLET)
                    .build();
            
            Map<String, BigInteger> balances = client.balance().getBalances(request);
            
            log.info("Found {} token balances for wallet {}", balances.size(), SAMPLE_WALLET);
            balances.entrySet().stream()
                    .limit(5)
                    .forEach(entry -> log.info("  Token {}: {} wei", entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Demonstrates custom token balance queries
     */
    public void runCustomBalanceExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Custom Balance Example ===");
            
            CustomBalanceRequest request = CustomBalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .walletAddress(SAMPLE_WALLET)
                    .tokens(Arrays.asList(USDC_TOKEN, USDT_TOKEN))
                    .build();
            
            Map<String, BigInteger> balances = client.balance().getCustomBalances(request);
            
            log.info("Custom token balances for wallet {}:", SAMPLE_WALLET);
            balances.forEach((token, balance) -> 
                    log.info("  Token {}: {} wei", token, balance));
        }
    }

    /**
     * Demonstrates allowance checking for DEX interactions
     */
    public void runAllowanceExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Allowance Example ===");
            
            AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .spender(SAMPLE_SPENDER)
                    .walletAddress(SAMPLE_WALLET)
                    .build();
            
            Map<String, BigInteger> allowances = client.balance().getAllowances(request);
            
            log.info("Found {} token allowances for spender {}", allowances.size(), SAMPLE_SPENDER);
            allowances.entrySet().stream()
                    .filter(entry -> !entry.getValue().equals(BigInteger.ZERO))
                    .limit(5)
                    .forEach(entry -> log.info("  Token {}: {} wei allowance", entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Demonstrates custom token allowance queries
     */
    public void runCustomAllowanceExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Custom Allowance Example ===");
            
            CustomAllowanceBalanceRequest request = CustomAllowanceBalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .spender(SAMPLE_SPENDER)
                    .walletAddress(SAMPLE_WALLET)
                    .tokens(Arrays.asList(USDC_TOKEN, USDT_TOKEN))
                    .build();
            
            Map<String, BigInteger> allowances = client.balance().getCustomAllowances(request);
            
            log.info("Custom token allowances for spender {}:", SAMPLE_SPENDER);
            allowances.forEach((token, allowance) -> 
                    log.info("  Token {}: {} wei allowance", token, allowance));
        }
    }

    /**
     * Demonstrates combined balance and allowance checking
     */
    public void runCombinedBalanceAndAllowanceExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Combined Balance and Allowance Example ===");
            
            AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .spender(SAMPLE_SPENDER)
                    .walletAddress(SAMPLE_WALLET)
                    .build();
            
            Map<String, BalanceAndAllowanceItem> combined = client.balance().getAllowancesAndBalances(request);
            
            log.info("Combined balance and allowance data for {} tokens:", combined.size());
            combined.entrySet().stream()
                    .limit(3)
                    .forEach(entry -> {
                        BalanceAndAllowanceItem item = entry.getValue();
                        log.info("  Token {}: balance={} wei, allowance={} wei", 
                                entry.getKey(), item.getBalance(), item.getAllowance());
                    });
        }
    }

    /**
     * Demonstrates aggregated balance checking for multiple wallets
     */
    public void runAggregatedBalanceExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Aggregated Balance Example ===");
            
            List<String> wallets = Arrays.asList(
                    SAMPLE_WALLET,
                    "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8" // Another sample wallet
            );
            
            AggregatedBalanceRequest request = AggregatedBalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .spender(SAMPLE_SPENDER)
                    .wallets(wallets)
                    .filterEmpty(true)
                    .build();
            
            List<AggregatedBalanceResponse> aggregated = client.balance().getAggregatedBalancesAndAllowances(request);
            
            log.info("Aggregated balance data for {} wallets:", wallets.size());
            aggregated.stream()
                    .limit(3)
                    .forEach(token -> {
                        log.info("  Token: {} ({}) - {} decimals", token.getName(), token.getSymbol(), token.getDecimals());
                        log.info("    Address: {}", token.getAddress());
                        log.info("    Wallet data count: {}", token.getWallets().size());
                    });
        }
    }

    /**
     * Demonstrates multi-wallet balance checking
     */
    public void runMultiWalletBalanceExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Multi-Wallet Balance Example ===");
            
            MultiWalletBalanceRequest request = MultiWalletBalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .wallets(Arrays.asList(
                            SAMPLE_WALLET,
                            "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8"
                    ))
                    .tokens(Arrays.asList(USDC_TOKEN, USDT_TOKEN))
                    .build();
            
            Map<String, Map<String, BigInteger>> multiWalletBalances = client.balance().getBalancesByMultipleWallets(request);
            
            log.info("Multi-wallet balance data:");
            multiWalletBalances.forEach((wallet, tokenBalances) -> {
                log.info("  Wallet: {}", wallet);
                tokenBalances.forEach((token, balance) -> 
                        log.info("    Token {}: {} wei", token, balance));
            });
        }
    }

    /**
     * Demonstrates reactive balance operations
     */
    public void runReactiveBalanceExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Reactive Balance Example ===");
            
            BalanceRequest request = BalanceRequest.builder()
                    .chainId(1) // Ethereum
                    .walletAddress(SAMPLE_WALLET)
                    .build();

            // Reactive balance checking
            client.balance().getBalancesRx(request)
                    .doOnSuccess(balances -> {
                        log.info("Reactive: Found {} token balances", balances.size());
                        balances.entrySet().stream()
                                .filter(entry -> !entry.getValue().equals(BigInteger.ZERO))
                                .limit(3)
                                .forEach(entry -> log.info("  Reactive: Token {}: {} wei", 
                                        entry.getKey(), entry.getValue()));
                    })
                    .doOnError(error -> log.error("Reactive balance check failed", error))
                    .subscribe();
            
            // Small delay to let async operation complete
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            log.error("Reactive balance example failed", e);
        }
    }
}