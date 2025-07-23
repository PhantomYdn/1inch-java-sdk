package io.oneinch.sdk.examples;

import java.math.BigInteger;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.QuoteRequest;
import io.oneinch.sdk.model.QuoteResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuickStartExample {

    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY_HERE";
        
        log.info("=== Reactive Approach ===");
        reactiveExample(apiKey);
        
        log.info("=== Synchronous Approach ===");
        synchronousExample(apiKey);
    }
    
    /**
     * Demonstrates the modern reactive approach using RxJava
     */
    public static void reactiveExample(String apiKey) {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(apiKey)
                .build()) {
            
            // Get a quote for swapping ETH to 1INCH (reactive)
            QuoteRequest quoteRequest = QuoteRequest.builder()
                    .chainId(1)  // Ethereum
                    .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                    .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
                    .amount(new BigInteger("10000000000000000"))  // 0.01 ETH in wei
                    .includeTokensInfo(true)
                    .build();

            // Reactive approach with proper error handling
            client.swap().getQuoteRx(quoteRequest)
                    .doOnSuccess(quote -> {
                        log.info("Reactive: Expected output: {} {} tokens", 
                                quote.getDstAmount(),
                                quote.getDstToken() != null ? quote.getDstToken().getSymbol() : "");
                    })
                    .doOnError(error -> log.error("Reactive: Error getting quote", error))
                    .subscribe();
                    
            // Small delay to let async operation complete
            Thread.sleep(1000);
            
        } catch (Exception e) {
            log.error("Reactive example failed", e);
        }
    }
    
    /**
     * Demonstrates the synchronous approach
     */
    public static void synchronousExample(String apiKey) {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(apiKey)
                .build()) {
            
            // Get a quote for swapping ETH to 1INCH (synchronous)
            QuoteRequest quoteRequest = QuoteRequest.builder()
                    .chainId(1)  // Ethereum
                    .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                    .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
                    .amount(new BigInteger("10000000000000000"))  // 0.01 ETH in wei
                    .build();

            QuoteResponse quote = client.swap().getQuote(quoteRequest);
            log.info("Synchronous: Expected output: {} 1INCH tokens", quote.getDstAmount());
            
        } catch (OneInchException e) {
            log.error("Synchronous: Error getting quote: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Synchronous: Unexpected error", e);
        }
    }
}