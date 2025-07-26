package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Example demonstrating Portfolio API usage
 */
@Slf4j
public class PortfolioExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String SAMPLE_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302"; // 1inch token contract
    
    public static void main(String[] args) {
        PortfolioExample example = new PortfolioExample();
        
        try {
            log.info("=== Running Portfolio API Examples ===");
            example.runGeneralOverviewExample();
            example.runProtocolsDetailsExample();
            example.runTokensDetailsExample();
            example.runServiceInfoExample();
            
        } catch (Exception e) {
            log.error("Portfolio example failed", e);
        }
    }

    /**
     * Demonstrates general portfolio overview
     */
    public void runGeneralOverviewExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== General Portfolio Overview Example ===");
            
            // Get general current value
            PortfolioOverviewRequest request = PortfolioOverviewRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1) // Ethereum
                    .build();
            
            Object currentValue = client.portfolio().getGeneralCurrentValue(request);
            log.info("General current value: {}", currentValue);
            
            // Get general P&L
            Object profitAndLoss = client.portfolio().getGeneralProfitAndLoss(request);
            log.info("General profit and loss: {}", profitAndLoss);
        }
    }

    /**
     * Demonstrates protocols details
     */
    public void runProtocolsDetailsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Protocols Details Example ===");
            
            PortfolioDetailsRequest request = PortfolioDetailsRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1) // Ethereum
                    .timerange("1month")
                    .closed(true)
                    .build();
            
            PortfolioProtocolsResponse protocolsDetails = client.portfolio().getProtocolsDetails(request);
            
            log.info("Protocols details:");
            log.info("  Found {} protocols", protocolsDetails.getResult().size());
            
            protocolsDetails.getResult().stream()
                    .limit(3)
                    .forEach(protocol -> log.info("  Protocol: {} - Value: ${}", 
                            protocol.getProtocolName(), protocol.getValueUsd()));
        }
    }

    /**
     * Demonstrates tokens details
     */
    public void runTokensDetailsExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Tokens Details Example ===");
            
            PortfolioDetailsRequest request = PortfolioDetailsRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1) // Ethereum
                    .timerange("1month")
                    .build();
            
            PortfolioTokensResponse tokensDetails = client.portfolio().getTokensDetails(request);
            
            log.info("Tokens details:");
            log.info("  Found {} tokens", tokensDetails.getResult().size());
            
            tokensDetails.getResult().stream()
                    .limit(5)
                    .forEach(token -> log.info("  Token: {} ({}) - Value: ${}, ROI: {}%", 
                            token.getName(), token.getSymbol(), token.getValueUsd(), 
                            token.getRoi() != null ? String.format("%.2f", token.getRoi() * 100) : "N/A"));
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
            
            // Check service availability
            Object availability = client.portfolio().getServiceAvailability();
            log.info("Service availability: {}", availability);
            
            // Get supported chains
            List<Object> supportedChains = client.portfolio().getSupportedChains();
            log.info("Supported chains: {}", supportedChains.size());
            
            // Get supported protocols
            List<Object> supportedProtocols = client.portfolio().getSupportedProtocols();
            log.info("Supported protocols: {}", supportedProtocols.size());
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
            
            PortfolioOverviewRequest request = PortfolioOverviewRequest.builder()
                    .addresses(Arrays.asList(SAMPLE_ADDRESS))
                    .chainId(1)
                    .build();

            // Reactive current value
            client.portfolio().getGeneralCurrentValueRx(request)
                    .doOnSuccess(result -> log.info("Reactive current value: {}", result))
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
}