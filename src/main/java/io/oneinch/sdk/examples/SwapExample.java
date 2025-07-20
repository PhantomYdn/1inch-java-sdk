package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class SwapExample {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private static final String ONEINCH_TOKEN_ADDRESS = "0x111111111117dc0aa78b770fa6a738034120c302";
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    
    public static void main(String[] args) {
        SwapExample example = new SwapExample();
        
        try {
            example.runCompleteSwapFlow();
        } catch (Exception e) {
            log.error("Example failed", e);
        }
    }
    
    public void runCompleteSwapFlow() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Complete Swap Flow Example ===");
            
            String swapAmount = "10000000000000000"; // 0.01 ETH in wei
            
            // Step 1: Get spender address
            log.info("Step 1: Getting spender address...");
            SpenderResponse spender = client.swap().getSpender();
            log.info("Spender address: {}", spender.getAddress());
            
            // Step 2: Check allowance for 1INCH token (if swapping FROM 1INCH)
            log.info("Step 2: Checking allowance...");
            AllowanceRequest allowanceRequest = AllowanceRequest.builder()
                    .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                    .walletAddress(WALLET_ADDRESS)
                    .build();
            
            AllowanceResponse allowance = client.swap().getAllowance(allowanceRequest);
            log.info("Current allowance: {}", allowance.getAllowance());
            
            // Step 3: Approve if necessary (example for 1INCH -> ETH swap)
            if (new BigInteger(allowance.getAllowance()).compareTo(new BigInteger(swapAmount)) < 0) {
                log.info("Step 3: Getting approve transaction data...");
                ApproveTransactionRequest approveRequest = ApproveTransactionRequest.builder()
                        .tokenAddress(ONEINCH_TOKEN_ADDRESS)
                        .amount(swapAmount)
                        .build();
                
                ApproveCallDataResponse approveData = client.swap().getApproveTransaction(approveRequest);
                log.info("Approve transaction data: {}", approveData.getData());
                log.info("Gas price: {}", approveData.getGasPrice());
                log.info("To: {}", approveData.getTo());
                log.info("Value: {}", approveData.getValue());
                
                // At this point, you would execute the approve transaction
                log.info("Execute the approve transaction before proceeding to swap...");
            }
            
            // Step 4: Get quote
            log.info("Step 4: Getting quote...");
            QuoteRequest quoteRequest = QuoteRequest.builder()
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
            
            if (quote.getSrcToken() != null) {
                log.info("Source token: {} ({})", quote.getSrcToken().getSymbol(), quote.getSrcToken().getName());
            }
            if (quote.getDstToken() != null) {
                log.info("Destination token: {} ({})", quote.getDstToken().getSymbol(), quote.getDstToken().getName());
            }
            
            // Step 5: Execute swap
            log.info("Step 5: Getting swap transaction data...");
            SwapRequest swapRequest = SwapRequest.builder()
                    .src(ETH_ADDRESS)
                    .dst(ONEINCH_TOKEN_ADDRESS)
                    .amount(swapAmount)
                    .from(WALLET_ADDRESS)
                    .origin(WALLET_ADDRESS)
                    .slippage(1.0) // 1% slippage
                    .includeTokensInfo(true)
                    .includeProtocols(true)
                    .includeGas(true)
                    .build();
            
            SwapResponse swap = client.swap().getSwap(swapRequest);
            log.info("Swap transaction data:");
            log.info("  From: {}", swap.getTx().getFrom());
            log.info("  To: {}", swap.getTx().getTo());
            log.info("  Data: {}", swap.getTx().getData().substring(0, 50) + "...");
            log.info("  Value: {}", swap.getTx().getValue());
            log.info("  Gas Price: {}", swap.getTx().getGasPrice());
            log.info("  Gas Limit: {}", swap.getTx().getGas());
            
            log.info("Expected output amount: {}", swap.getDstAmount());
            
            // At this point, you would execute the swap transaction
            log.info("Execute the swap transaction to complete the swap...");
            
            log.info("=== Complete Swap Flow Example Finished ===");
        }
    }
    
    public void runAsyncExample() throws Exception {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Async Example ===");
            
            QuoteRequest quoteRequest = QuoteRequest.builder()
                    .src(ETH_ADDRESS)
                    .dst(ONEINCH_TOKEN_ADDRESS)
                    .amount("10000000000000000")
                    .build();
            
            // Async quote
            client.swap().getQuoteAsync(quoteRequest)
                    .thenAccept(quote -> {
                        log.info("Async quote result: {}", quote.getDstAmount());
                    })
                    .join();
            
            // Async spender
            client.swap().getSpenderAsync()
                    .thenAccept(spender -> {
                        log.info("Async spender result: {}", spender.getAddress());
                    })
                    .join();
            
            log.info("=== Async Example Finished ===");
        }
    }
    
    public void runErrorHandlingExample() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(API_KEY)
                .build()) {
            
            log.info("=== Starting Error Handling Example ===");
            
            // Invalid quote request to trigger an error
            QuoteRequest invalidRequest = QuoteRequest.builder()
                    .src("invalid_address")
                    .dst("invalid_address")
                    .amount("0")
                    .build();
            
            try {
                client.swap().getQuote(invalidRequest);
            } catch (OneInchApiException e) {
                log.error("API Error caught:");
                log.error("  Error: {}", e.getError());
                log.error("  Description: {}", e.getDescription());
                log.error("  Status Code: {}", e.getStatusCode());
                log.error("  Request ID: {}", e.getRequestId());
                if (e.getMeta() != null) {
                    e.getMeta().forEach(meta -> 
                        log.error("  Meta: {} = {}", meta.getType(), meta.getValue())
                    );
                }
            } catch (OneInchException e) {
                log.error("SDK Error: {}", e.getMessage());
            }
            
            log.info("=== Error Handling Example Finished ===");
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
    }
}