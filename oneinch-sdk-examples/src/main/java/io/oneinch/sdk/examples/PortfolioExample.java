package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Example demonstrating Portfolio API v5 usage
 */
@Slf4j
public class PortfolioExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String SAMPLE_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302"; // 1inch token contract
    
    public static void main(String[] args) {
        PortfolioExample example = new PortfolioExample();
        
        try {
            log.info("=== Running Portfolio API v5 Examples ===");
            example.runServiceInfoExample();
            example.runCurrentValueExample();
            example.runProtocolsSnapshotExample();
            example.runTokensSnapshotExample();
            example.runReactivePortfolioExample();
            
        } catch (Exception e) {
            log.error("Portfolio example failed", e);
        }
    }

    /**
     * Demonstrates service information endpoints
     */
    public void runServiceInfoExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Service Information Example ===");
            
            // Check service status
            ApiStatusResponse status = client.portfolio().getServiceStatus();
            log.info("Service available: {}", status.getIsAvailable());
            
            // Get supported chains
            List<SupportedChainResponse> supportedChains = client.portfolio().getSupportedChains();
            log.info("Supported chains: {}", supportedChains.size());
            supportedChains.stream()
                    .limit(3)
                    .forEach(chain -> log.info("  Chain: {} ({})", 
                            chain.getChainName(), chain.getChainId()));
            
            // Get supported protocols
            List<SupportedProtocolGroupResponse> supportedProtocols = client.portfolio().getSupportedProtocols();
            log.info("Supported protocols: {}", supportedProtocols.size());
            supportedProtocols.stream()
                    .limit(3)
                    .forEach(protocol -> log.info("  Protocol: {} on chain {}", 
                            protocol.getProtocolGroupName(), protocol.getChainId()));
        }
    }

    /**
     * Demonstrates current value breakdown
     */
    public void runCurrentValueExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Current Value Example ===");
            
            PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1) // Ethereum
                    .build();
            
            CurrentValueResponse currentValue = client.portfolio().getCurrentValue(request);
            
            log.info("Total portfolio value: ${}", currentValue.getTotal());
            
            log.info("Value by address:");
            currentValue.getByAddress().forEach(addr -> 
                    log.info("  {}: ${}", addr.getAddress(), addr.getValueUsd()));
            
            log.info("Value by category:");
            currentValue.getByCategory().forEach(cat -> 
                    log.info("  {}: ${}", cat.getCategoryName(), cat.getValueUsd()));
            
            log.info("Value by chain:");
            currentValue.getByChain().forEach(chain -> 
                    log.info("  {}: ${}", chain.getChainName(), chain.getValueUsd()));
        }
    }

    /**
     * Demonstrates protocols snapshot
     */
    public void runProtocolsSnapshotExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Protocols Snapshot Example ===");
            
            PortfolioV5SnapshotRequest request = PortfolioV5SnapshotRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1) // Ethereum
                    .build();
            
            List<AdapterResult> protocolsSnapshot = client.portfolio().getProtocolsSnapshot(request);
            
            log.info("Found {} protocol positions", protocolsSnapshot.size());
            
            protocolsSnapshot.stream()
                    .limit(3)
                    .forEach(position -> {
                        log.info("  Protocol: {} - Value: ${}", 
                                position.getProtocolGroupName(), position.getValueUsd());
                        log.info("    Contract: {} ({})", 
                                position.getContractName(), position.getContractAddress());
                        log.info("    Underlying tokens: {}", position.getUnderlyingTokens().size());
                        log.info("    Reward tokens: {}", position.getRewardTokens().size());
                    });
        }
    }

    /**
     * Demonstrates tokens snapshot
     */
    public void runTokensSnapshotExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Tokens Snapshot Example ===");
            
            PortfolioV5SnapshotRequest request = PortfolioV5SnapshotRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1) // Ethereum
                    .build();
            
            List<AdapterResult> tokensSnapshot = client.portfolio().getTokensSnapshot(request);
            
            log.info("Found {} token positions", tokensSnapshot.size());
            
            tokensSnapshot.stream()
                    .limit(5)
                    .forEach(token -> {
                        log.info("  Token: {} ({}) - Value: ${}", 
                                token.getContractName(), token.getContractSymbol(), token.getValueUsd());
                        log.info("    Address: {}", token.getContractAddress());
                        log.info("    Locked: {}", token.getLocked());
                    });
        }
    }

    /**
     * Demonstrates reactive portfolio operations
     */
    public void runReactivePortfolioExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Reactive Portfolio Example ===");
            
            PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1)
                    .build();

            // Reactive current value
            client.portfolio().getCurrentValueRx(request)
                    .doOnSuccess(result -> log.info("Reactive current value total: ${}", result.getTotal()))
                    .doOnSuccess(result -> log.info("Reactive address count: {}", result.getByAddress().size()))
                    .doOnError(error -> log.error("Reactive current value failed", error))
                    .subscribe();
            
            // Small delay to let async operation complete
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            log.error("Reactive portfolio example failed", e);
        }
    }

    /**
     * Demonstrates protocols metrics (P&L, ROI, APR)
     */
    public void runProtocolsMetricsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Protocols Metrics Example ===");
            
            PortfolioV5MetricsRequest request = PortfolioV5MetricsRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1) // Ethereum
                    .build();
            
            List<HistoryMetrics> protocolsMetrics = client.portfolio().getProtocolsMetrics(request);
            
            log.info("Found {} protocol metrics", protocolsMetrics.size());
            
            protocolsMetrics.stream()
                    .limit(3)
                    .forEach(metric -> {
                        log.info("  Position: {}", metric.getIndex());
                        log.info("    Profit (USD): {}", metric.getProfitAbsUsd());
                        log.info("    ROI: {}%", metric.getRoi() != null ? 
                                String.format("%.2f", metric.getRoi() * 100) : "N/A");
                        log.info("    Weighted APR: {}%", metric.getWeightedApr() != null ? 
                                String.format("%.2f", metric.getWeightedApr() * 100) : "N/A");
                        log.info("    Holding time: {} days", metric.getHoldingTimeDays());
                        log.info("    Rewards (USD): {}", metric.getRewardsUsd());
                    });
        }
    }
}